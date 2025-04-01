package ir.mostafa.launcher.ui.settings.license

import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.OpenInBrowser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mostafa.launcher.licenses.OpenSourceLibrary
import ir.mostafa.launcher.ui.component.preferences.PreferenceCategory
import ir.mostafa.launcher.ui.locals.LocalNavController

@Composable
fun LicenseScreen(library: OpenSourceLibrary) {
    val context = LocalContext.current
    val viewModel: LicenseScreenVM = viewModel()
    val navController = LocalNavController.current

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState()
        )
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(library.name)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController?.navigateUp()
                    }) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    val colorScheme = MaterialTheme.colorScheme
                    IconButton(onClick = {
                        CustomTabsIntent.Builder()
                            .setDefaultColorSchemeParams(CustomTabColorSchemeParams.Builder()
                                .setToolbarColor(colorScheme.primaryContainer.toArgb())
                                .build())
                            .build()
                            .launchUrl(context, Uri.parse(library.url))
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.OpenInBrowser,
                            contentDescription = stringResource(
                                R.string.open_webpage
                            )
                        )
                    }
                }
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(it)
        ) {
            library.description?.let {
                item {
                    PreferenceCategory {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = it)
                        }
                    }
                }
            }
            item {
                PreferenceCategory {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = library.licenseName),
                            style = MaterialTheme.typography.titleMedium
                        )
                        library.copyrightNote?.let {
                            Text(
                                text = it,
                                modifier = Modifier.padding(vertical = 4.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        val licenseText by viewModel.getLicenseText(library).collectAsState(null)
                        licenseText?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}