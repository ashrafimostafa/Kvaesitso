package ir.mostafa.launcher.ui.base

import android.appwidget.AppWidgetHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import ir.mostafa.launcher.crashreporter.CrashReporter
import kotlinx.coroutines.awaitCancellation

val LocalAppWidgetHost =
    staticCompositionLocalOf<AppWidgetHost> { throw IllegalStateException("AppWidgetHost is not provided") }

@Composable
fun ProvideAppWidgetHost(
    content: @Composable () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val widgetHost = remember { AppWidgetHost(context.applicationContext, 44203) }
    LaunchedEffect(null) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            widgetHost.startListening()
            try {
                awaitCancellation()
            } finally {
                try {
                    widgetHost.stopListening()
                } catch (e: Exception) {
                    CrashReporter.logException(e)
                }
            }
        }
    }
    CompositionLocalProvider(LocalAppWidgetHost provides widgetHost, content = content)
}