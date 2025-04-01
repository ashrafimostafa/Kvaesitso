package ir.mostafa.launcher.ui.launcher.searchbar

import android.content.Intent
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.launcher.LauncherScaffoldVM
import ir.mostafa.launcher.ui.launcher.widgets.WidgetsVM
import ir.mostafa.launcher.ui.settings.SettingsActivity

@Composable
fun RowScope.SearchBarMenu(
    searchBarValue: String,
    onInputClear: () -> Unit,
) {
    val context = LocalContext.current
    var showOverflowMenu by remember { mutableStateOf(false) }
    val rightIcon = AnimatedImageVector.animatedVectorResource(R.drawable.anim_ic_menu_clear)
    val launcherVM: LauncherScaffoldVM = viewModel()
    val widgetsVM: WidgetsVM = viewModel()

    IconButton(onClick = {
        if (searchBarValue.isNotBlank()) onInputClear()
        else showOverflowMenu = true
    }) {
        Icon(
            painter = rememberAnimatedVectorPainter(
                rightIcon,
                atEnd = searchBarValue.isNotEmpty()
            ),
            contentDescription = stringResource(if (searchBarValue.isNotBlank()) R.string.action_clear else R.string.action_more_actions),
            tint = LocalContentColor.current
        )
    }
    DropdownMenu(expanded = showOverflowMenu, onDismissRequest = { showOverflowMenu = false }) {
        DropdownMenuItem(
            onClick = {
                context.startActivity(Intent(context, SettingsActivity::class.java))
                showOverflowMenu = false
            },
            text = {
                Text(stringResource(R.string.settings))
            },
            leadingIcon = {
                Icon(imageVector = Icons.Rounded.Settings, contentDescription = null)
            }
        )
    }
}