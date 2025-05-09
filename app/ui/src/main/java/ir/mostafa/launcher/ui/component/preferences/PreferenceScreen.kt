package ir.mostafa.launcher.ui.component.preferences

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ir.mostafa.launcher.ui.locals.LocalNavController


@Composable
fun PreferenceScreen(
    title: String,
    floatingActionButton: @Composable () -> Unit = {},
    topBarActions: @Composable RowScope.() -> Unit = {},
    helpUrl: String? = null,
    lazyColumnState: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit,
) {
    PreferenceScreen(
        title = {
            Text(
                title,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 16.dp),
                maxLines = 1
            )
        },
        floatingActionButton = floatingActionButton,
        topBarActions = topBarActions,
        helpUrl = helpUrl,
        lazyColumnState = lazyColumnState,
        content = content
    )
}

@Composable
fun PreferenceScreen(
    title: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    topBarActions: @Composable RowScope.() -> Unit = {},
    helpUrl: String? = null,
    lazyColumnState: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit,
) {
    val navController = LocalNavController.current

    val context = LocalContext.current

    val colorScheme = MaterialTheme.colorScheme

    val touchSlop = LocalViewConfiguration.current.touchSlop
    var fabVisible by remember { mutableStateOf(true) }
    val nestedScrollConnection = remember {
        object: NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (consumed.y < -touchSlop) fabVisible = false
                else if (consumed.y > touchSlop) fabVisible = true
                return super.onPostScroll(consumed, available, source)
            }
        }
    }

    val activity = LocalContext.current as? AppCompatActivity
    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                fabVisible,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                floatingActionButton()
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = title,
                navigationIcon = {
                    IconButton(onClick = {
                        if (navController?.navigateUp() != true) {
                            activity?.onBackPressed()
                        }
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (helpUrl != null) {
                        IconButton(onClick = {
                            CustomTabsIntent.Builder()
                                .setDefaultColorSchemeParams(
                                    CustomTabColorSchemeParams.Builder()
                                        .setToolbarColor(colorScheme.primaryContainer.toArgb())
                                        .setSecondaryToolbarColor(colorScheme.secondaryContainer.toArgb())
                                        .build()
                                )
                                .build().launchUrl(context, Uri.parse(helpUrl))
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.HelpOutline,
                                contentDescription = "Help"
                            )
                        }
                    }
                    topBarActions()
                }
            )
        }) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)
                .padding(it),
            state = lazyColumnState,
            content = content,
        )
    }

}