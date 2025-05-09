package ir.mostafa.launcher.ui.settings.colorscheme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.SettingsSuggest
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.component.BottomSheetDialog
import ir.mostafa.launcher.ui.component.colorpicker.HctColorPicker
import ir.mostafa.launcher.ui.component.colorpicker.rememberHctColorPickerState
import ir.mostafa.launcher.ui.component.preferences.SwitchPreference

@Composable
fun CorePaletteColorPreference(
    title: String,
    value: Int?,
    onValueChange: (Int?) -> Unit,
    defaultValue: Int,
    modifier: Modifier = Modifier,
    autoGenerate: (() -> Int?)? = null,
) {
    var showDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val tooltipState = rememberTooltipState()

    TooltipBox(
        state = tooltipState,
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(title)
            }
        },
    ) {
        ColorSwatch(
            color = Color(value ?: defaultValue),
            modifier = modifier
                .size(48.dp)
                .combinedClickable(
                    onClick = { showDialog = true },
                    onLongClick = {
                        onValueChange(null)
                    }
                ),
        )
    }

    if (showDialog) {
        var currentValue by remember { mutableStateOf(value) }
        BottomSheetDialog(onDismissRequest = {
            onValueChange(currentValue)
            showDialog = false
        }) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(it)
            ) {
                SwitchPreference(
                    icon = Icons.Rounded.SettingsSuggest,
                    title = stringResource(R.string.theme_color_scheme_system_default),
                    value = currentValue == null,
                    onValueChanged = {
                        currentValue = if (it) null else defaultValue
                    }
                )
                AnimatedVisibility(
                    currentValue != null,
                    enter = expandVertically(
                        expandFrom = Alignment.Top,
                    ),
                    exit = shrinkVertically(
                        shrinkTowards = Alignment.Top,
                    )
                ) {
                    Column {
                        HorizontalDivider(
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                        val colorPickerState = rememberHctColorPickerState(
                            initialColor = Color(value ?: defaultValue),
                            onColorChanged = {
                                currentValue = it.toArgb()
                            }
                        )
                        HctColorPicker(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally),
                            state = colorPickerState,
                        )

                        if (autoGenerate != null) {
                            HorizontalDivider(
                                modifier = Modifier.padding(top = 16.dp)
                            )

                            TextButton(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .align(Alignment.End),
                                contentPadding = ButtonDefaults.TextButtonWithIconContentPadding,
                                onClick = {
                                    val autoGenerated = autoGenerate()
                                    currentValue = autoGenerated
                                    if (autoGenerated != null) {
                                        colorPickerState.setColor(Color(autoGenerated))
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Rounded.AutoFixHigh, null,
                                    modifier = Modifier
                                        .padding(ButtonDefaults.IconSpacing)
                                        .size(ButtonDefaults.IconSize)
                                )
                                Text(stringResource(R.string.theme_color_scheme_autogenerate))
                            }
                        }
                    }
                }
            }
        }
    }
}