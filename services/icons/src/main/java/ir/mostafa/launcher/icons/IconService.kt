package ir.mostafa.launcher.icons

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.util.Log
import android.util.LruCache
import ir.mostafa.launcher.data.customattrs.AdaptifiedLegacyIcon
import ir.mostafa.launcher.data.customattrs.CustomAttributesRepository
import ir.mostafa.launcher.data.customattrs.CustomIcon
import ir.mostafa.launcher.data.customattrs.CustomIconPackIcon
import ir.mostafa.launcher.data.customattrs.LegacyCustomIconPackIcon
import ir.mostafa.launcher.data.customattrs.CustomThemedIcon
import ir.mostafa.launcher.data.customattrs.DefaultPlaceholderIcon
import ir.mostafa.launcher.data.customattrs.ForceThemedIcon
import ir.mostafa.launcher.data.customattrs.UnmodifiedSystemDefaultIcon
import ir.mostafa.launcher.icons.providers.CalendarIconProvider
import ir.mostafa.launcher.icons.providers.CompatIconProvider
import ir.mostafa.launcher.icons.providers.CustomIconPackIconProvider
import ir.mostafa.launcher.icons.providers.LegacyCustomIconPackIconProvider
import ir.mostafa.launcher.icons.providers.CustomThemedIconProvider
import ir.mostafa.launcher.icons.providers.DynamicClockIconProvider
import ir.mostafa.launcher.icons.providers.IconPackIconProvider
import ir.mostafa.launcher.icons.providers.IconProvider
import ir.mostafa.launcher.icons.providers.PlaceholderIconProvider
import ir.mostafa.launcher.icons.providers.SystemIconProvider
import ir.mostafa.launcher.icons.providers.ThemedPlaceholderIconProvider
import ir.mostafa.launcher.icons.providers.getFirstIcon
import ir.mostafa.launcher.icons.transformations.ForceThemedIconTransformation
import ir.mostafa.launcher.icons.transformations.LauncherIconTransformation
import ir.mostafa.launcher.icons.transformations.LegacyToAdaptiveTransformation
import ir.mostafa.launcher.icons.transformations.transform
import ir.mostafa.launcher.ktx.isAtLeastApiLevel
import ir.mostafa.launcher.preferences.ui.IconSettings
import ir.mostafa.launcher.search.Application
import ir.mostafa.launcher.search.SavableSearchable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class IconService(
    val context: Context,
    private val iconPackManager: IconPackManager,
    private val settings: IconSettings,
    private val customAttributesRepository: CustomAttributesRepository,
) {

    private val appReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            requestIconPackListUpdate()
        }
    }

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    private val cache = LruCache<String, LauncherIcon>(200)

    private var iconProviders: MutableStateFlow<List<IconProvider>> = MutableStateFlow(listOf())

    /**
     * Signal that installed icon packs have been updated. Force a reload of all icons.
     */
    private val iconPacksUpdated = MutableSharedFlow<Unit>(1)

    private var transformations: MutableStateFlow<List<LauncherIconTransformation>> =
        MutableStateFlow(
            listOf()
        )

    init {
        requestIconPackListUpdate()
        context.registerReceiver(appReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_MY_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        })

        iconPacksUpdated.tryEmit(Unit)

        scope.launch {
            settings.distinctUntilChanged().collectLatest { settings ->
                iconPacksUpdated.collectLatest {
                    val fallbackProvider = if (settings.themedIcons) {
                        ThemedPlaceholderIconProvider(context)
                    } else {
                        PlaceholderIconProvider(context)
                    }
                    val providers = mutableListOf<IconProvider>()

                    if (!settings.iconPack.isNullOrBlank()) {
                        val pack = iconPackManager.getIconPack(settings.iconPack!!)
                        if (pack != null) {
                            providers.add(
                                IconPackIconProvider(
                                    context,
                                    pack,
                                    iconPackManager,
                                    settings.themedIcons,
                                )
                            )
                        } else {
                        }
                    }
                    providers.add(DynamicClockIconProvider(context, settings.themedIcons))
                    providers.add(CalendarIconProvider(context, settings.themedIcons))
                    if (!isAtLeastApiLevel(33)) {
                        providers.add(CompatIconProvider(context, settings.themedIcons))
                    }
                    providers.add(SystemIconProvider(context, settings.themedIcons))
                    providers.add(fallbackProvider)
                    cache.evictAll()

                    val transformations = mutableListOf<LauncherIconTransformation>()

                    if (settings.adaptify) transformations.add(LegacyToAdaptiveTransformation())
                    if (settings.themedIcons && settings.forceThemed) transformations.add(
                        ForceThemedIconTransformation()
                    )

                    iconProviders.value = providers
                    this@IconService.transformations.value = transformations
                }
            }
        }
    }


    fun getIcon(searchable: SavableSearchable, size: Int): Flow<LauncherIcon?> {
        if (searchable is Application && searchable.isPrivate) {
            return transformations.map {
                searchable.getPlaceholderIcon(context).transform(it)
            }
        }

        val customIcon = customAttributesRepository.getCustomIcon(searchable)
        return combine(iconProviders, transformations, customIcon) { providers, transformations, ci ->
            var icon = cache.get(searchable.key + ci.hashCode() + providers.hashCode() + transformations.hashCode())
            if (icon != null) {
                return@combine icon
            }

            val provs = if (ci != null) getProviders(ci) + providers else providers
            val transforms = getTransformations(ci) ?: transformations

            icon = provs.getFirstIcon(searchable, size)

            if (icon != null) {
                icon = icon.transform(transforms)

                cache.put(searchable.key + ci.hashCode() + providers.hashCode() + transformations.hashCode(), icon)
            }
            return@combine icon
        }
    }

    private fun getProviders(customIcon: CustomIcon?): List<IconProvider> {
        if (customIcon is UnmodifiedSystemDefaultIcon) {
            return listOf(
                SystemIconProvider(context, false)
            )
        }
        if (customIcon is CustomIconPackIcon) {
            return listOf(
                CustomIconPackIconProvider(
                    customIcon,
                    iconPackManager
                )
            )
        }
        if (customIcon is LegacyCustomIconPackIcon) {
            return listOf(
                LegacyCustomIconPackIconProvider(
                    customIcon,
                    iconPackManager
                )
            )
        }
        if (customIcon is CustomThemedIcon) {
            return listOf(
                CustomThemedIconProvider(
                    customIcon,
                    iconPackManager
                )
            )
        }
        if (customIcon is DefaultPlaceholderIcon) {
            return iconProviders.value.lastOrNull()?.let { listOf(it) } ?: emptyList()
        }
        return emptyList()
    }

    private fun getTransformations(customIcon: CustomIcon?): List<LauncherIconTransformation>? {
        customIcon ?: return null
        if (customIcon is AdaptifiedLegacyIcon) {
            return listOf(
                LegacyToAdaptiveTransformation(
                    foregroundScale = customIcon.fgScale,
                    backgroundColor = customIcon.bgColor
                )
            )
        }
        if (customIcon is ForceThemedIcon) {
            return listOf(
                ForceThemedIconTransformation()
            )
        }
        if (customIcon is UnmodifiedSystemDefaultIcon) {
            return emptyList()
        }
        return null
    }


    fun requestIconPackListUpdate() {
        scope.launch {
            iconPackManager.updateIconPacks().also {
                if (it)iconPacksUpdated.tryEmit(Unit)
            }
        }
    }

    fun reinstallAllIconPacks() {
        scope.launch {
            iconPackManager.updateIconPacks(forceReinstall = true)
            iconPacksUpdated.tryEmit(Unit)
        }
    }

    fun getInstalledIconPacks(): Flow<List<IconPack>> {
        return iconPackManager.getInstalledIconPacks()
    }

    suspend fun getCustomIconSuggestions(
        searchable: SavableSearchable,
        size: Int
    ): List<CustomIconWithPreview> {
        val suggestions = mutableListOf<CustomIconWithPreview>()

        val rawIcon = iconProviders.first().getFirstIcon(searchable, size) ?: return emptyList()

        val defaultTransformations = transformations.first()

        val transformationOptions = mutableListOf<CustomIcon>(UnmodifiedSystemDefaultIcon)

        if (rawIcon is StaticLauncherIcon && rawIcon.backgroundLayer is TransparentLayer) {
            // Legacy icons that simply fill the entire canvas
            transformationOptions.add(
                AdaptifiedLegacyIcon(
                    fgScale = 1f,
                    bgColor = 1
                )
            )
            // 48x48 with 5px padding used to be the default icon size for icons generated by
            // the Android Studio asset generator. Upscale these icons to remove that padding.

            transformationOptions.add(
                AdaptifiedLegacyIcon(
                    fgScale = 48f / 38f,
                    bgColor = 1
                )
            )

            // Android 7.1 round icons (48x48 circle with 1px padding)
            transformationOptions.add(
                AdaptifiedLegacyIcon(
                    fgScale = 48f / 44f,
                    bgColor = 1
                )
            )
            transformationOptions.add(
                AdaptifiedLegacyIcon(
                    fgScale = 0.7f,
                    bgColor = 0
                )
            )
            transformationOptions.add(
                AdaptifiedLegacyIcon(
                    fgScale = 0.7f,
                    bgColor = Color.WHITE,
                )
            )
        }

        val providerOptions = mutableListOf<CustomIcon>()

        if (searchable is Application) {
            val iconPackIcons = iconPackManager.getAllIconPackIcons(
                searchable.componentName
            )

            providerOptions.addAll(
                iconPackIcons.map {
                    val ent = it.toDatabaseEntity()
                    CustomIconPackIcon(
                        iconPackPackage = it.iconPack,
                        type = ent.type,
                        drawable = ent.drawable,
                        extras = ent.extras,
                        allowThemed = it.themed,
                    )
                }
            )
            transformationOptions.add(
                ForceThemedIcon
            )
        } else {
            transformationOptions.add(
                ForceThemedIcon
            )
        }

        providerOptions.add(DefaultPlaceholderIcon)

        suggestions.addAll(
            transformationOptions.map {
                val transformations = getTransformations(it) ?: defaultTransformations
                val providers = getProviders(it)

                val icon = providers.getFirstIcon(searchable, size) ?: rawIcon

                CustomIconWithPreview(
                    preview = icon.transform(transformations),
                    customIcon = it,
                )

            }
        )

        suggestions.addAll(
            providerOptions.mapNotNull {
                val providers = getProviders(it)

                val icon = providers.getFirstIcon(searchable, size) ?: return@mapNotNull null

                CustomIconWithPreview(
                    preview = icon.transform(defaultTransformations),
                    customIcon = it,
                )

            }
        )

        return suggestions

    }

    suspend fun getUncustomizedDefaultIcon(
        searchable: SavableSearchable,
        size: Int
    ): CustomIconWithPreview? {
        val icon = iconProviders.first().getFirstIcon(searchable, size)
            ?.transform(transformations.first()) ?: return null
        return CustomIconWithPreview(
            customIcon = null,
            preview = icon
        )
    }

    suspend fun searchCustomIcons(query: String, iconPack: IconPack?): List<CustomIconWithPreview> {
        val transformations = this.transformations.first()
        val iconPackIcons = iconPackManager.searchIconPackIcon(query, iconPack).flatMap {
            val themedIcon = if (it.themed) {
                iconPackManager.getIcon(it.iconPack, it, true)
                    ?.transform(transformations)
            } else null
            val unthemedIcon = iconPackManager.getIcon(it.iconPack, it, false)
                ?.transform(transformations)

            buildList {
                val ent = it.toDatabaseEntity()
                if (unthemedIcon != null) {
                    add(CustomIconWithPreview(
                        customIcon = CustomIconPackIcon(
                            iconPackPackage = it.iconPack,
                            type = ent.type,
                            drawable = ent.drawable,
                            extras = ent.extras,
                            allowThemed = false,
                        ),
                        preview = unthemedIcon
                    ))
                }
                if (themedIcon != null) {
                    add(CustomIconWithPreview(
                        customIcon = CustomIconPackIcon(
                            iconPackPackage = it.iconPack,
                            type = ent.type,
                            drawable = ent.drawable,
                            extras = ent.extras,
                            allowThemed = true,
                        ),
                        preview = themedIcon
                    ))
                }
            }
        }

        return iconPackIcons
    }

    fun setCustomIcon(searchable: SavableSearchable, icon: CustomIcon?) {
        customAttributesRepository.setCustomIcon(searchable, icon)
    }

}

data class CustomIconWithPreview(
    val preview: LauncherIcon,
    val customIcon: CustomIcon?,
)