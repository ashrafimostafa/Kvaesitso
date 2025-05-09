package ir.mostafa.launcher.ui.settings.integrations

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mostafa.launcher.icons.Google
import ir.mostafa.launcher.icons.Nextcloud
import ir.mostafa.launcher.icons.Owncloud
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.component.preferences.Preference
import ir.mostafa.launcher.ui.component.preferences.PreferenceScreen
import ir.mostafa.launcher.ui.locals.LocalNavController

@Composable
fun IntegrationsSettingsScreen() {
    val viewModel: IntegrationsSettingsScreenVM = viewModel()
    val navController = LocalNavController.current

    PreferenceScreen(title = stringResource(R.string.preference_screen_integrations)) {
        item {
            Preference(
                title = stringResource(R.string.preference_weather_integration),
                icon = Icons.Rounded.LightMode,
                onClick = {
                    navController?.navigate("settings/integrations/weather")
                }
            )
            Preference(
                title = stringResource(R.string.preference_media_integration),
                icon = Icons.Rounded.PlayCircleOutline,
                onClick = {
                    navController?.navigate("settings/integrations/media")
                }
            )
            Preference(
                title = stringResource(R.string.preference_nextcloud),
                icon = Icons.Rounded.Nextcloud,
                onClick = {
                    navController?.navigate("settings/integrations/nextcloud")
                }
            )
            Preference(
                title = stringResource(R.string.preference_owncloud),
                icon = Icons.Rounded.Owncloud,
                onClick = {
                    navController?.navigate("settings/integrations/owncloud")
                }
            )
            if (viewModel.isGoogleAvailable) {
                Preference(
                    title = stringResource(R.string.preference_google),
                    icon = Icons.Rounded.Google,
                    onClick = {
                        navController?.navigate("settings/integrations/google")
                    }
                )
            }
        }
    }
}

@Composable
fun GoogleSigninButton(
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.height(40.dp),
        shadowElevation = 1.dp,
        color = Color.White,
        shape = RoundedCornerShape(2.dp),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(id = R.drawable.ic_google_g),
                    contentDescription = null
                )
            }
            Text(
                modifier = Modifier.padding(start = 13.dp, end = 8.dp),
                text = stringResource(id = R.string.preference_google_signin),
                fontFamily = FontFamily.SansSerif,
                color = Color(0f, 0f, 0f, 0.54f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}