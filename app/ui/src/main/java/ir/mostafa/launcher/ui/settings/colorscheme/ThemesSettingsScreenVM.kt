package ir.mostafa.launcher.ui.settings.colorscheme

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.mostafa.launcher.ktx.tryStartActivity
import ir.mostafa.launcher.preferences.ThemeDescriptor
import ir.mostafa.launcher.preferences.ui.UiSettings
import ir.mostafa.launcher.themes.BlackAndWhiteThemeId
import ir.mostafa.launcher.themes.DefaultThemeId
import ir.mostafa.launcher.themes.Theme
import ir.mostafa.launcher.themes.ThemeRepository
import ir.mostafa.launcher.themes.toJson
import ir.mostafa.launcher.ui.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.util.UUID

class ThemesSettingsScreenVM : ViewModel(), KoinComponent {

    private val themeRepository: ThemeRepository by inject()
    private val uiSettings: UiSettings by inject()

    val selectedTheme = uiSettings.theme.map {
        when(it) {
            ThemeDescriptor.Default -> DefaultThemeId
            ThemeDescriptor.BlackAndWhite -> BlackAndWhiteThemeId
            is ThemeDescriptor.Custom -> UUID.fromString(it.id)
        }
    }
    val themes: Flow<List<Theme>> = themeRepository.getThemes()

    fun getTheme(id: UUID): Flow<Theme?> {
        return themeRepository.getTheme(id)
    }

    fun updateTheme(theme: Theme) {
        themeRepository.updateTheme(theme)
    }

    fun selectTheme(theme: Theme) {
        uiSettings.setTheme(when(theme.id) {
            DefaultThemeId -> ThemeDescriptor.Default
            BlackAndWhiteThemeId -> ThemeDescriptor.BlackAndWhite
            else -> ThemeDescriptor.Custom(theme.id.toString())
        })
    }

    fun duplicate(theme: Theme) {
        themeRepository.createTheme(theme.copy(id = UUID.randomUUID()))
    }

    fun delete(theme: Theme) {
        themeRepository.deleteTheme(theme)
    }

    fun exportTheme(context: Context, theme: Theme) {
        viewModelScope.launch {
            val file = withContext(Dispatchers.IO) {
                val file = File(context.cacheDir, "${theme.name}.kvtheme")
                file.writeText(theme.toJson())
                file
            }
            context.tryStartActivity(Intent().apply {
                action = Intent.ACTION_SEND
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName + ".fileprovider",
                    file
                ))
            }.let { Intent.createChooser(it, null) })
        }
    }

    fun createNew(context: Context) {
        themeRepository.createTheme(
            Theme(
                id = UUID.randomUUID(),
                name = context.getString(R.string.new_color_scheme_name)
            )
        )
    }
}