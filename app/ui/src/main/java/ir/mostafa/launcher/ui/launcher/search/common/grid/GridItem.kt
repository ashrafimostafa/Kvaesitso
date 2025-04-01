package ir.mostafa.launcher.ui.launcher.search.common.grid

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.mostafa.launcher.search.AppShortcut
import ir.mostafa.launcher.search.Application
import ir.mostafa.launcher.search.Article
import ir.mostafa.launcher.search.CalendarEvent
import ir.mostafa.launcher.search.Contact
import ir.mostafa.launcher.search.File
import ir.mostafa.launcher.search.Location
import ir.mostafa.launcher.search.SavableSearchable
import ir.mostafa.launcher.search.Searchable
import ir.mostafa.launcher.search.Website
import ir.mostafa.launcher.ui.component.LauncherCard
import ir.mostafa.launcher.ui.component.LocalIconShape
import ir.mostafa.launcher.ui.component.ShapedLauncherIcon
import ir.mostafa.launcher.ui.ktx.toPixels
import ir.mostafa.launcher.ui.launcher.search.apps.AppItemGridPopup
import ir.mostafa.launcher.ui.launcher.search.calendar.CalendarItemGridPopup
import ir.mostafa.launcher.ui.launcher.search.common.SearchableItemVM
import ir.mostafa.launcher.ui.launcher.search.contacts.ContactItemGridPopup
import ir.mostafa.launcher.ui.launcher.search.files.FileItemGridPopup
import ir.mostafa.launcher.ui.launcher.search.listItemViewModel
import ir.mostafa.launcher.ui.launcher.search.location.LocationItemGridPopup
import ir.mostafa.launcher.ui.launcher.search.shortcut.ShortcutItemGridPopup
import ir.mostafa.launcher.ui.launcher.search.website.WebsiteItemGridPopup
import ir.mostafa.launcher.ui.launcher.search.wikipedia.ArticleItemGridPopup
import ir.mostafa.launcher.ui.launcher.transitions.EnterHomeTransitionParams
import ir.mostafa.launcher.ui.launcher.transitions.HandleEnterHomeTransition
import ir.mostafa.launcher.ui.locals.LocalGridSettings
import ir.mostafa.launcher.ui.locals.LocalWindowSize
import ir.mostafa.launcher.ui.overlays.Overlay
import kotlin.math.pow


@Composable
fun GridItem(
    modifier: Modifier = Modifier,
    item: SavableSearchable,
    showLabels: Boolean = true,
    labelMaxLines: Int = 1,
    highlight: Boolean = false
) {
    val viewModel: SearchableItemVM = listItemViewModel(key = "search-${item.key}")
    val iconSize = LocalGridSettings.current.iconSize.dp.toPixels()

    LaunchedEffect(item, iconSize) {
        viewModel.init(item, iconSize.toInt())
    }

    val context = LocalContext.current

    var showPopup by remember(item.key) { mutableStateOf(false) }
    var bounds by remember { mutableStateOf(Rect.Zero) }

    val launchOnPress = !item.preferDetailsOverLaunch
    val hapticFeedback = LocalHapticFeedback.current

    LaunchedEffect(showPopup) {
        if (showPopup) viewModel.requestUpdatedSearchable(context)
    }

    Column(
        modifier = modifier
            .combinedClickable(
                onClick = {
                    if (!launchOnPress || !viewModel.launch(context, bounds)) {
                        showPopup = true
                    }
                },
                onLongClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    showPopup = true
                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ) then if (!showLabels) Modifier.aspectRatio(1f) else Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val badge by viewModel.badge.collectAsStateWithLifecycle()
        val icon by viewModel.icon.collectAsStateWithLifecycle()


        val windowSize = LocalWindowSize.current

        if (item is Application) {
            HandleEnterHomeTransition {
                val cn = item.componentName
                if (
                    it.componentName == cn &&
                    bounds.right > 0f && bounds.left < windowSize.width &&
                    bounds.bottom > 0f && bounds.top < windowSize.height
                ) {
                    return@HandleEnterHomeTransition EnterHomeTransitionParams(
                        bounds
                    ) { _, _ ->
                        ShapedLauncherIcon(
                            size = LocalGridSettings.current.iconSize.dp,
                            icon = { icon })
                    }
                }
                return@HandleEnterHomeTransition null
            }
        }

        val iconShape = LocalIconShape.current

        Box(
            modifier = if (highlight) {
                Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        iconShape
                    )
            } else Modifier then if (showLabels) Modifier else Modifier
                .semantics {
                    contentDescription = item.label
                },
        ) {
            ShapedLauncherIcon(
                modifier = Modifier
                    .padding(4.dp)
                    .onGloballyPositioned {
                        bounds = it.boundsInWindow()
                    } then
                        if (highlight) Modifier.background(
                            MaterialTheme.colorScheme.surface,
                            iconShape
                        )
                        else Modifier,
                size = LocalGridSettings.current.iconSize.dp,
                badge = { badge },
                icon = { icon },
            )
        }
        if (showLabels) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                text = item.labelOverride ?: item.label,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                maxLines = labelMaxLines,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        if (showPopup) {
            ItemPopup(origin = bounds, searchable = item, onDismissRequest = { showPopup = false })
        }
    }
}

@Composable
fun ItemPopup(origin: Rect, searchable: Searchable, onDismissRequest: () -> Unit) {
    val show = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    val animationProgress = remember {
        Animatable(0f)
    }
    LaunchedEffect(show.targetState) {
        if (!show.targetState) {
            animationProgress.animateTo(
                0f, spring(
                    Spring.DampingRatioNoBouncy,
                    Spring.StiffnessMediumLow,
                )
            )
            onDismissRequest()
        } else {
            animationProgress.animateTo(
                1f, spring(
                    Spring.DampingRatioLowBouncy,
                    Spring.StiffnessMediumLow,
                )
            )
        }
    }
    BackHandler {
        show.targetState = false
    }

    Overlay {
        Box(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.scrim.copy(
                        alpha = 0.32f * animationProgress.value.coerceIn(
                            0f,
                            1f
                        )
                    )
                )
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .padding(horizontal = 8.dp)
                .then(
                    if (show.targetState) {
                        Modifier.pointerInput(Unit) {
                            detectTapGestures(onPress = {
                                show.targetState = false
                            })
                        }
                    } else {
                        Modifier
                    }
                )
        ) {
            LauncherCard(
                elevation = 8.dp * animationProgress.value,
                backgroundOpacity = 1f,
                modifier = Modifier
                    .placeOverlay(
                        origin.translate(
                            -8.dp.toPixels(),
                            -WindowInsets.systemBars.union(WindowInsets.ime)
                                .getTop(LocalDensity.current).toFloat()
                        ),
                        animationProgress.value
                    )
            ) {
                when (searchable) {
                    is Application -> {
                        AppItemGridPopup(
                            app = searchable,
                            show = show,
                            animationProgress = animationProgress.value,
                            origin = origin,
                            onDismiss = {
                                show.targetState = false
                            }
                        )
                    }

                    is Website -> {
                        WebsiteItemGridPopup(
                            website = searchable,
                            show = show,
                            animationProgress = animationProgress.value,
                            origin = origin,
                            onDismiss = {
                                show.targetState = false
                            }
                        )
                    }

                    is Article -> {
                        ArticleItemGridPopup(
                            article = searchable,
                            show = show,
                            animationProgress = animationProgress.value,
                            origin = origin,
                            onDismiss = {
                                show.targetState = false
                            }
                        )
                    }

                    is Contact -> {
                        ContactItemGridPopup(
                            contact = searchable,
                            show = show,
                            animationProgress = animationProgress.value,
                            origin = origin,
                            onDismiss = {
                                show.targetState = false
                            }
                        )
                    }

                    is File -> {
                        FileItemGridPopup(
                            file = searchable,
                            show = show,
                            animationProgress = animationProgress.value,
                            origin = origin,
                            onDismiss = {
                                show.targetState = false
                            }
                        )
                    }

                    is CalendarEvent -> {
                        CalendarItemGridPopup(
                            calendar = searchable,
                            show = show,
                            animationProgress = animationProgress.value,
                            origin = origin,
                            onDismiss = {
                                show.targetState = false
                            }
                        )
                    }

                    is AppShortcut -> {
                        ShortcutItemGridPopup(
                            shortcut = searchable,
                            show = show,
                            animationProgress = animationProgress.value,
                            origin = origin,
                            onDismiss = {
                                show.targetState = false
                            }
                        )
                    }

                    is Location -> {
                        LocationItemGridPopup(
                            location = searchable,
                            show = show,
                            animationProgress = animationProgress.value,
                            origin = origin,
                            onDismiss = {
                                show.targetState = false
                            }
                        )
                    }
                }
            }
        }
    }

}

private fun Modifier.placeOverlay(
    origin: Rect,
    animationProgress: Float,
): Modifier {
    return layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable.placeRelative(
                (
                        lerp(
                            origin.center.x,
                            constraints.maxWidth / 2f,
                            ((placeable.width.toFloat() - origin.width) / (constraints.maxWidth.toFloat() - origin.width))
                        ) - placeable.width / 2f).toInt(),
                lerp(
                    origin.top,
                    (origin.center.y - placeable.height / 2f).coerceIn(
                        0f,
                        constraints.maxHeight.toFloat() - placeable.height.toFloat(),
                    ),
                    animationProgress.pow(2f)
                ).toInt()
            )
        }
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + fraction * (stop - start)
}

private fun lerp(start: Int, stop: Int, fraction: Float): Int {
    return start + (fraction * (stop - start)).toInt()
}