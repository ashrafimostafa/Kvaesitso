package ir.mostafa.launcher.ui.settings.backup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mostafa.launcher.ui.R
import ir.mostafa.launcher.ui.component.BottomSheetDialog
import ir.mostafa.launcher.ui.component.LargeMessage
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun CreateBackupSheet(
    onDismissRequest: () -> Unit
) {

    val viewModel: CreateBackupSheetVM = viewModel()

    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/vnd.ir.mostafa.launcher.backup"),
        onResult = {
            if (it != null) viewModel.createBackup(it)
        }
    )
    LaunchedEffect(null) {
        viewModel.reset()
        val fileName = "${
            ZonedDateTime.now().format(
                DateTimeFormatter.ISO_INSTANT
            ).replace(":", "_")
        }.mostafa"
        backupLauncher.launch(fileName)
    }

    val state by viewModel.state

    BottomSheetDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                stringResource(id = R.string.preference_backup),
            )
        },
        confirmButton = {
            if (state == CreateBackupState.Ready) {
                Button(
                    onClick = {
                        val fileName = "${
                            ZonedDateTime.now().format(
                                DateTimeFormatter.ISO_INSTANT
                            )
                        }.mostafa"
                        backupLauncher.launch(fileName)
                    }) {
                    Text(stringResource(R.string.preference_backup))
                }
            } else if (state == CreateBackupState.BackedUp) {
                OutlinedButton(
                    onClick = onDismissRequest
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .verticalScroll(rememberScrollState())
                .padding(it)
        ) {
            when (state) {
                CreateBackupState.Ready -> {

                }
                CreateBackupState.BackingUp -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                CreateBackupState.BackedUp -> {
                    LargeMessage(
                        modifier = Modifier.aspectRatio(1f),
                        icon = Icons.Rounded.CheckCircleOutline,
                        text = stringResource(
                            id = R.string.backup_complete
                        )
                    )
                }
            }
        }
    }
}

