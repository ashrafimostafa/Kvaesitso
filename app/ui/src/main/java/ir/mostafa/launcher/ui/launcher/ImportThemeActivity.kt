package ir.mostafa.launcher.ui.launcher

import android.os.Bundle
import androidx.activity.compose.setContent
import ir.mostafa.launcher.ui.base.BaseActivity
import ir.mostafa.launcher.ui.base.ProvideSettings
import ir.mostafa.launcher.ui.common.ImportThemeSheet
import ir.mostafa.launcher.ui.overlays.OverlayHost
import ir.mostafa.launcher.ui.theme.LauncherTheme

class ImportThemeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent.data ?: return finish()

        setContent {
            LauncherTheme {
                ProvideSettings {
                    OverlayHost {
                        ImportThemeSheet(
                            onDismiss = { finish() },
                            uri = uri,
                        )
                    }
                }
            }
        }
    }
}