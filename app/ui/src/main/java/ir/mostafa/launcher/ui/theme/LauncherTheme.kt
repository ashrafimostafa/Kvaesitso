package ir.mostafa.launcher.ui.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import ir.mostafa.launcher.preferences.Font
import ir.mostafa.launcher.preferences.SurfaceShape
import ir.mostafa.launcher.preferences.ui.UiSettings
import ir.mostafa.launcher.themes.ThemeRepository
import ir.mostafa.launcher.ui.locals.LocalDarkTheme
import ir.mostafa.launcher.ui.theme.colorscheme.*
import ir.mostafa.launcher.ui.theme.typography.DefaultTypography
import ir.mostafa.launcher.ui.theme.typography.getDeviceDefaultTypography
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.inject
import ir.mostafa.launcher.preferences.ColorScheme as ColorSchemePref


@Composable
fun LauncherTheme(
    content: @Composable () -> Unit
) {

    val context = LocalContext.current
    val uiSettings: UiSettings by inject()
    val themeRepository: ThemeRepository by inject()

    val theme by remember {
        uiSettings.theme.flatMapLatest {
            themeRepository.getThemeOrDefault(it)
        }
    }.collectAsState(themeRepository.getDefaultTheme())

    val colorSchemePref by remember { uiSettings.colorScheme }.collectAsState(
        ColorSchemePref.System
    )
    val darkTheme =
        colorSchemePref == ColorSchemePref.Dark || colorSchemePref == ColorSchemePref.System && isSystemInDarkTheme()

    val cornerRadius by remember {
        uiSettings.cardStyle.map {
            it.cornerRadius.dp
        }
    }.collectAsState(8.dp)

    val baseShape by remember {
        uiSettings.cardStyle.map {
            when (it.shape) {
                SurfaceShape.Cut -> CutCornerShape(0f)
                else -> RoundedCornerShape(0f)
            }
        }
    }.collectAsState(RoundedCornerShape(0f))

    val colorScheme = if (darkTheme) {
        darkColorSchemeOf(theme)
    } else {
        lightColorSchemeOf(theme)
    }

    val font by remember { uiSettings.font }.collectAsState(
        Font.Outfit
    )

    val typography = remember(font) {
        getTypography(context, font)
    }

    CompositionLocalProvider(
        LocalDarkTheme provides darkTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = Shapes(
                extraSmall = baseShape.copy(CornerSize(cornerRadius / 3f)),
                small = baseShape.copy(CornerSize(cornerRadius / 3f * 2f)),
                medium = baseShape.copy(CornerSize(cornerRadius)),
                large = baseShape.copy(CornerSize((cornerRadius / 3f * 4f).coerceAtMost(16.dp))),
                extraLarge = baseShape.copy(CornerSize((cornerRadius / 3f * 7f).coerceAtMost(28.dp))),
            ),
            content = content
        )
    }
}

fun getTypography(context: Context, font: Font?): Typography {
    return when (font) {
        Font.System -> getDeviceDefaultTypography(context)
        else -> DefaultTypography
    }
}