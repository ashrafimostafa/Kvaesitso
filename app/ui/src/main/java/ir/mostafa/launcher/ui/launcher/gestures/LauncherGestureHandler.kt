package ir.mostafa.launcher.ui.launcher.gestures

import android.app.WallpaperManager
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mostafa.launcher.preferences.GestureAction
import ir.mostafa.launcher.search.SavableSearchable
import ir.mostafa.launcher.ui.component.FakeSplashScreen
import ir.mostafa.launcher.ui.gestures.Gesture
import ir.mostafa.launcher.ui.gestures.GestureHandler
import ir.mostafa.launcher.ui.gestures.LocalGestureDetector
import ir.mostafa.launcher.ui.ktx.animateTo
import ir.mostafa.launcher.ui.ktx.toPixels
import ir.mostafa.launcher.ui.launcher.GestureState
import ir.mostafa.launcher.ui.launcher.LauncherScaffoldVM
import ir.mostafa.launcher.ui.launcher.sheets.FailedGestureSheet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

/**
 * Handles gestures on the launcher according to the user's settings.
 * @param onHomeButtonPress Called when the home button is pressed. Allows the caller to intercept the event.
 * If the function returns true, the event is considered handled and the default action is not performed.
 */
@Composable
fun LauncherGestureHandler(
    onHomeButtonPress: () -> Boolean = { false }
) {
    val context = LocalContext.current
    val wallpaperManager = remember { WallpaperManager.getInstance(context) }
    val gestureDetector = LocalGestureDetector.current

    val viewModel: LauncherScaffoldVM = viewModel()
    val scope = rememberCoroutineScope()

    val gestureState by viewModel.gestureState.collectAsState(GestureState())

    val shouldDetectDoubleTapGesture = gestureState.doubleTapAction !is GestureAction.NoAction

    LaunchedEffect(shouldDetectDoubleTapGesture) {
        gestureDetector.shouldDetectDoubleTaps = shouldDetectDoubleTapGesture
    }

    val windowToken = LocalView.current.windowToken

    var launchingApp by remember { mutableStateOf<SavableSearchable?>(null) }
    var swipeGestureProgress = remember { mutableStateOf(0f) }
    var swipeGestureDirection by remember { mutableStateOf<SwipeDirection?>(null) }

    // Min swipe distance to trigger show the launch app preview
    val swipeStartThreshold = 18.dp.toPixels()
    // Min swipe distance to trigger the action
    val swipeActionThreshold = 150.dp.toPixels()
    GestureHandler(
        detector = gestureDetector,
        onDoubleTap = {
            viewModel.handleGesture(context, Gesture.DoubleTap)
        },
        onLongPress = {
            viewModel.handleGesture(context, Gesture.LongPress)
        },
        onHomeButtonPress = {
            if (onHomeButtonPress()) {
                return@GestureHandler
            }
            viewModel.handleGesture(context, Gesture.HomeButton)
        },
        onDrag = {
            when {
                gestureState.swipeRightApp != null && it.x > swipeStartThreshold && (
                        swipeGestureDirection == SwipeDirection.Right || (swipeGestureDirection == null && it.x.absoluteValue > it.y.absoluteValue * 2f)
                        ) -> {

                    swipeGestureDirection = SwipeDirection.Right
                    swipeGestureProgress.value =
                        ((it.x - swipeStartThreshold) / (swipeActionThreshold - swipeStartThreshold)).coerceIn(
                            0f,
                            2f
                        )
                    launchingApp = gestureState.swipeRightApp
                    return@GestureHandler false
                }

                gestureState.swipeLeftApp != null && it.x < -swipeStartThreshold && (
                        swipeGestureDirection == SwipeDirection.Left || (swipeGestureDirection == null && it.x.absoluteValue > it.y.absoluteValue * 2f)
                        ) -> {

                    swipeGestureDirection = SwipeDirection.Left
                    swipeGestureProgress.value =
                        ((-it.x - swipeStartThreshold) / (swipeActionThreshold - swipeStartThreshold)).coerceIn(
                            0f,
                            2f
                        )
                    launchingApp = gestureState.swipeLeftApp
                    return@GestureHandler false
                }

                gestureState.swipeDownApp != null && it.y > swipeStartThreshold && (
                        swipeGestureDirection == SwipeDirection.Down || (swipeGestureDirection == null && it.y.absoluteValue > it.x.absoluteValue * 2f)
                        ) -> {

                    swipeGestureDirection = SwipeDirection.Down
                    swipeGestureProgress.value =
                        ((it.y - swipeStartThreshold) / (swipeActionThreshold - swipeStartThreshold)).coerceIn(
                            0f,
                            2f
                        )
                    launchingApp = gestureState.swipeDownApp
                    return@GestureHandler false
                }

                else -> {
                    swipeGestureDirection = null
                    swipeGestureProgress.value = 0f
                    launchingApp = null
                }
            }

            return@GestureHandler when {
                it.x > swipeActionThreshold && it.x.absoluteValue > it.y.absoluteValue * 2f -> {
                    viewModel.handleGesture(context, Gesture.SwipeRight)
                }

                it.x < -swipeActionThreshold && it.x.absoluteValue > it.y.absoluteValue * 2f -> {
                    viewModel.handleGesture(context, Gesture.SwipeLeft)
                }

                it.y > swipeActionThreshold && it.y.absoluteValue > it.x.absoluteValue * 2f -> {
                    viewModel.handleGesture(context, Gesture.SwipeDown)
                }

                else -> false
            }
        },
        onDragEnd = {
            if (swipeGestureProgress.value > 0f) {
                scope.launch {
                    val direction = swipeGestureDirection
                    if (swipeGestureProgress.value >= 1f
                        && direction != null
                    ) {
                        swipeGestureProgress.animateTo(2f)
                        viewModel.handleGesture(
                            context,
                            when (direction) {
                                SwipeDirection.Right -> Gesture.SwipeRight
                                SwipeDirection.Left -> Gesture.SwipeLeft
                                SwipeDirection.Down -> Gesture.SwipeDown
                            },
                        )
                        delay(500)
                    } else {
                        swipeGestureProgress.animateTo(0f)
                    }
                    swipeGestureDirection = null
                    swipeGestureProgress.value = 0f
                    launchingApp = null
                }
            }
        },
        onTap = {
            wallpaperManager.sendWallpaperCommand(
                windowToken,
                WallpaperManager.COMMAND_TAP,
                it.x.toInt(),
                it.y.toInt(),
                0,
                null
            )
        }
    )

    if (launchingApp != null) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            FakeSplashScreen(
                modifier = Modifier
                    .offset {
                        val p = swipeGestureProgress.value
                        when (swipeGestureDirection) {
                            SwipeDirection.Right -> IntOffset(
                                (-minWidth.toPx() * (1f - p * 0.5f)).toInt(),
                                0
                            )

                            SwipeDirection.Left -> IntOffset(
                                (minWidth.toPx() * (1f - p * 0.5f)).toInt(),
                                0
                            )

                            SwipeDirection.Down -> IntOffset(
                                0,
                                (-minHeight.toPx() * (1f - p * 0.5f)).toInt()
                            )

                            else -> IntOffset.Zero
                        }
                    }
                    .graphicsLayer {
                        alpha = (swipeGestureProgress.value).coerceAtMost(1f)
                    },
                searchable = launchingApp,
            )
        }
    }


    if (viewModel.failedGestureState != null) {
        FailedGestureSheet(
            failedGesture = viewModel.failedGestureState!!,
            onDismiss = {
                viewModel.dismissGestureFailedSheet()
            }
        )
    }
}

internal enum class SwipeDirection {
    Left,
    Right,
    Down
}