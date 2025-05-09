package ir.mostafa.launcher.ui.settings.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mostafa.launcher.preferences.SurfaceShape
import ir.mostafa.launcher.preferences.ui.CardStyle
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.component.LauncherCard
import ir.mostafa.launcher.ui.component.preferences.ListPreference
import ir.mostafa.launcher.ui.component.preferences.PreferenceCategory
import ir.mostafa.launcher.ui.component.preferences.PreferenceScreen
import ir.mostafa.launcher.ui.component.preferences.SliderPreference

@Composable
fun CardsSettingsScreen() {
    val viewModel: CardsSettingsScreenVM = viewModel()

    val cardStyle by viewModel.cardStyle.collectAsState(CardStyle())

    PreferenceScreen(title = stringResource(R.string.preference_cards)) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondary)
            ) {
                LauncherCard(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
        item {
            PreferenceCategory {
                ListPreference(
                    icon = Icons.Rounded.Rectangle,
                    title = stringResource(R.string.preference_cards_shape),
                    items = listOf(
                        stringResource(R.string.preference_cards_shape_rounded) to SurfaceShape.Rounded,
                        stringResource(R.string.preference_cards_shape_cut) to SurfaceShape.Cut,
                    ),
                    value = cardStyle.shape,
                    onValueChanged = {
                        viewModel.setShape(it)
                    })
                SliderPreference(
                    title = stringResource(R.string.preference_cards_corner_radius),
                    icon = Icons.Rounded.RoundedCorner,
                    value = cardStyle.cornerRadius,
                    min = 0,
                    max = 24,
                    step = 1,
                    onValueChanged = {
                        viewModel.setRadius(it)
                    }
                )
                SliderPreference(
                    title = stringResource(R.string.preference_cards_opacity),
                    icon = Icons.Rounded.Opacity,
                    value = cardStyle.opacity,
                    min = 0f,
                    max = 1f,
                    onValueChanged = {
                        viewModel.setOpacity(it)
                    }
                )
                SliderPreference(
                    title = stringResource(R.string.preference_cards_stroke_width),
                    icon = Icons.Rounded.LineWeight,
                    value = cardStyle.borderWidth,
                    min = 0,
                    max = 8,
                    step = 1,
                    onValueChanged = {
                        viewModel.setBorderWidth(it)
                    }
                )
            }
        }
    }
}