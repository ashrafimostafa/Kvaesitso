package ir.mostafa.launcher.ui.launcher.search.location

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.AirplanemodeActive
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Commute
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.material.icons.rounded.DirectionsBoat
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.DirectionsRailway
import androidx.compose.material.icons.rounded.DirectionsTransit
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.icons.rounded.Subway
import androidx.compose.material.icons.rounded.Train
import androidx.compose.material.icons.rounded.Tram
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.roundToIntRect
import androidx.compose.ui.unit.times
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blend.Blend.harmonize
import coil.compose.AsyncImage
import ir.mostafa.launcher.i18n.R
import ir.mostafa.launcher.icons.CableCar
import ir.mostafa.launcher.ktx.tryStartActivity
import ir.mostafa.launcher.search.Location
import ir.mostafa.launcher.search.isOpen
import ir.mostafa.launcher.search.location.Attribution
import ir.mostafa.launcher.search.location.Departure
import ir.mostafa.launcher.search.location.LineType
import ir.mostafa.launcher.search.location.OpeningHours
import ir.mostafa.launcher.search.location.OpeningSchedule
import ir.mostafa.launcher.ui.base.LocalTime
import ir.mostafa.launcher.ui.component.DefaultToolbarAction
import ir.mostafa.launcher.ui.component.MarqueeText
import ir.mostafa.launcher.ui.component.RatingBar
import ir.mostafa.launcher.ui.component.ShapedLauncherIcon
import ir.mostafa.launcher.ui.component.Toolbar
import ir.mostafa.launcher.ui.component.ToolbarAction
import ir.mostafa.launcher.ui.ktx.blendIntoViewScale
import ir.mostafa.launcher.ui.ktx.metersToLocalizedString
import ir.mostafa.launcher.ui.launcher.search.common.SearchableItemVM
import ir.mostafa.launcher.ui.launcher.search.listItemViewModel
import ir.mostafa.launcher.ui.launcher.sheets.LocalBottomSheetManager
import ir.mostafa.launcher.ui.locals.LocalFavoritesEnabled
import ir.mostafa.launcher.ui.locals.LocalGridSettings
import ir.mostafa.launcher.ui.locals.LocalSnackbarHostState
import ir.mostafa.launcher.ui.modifier.scale
import kotlinx.coroutines.flow.emptyFlow
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.pow

@Composable
fun LocationItem(
    modifier: Modifier = Modifier,
    location: Location,
    showDetails: Boolean,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: SearchableItemVM = listItemViewModel(key = "search-${location.key}")

    val userLocation by remember {
        viewModel.devicePoseProvider.getLocation()
    }.collectAsStateWithLifecycle(viewModel.devicePoseProvider.lastLocation)

    val targetHeading by remember(userLocation, location) {
        if (userLocation != null) {
            viewModel.devicePoseProvider.getHeadingToDegrees(
                userLocation!!.bearingTo(
                    location.toAndroidLocation()
                )
            )
        } else emptyFlow()
    }.collectAsStateWithLifecycle(null)

    val userHeading by remember {
        if (userLocation != null) {
            viewModel.devicePoseProvider.getAzimuthDegrees()
        } else emptyFlow()
    }.collectAsStateWithLifecycle(null)

    val icon by viewModel.icon.collectAsStateWithLifecycle()
    val badge by viewModel.badge.collectAsStateWithLifecycle(null)

    val imperialUnits by viewModel.imperialUnits.collectAsState()

    val showMap by viewModel.showMap.collectAsState()
    val isUpToDate by viewModel.isUpToDate.collectAsState()

    val distance = userLocation?.distanceTo(location.toAndroidLocation())

    SharedTransitionLayout(
        modifier = modifier,
    ) {
        AnimatedContent(showDetails) { showDetails ->
            if (!showDetails) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ShapedLauncherIcon(
                        modifier = Modifier
                            .animateEnterExit(
                                enter = slideIn { IntOffset(-it.width, 0) } + fadeIn(),
                                exit = slideOut { IntOffset(-it.width, 0) } + fadeOut(),
                            )
                            .padding(12.dp),
                        size = 48.dp,
                        icon = { icon },
                        badge = { badge },
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                    ) {
                        Text(
                            text = location.labelOverride ?: location.label,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier
                                .sharedBounds(
                                    rememberSharedContentState("label"),
                                    this@AnimatedContent
                                )
                        )
                        val category = location.category
                        val formattedDistance = distance?.metersToLocalizedString(
                            context, imperialUnits
                        )
                        if (category != null || formattedDistance != null) {
                            Text(
                                when {
                                    category != null && formattedDistance != null -> "$category • $formattedDistance"

                                    category != null -> category.toString()
                                    formattedDistance != null -> formattedDistance
                                    else -> ""
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .sharedElement(
                                        rememberSharedContentState("sublabel"),
                                        this@AnimatedContent
                                    )
                            )
                        }
                    }
                    Compass(
                        targetHeading = targetHeading,
                        modifier = Modifier.padding(end = 12.dp) then
                                if (!showMap) {
                                    Modifier.sharedBounds(
                                        rememberSharedContentState("compass"),
                                        this@AnimatedContent
                                    )
                                } else {
                                    Modifier.animateEnterExit(
                                        enter = slideIn { IntOffset(it.width, 0) } + fadeIn(),
                                        exit = slideOut { IntOffset(it.width, 0) } + fadeOut(),
                                    )
                                }
                    )
                }
            } else {
                Column {
                    if (showMap) {
                        val tileServerUrl by viewModel.mapTileServerUrl.collectAsState()
                        val shape = MaterialTheme.shapes.small

                        val applyTheming by viewModel.applyMapTheming.collectAsState()
                        MapTiles(
                            modifier = Modifier
                                .animateEnterExit(
                                    enter = expandVertically(),
                                    exit = shrinkOut(),
                                )
                                .padding(top = 12.dp, start = 12.dp, end = 12.dp)
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
                                .clip(MaterialTheme.shapes.small)
                                .clickable {
                                    viewModel.launch(context)
                                },
                            tileServerUrl = tileServerUrl,
                            location = location,
                            maxZoomLevel = 19,
                            tiles = IntSize(3, 2),
                            applyTheming = applyTheming,
                            userLocation = {
                                userLocation?.let {
                                    UserLocation(
                                        it.latitude,
                                        it.longitude,
                                        heading = userHeading,
                                    )
                                }
                            },
                        )
                    }

                    Row(
                        modifier = Modifier.padding(
                            top = 12.dp,
                            start = 12.dp,
                            end = 12.dp,
                            bottom = 4.dp
                        ),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                text = location.labelOverride ?: location.label,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .sharedBounds(
                                        rememberSharedContentState("label"),
                                        this@AnimatedContent
                                    )
                            )
                            val category = location.category
                            val formattedDistance = distance?.metersToLocalizedString(
                                context, imperialUnits
                            )
                            if (category != null || formattedDistance != null) {
                                Text(
                                    when {
                                        category != null && formattedDistance != null -> "$category • $formattedDistance"

                                        category != null -> category.toString()
                                        formattedDistance != null -> formattedDistance
                                        else -> ""
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier
                                        .padding(top = 2.dp)
                                        .sharedElement(
                                            rememberSharedContentState("sublabel"),
                                            this@AnimatedContent
                                        )
                                )
                            }
                            if (location.userRating != null) {
                                RatingBar(
                                    location.userRating!!,
                                    modifier = Modifier
                                        .padding(top = 6.dp)
                                        .offset(-2.dp)
                                )
                            }

                            if (!showMap) {
                                val attribution = location.attribution
                                if (attribution != null) {
                                    Attribution(
                                        attribution,
                                        reverse = true,
                                        modifier = Modifier
                                            .padding(
                                                top = 16.dp,
                                                bottom = 0.dp,
                                            )
                                            .clickable(
                                                enabled = attribution.url != null
                                            ) {
                                                context.tryStartActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse(attribution.url)
                                                    )
                                                )
                                            }
                                    )
                                }
                            }
                        }

                        if (!showMap) {
                            Compass(
                                targetHeading = targetHeading,
                                modifier = Modifier
                                    .sharedBounds(
                                        rememberSharedContentState("compass"),
                                        this@AnimatedContent
                                    ),
                                size = 56.dp,
                            )
                        } else {
                            val attribution = location.attribution
                            if (attribution != null) {
                                Attribution(
                                    attribution,
                                    modifier = Modifier
                                        .padding(
                                            top = 4.dp,
                                            bottom = 4.dp,
                                            start = 12.dp)
                                        .clickable(
                                            enabled = attribution.url != null
                                        ) {
                                            context.tryStartActivity(
                                                Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse(attribution.url)
                                                )
                                            )
                                        }
                                )
                            }
                        }
                    }

                    val openingSchedule = location.openingSchedule
                    val departures = remember(location.departures) {
                        location.departures
                            ?.sortedBy { it.time }
                    }
                    if (departures != null) {
                        val time = LocalTime.current
                        val nextDeparture = remember(time) {
                            departures.firstOrNull {
                                it.time.plus(it.delay ?: Duration.ZERO).isAfter(ZonedDateTime.now())
                            }
                        }
                        if (nextDeparture != null) {
                            var showDepartureList by remember(departures) {
                                mutableStateOf(false)
                            }
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp, end = 12.dp, top = 12.dp),
                                shape = MaterialTheme.shapes.small,
                                onClick = { showDepartureList = !showDepartureList }
                            ) {
                                val listState = rememberLazyListState()

                                AnimatedContent(showDepartureList) { showList ->
                                    if (!showList) {
                                        Row(
                                            Modifier
                                                .padding(12.dp)
                                                .fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            nextDeparture.LineIcon(Modifier.padding(end = 8.dp))
                                            val lastStop = nextDeparture.lastStop
                                            if (lastStop != null) {
                                                MarqueeText(
                                                    modifier = Modifier.weight(1f),
                                                    text = lastStop,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    iterations = Int.MAX_VALUE,
                                                    repeatDelayMillis = 0,
                                                    velocity = 20.dp,
                                                    fadeLeft = 5.dp,
                                                    fadeRight = 5.dp,
                                                )
                                            }

                                            val formattedTime = remember(time) {
                                                val timeLeft = Duration.between(
                                                    java.time.LocalTime.now(),
                                                    nextDeparture.time + (nextDeparture.delay
                                                        ?: Duration.ZERO)
                                                ).toMinutes()
                                                if (timeLeft < 1) "now" else "in $timeLeft min"
                                            }

                                            Text(
                                                text = formattedTime,
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier.padding(end = 12.dp)
                                            )
                                            Icon(Icons.AutoMirrored.Rounded.NavigateNext, null)
                                        }
                                    } else {
                                        val longestLine = remember(departures) {
                                            departures.maxOfOrNull { it.line.length }
                                        }
                                        LazyColumn(
                                            state = listState,
                                            modifier = modifier
                                                .heightIn(max = 192.dp)
                                                .padding(12.dp)
                                                .fillMaxWidth()
                                                .pointerInput(Unit) { detectDragGestures { _, _ -> } },
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            itemsIndexed(
                                                departures,
                                                key = { idx, _ -> idx }) { idx, it ->
                                                it.LazyColumnPart(
                                                    lineWidth = longestLine,
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .graphicsLayer {
                                                            alpha =
                                                                listState.layoutInfo.blendIntoViewScale(
                                                                    idx
                                                                )
                                                        }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (openingSchedule is OpeningSchedule.TwentyFourSeven || (openingSchedule is OpeningSchedule.Hours && openingSchedule.openingHours.isNotEmpty())) {
                        var showOpeningSchedule by remember(openingSchedule) {
                            mutableStateOf(false)
                        }
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 12.dp, end = 12.dp, top = 12.dp),
                            shape = MaterialTheme.shapes.small,
                            onClick = {
                                if (openingSchedule !is OpeningSchedule.TwentyFourSeven) {
                                    showOpeningSchedule = !showOpeningSchedule
                                }
                            }
                        ) {
                            AnimatedContent(showOpeningSchedule) { showSchedule ->
                                if (!showSchedule) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        when (openingSchedule) {
                                            is OpeningSchedule.TwentyFourSeven -> {
                                                Text(
                                                    text = stringResource(R.string.location_open_24_7),
                                                    style = MaterialTheme.typography.labelMedium,
                                                )
                                            }

                                            is OpeningSchedule.Hours -> {
                                                val text = remember(openingSchedule) {
                                                    val currentOpeningTime =
                                                        openingSchedule.getCurrentOpeningHours()
                                                    val timeFormat =
                                                        DateTimeFormatter.ofLocalizedTime(
                                                            FormatStyle.SHORT
                                                        )
                                                    return@remember if (currentOpeningTime != null) {
                                                        val isSameDay =
                                                            currentOpeningTime.dayOfWeek == LocalDateTime.now().dayOfWeek
                                                        val formattedTime =
                                                            timeFormat.format(currentOpeningTime.startTime + currentOpeningTime.duration)
                                                        val closingTime = if (isSameDay) {
                                                            context.getString(
                                                                R.string.location_closes,
                                                                formattedTime
                                                            )
                                                        } else {
                                                            val dow =
                                                                currentOpeningTime.dayOfWeek.getDisplayName(
                                                                    TextStyle.SHORT,
                                                                    Locale.getDefault()
                                                                )
                                                            context.getString(
                                                                R.string.location_closes_other_day,
                                                                dow,
                                                                formattedTime
                                                            )
                                                        }
                                                        "${context.getString(R.string.location_open)} • $closingTime"
                                                    } else {
                                                        val nextOpeningTime =
                                                            openingSchedule.getNextOpeningHours()
                                                        val isSameDay =
                                                            nextOpeningTime.dayOfWeek == LocalDateTime.now().dayOfWeek
                                                        val formattedTime =
                                                            timeFormat.format(nextOpeningTime.startTime)
                                                        val openingTime = if (isSameDay) {
                                                            context.getString(
                                                                R.string.location_opens,
                                                                formattedTime
                                                            )
                                                        } else {
                                                            val dow =
                                                                nextOpeningTime.dayOfWeek.getDisplayName(
                                                                    TextStyle.SHORT,
                                                                    Locale.getDefault()
                                                                )
                                                            context.getString(
                                                                R.string.location_opens_other_day,
                                                                dow,
                                                                formattedTime
                                                            )
                                                        }
                                                        "${context.getString(R.string.location_closed)} • $openingTime"
                                                    }
                                                }

                                                Text(
                                                    text = text,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    modifier = Modifier.weight(1f)
                                                )

                                                Icon(Icons.AutoMirrored.Rounded.NavigateNext, null)
                                            }
                                        }
                                    }
                                } else if (openingSchedule is OpeningSchedule.Hours) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    ) {
                                        val groups = remember(openingSchedule) {
                                            openingSchedule.openingHours
                                                .groupBy { it.dayOfWeek }.entries
                                                .sortedBy { it.key }
                                        }

                                        for (group in groups) {
                                            Row(
                                                modifier = Modifier.padding(
                                                    vertical = 2.dp,
                                                    horizontal = 12.dp
                                                ),
                                            ) {
                                                Text(
                                                    modifier = Modifier.weight(1f),
                                                    text = group.key.getDisplayName(
                                                        TextStyle.FULL,
                                                        Locale.getDefault()
                                                    ),
                                                    style = MaterialTheme.typography.bodySmall,
                                                )
                                                val times = remember(group.value) {
                                                    val dateFormatter =
                                                        DateTimeFormatter.ofLocalizedTime(
                                                            FormatStyle.SHORT
                                                        )
                                                    group.value.sortedBy { it.startTime }
                                                        .joinToString(separator = ", ") {
                                                            "${it.startTime.format(dateFormatter)}–${
                                                                it.startTime.plus(
                                                                    it.duration
                                                                ).format(dateFormatter)
                                                            }"
                                                        }
                                                }
                                                Text(
                                                    text = times,
                                                    style = MaterialTheme.typography.labelMedium,
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }

                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(start = 12.dp, top = 8.dp)
                    ) {
                        val navigationIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("google.navigation:q=${location.latitude},${location.longitude}")
                        )
                        val canResolveNavigationIntent = remember {
                            null != context.packageManager.resolveActivity(navigationIntent, 0)
                        }
                        if (canResolveNavigationIntent) {
                            AssistChip(
                                modifier = Modifier.padding(end = 12.dp),
                                onClick = { context.tryStartActivity(navigationIntent) },
                                label = { Text(stringResource(R.string.menu_navigation)) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Rounded.Directions, null,
                                        modifier = Modifier.size(AssistChipDefaults.IconSize)
                                    )
                                }
                            )
                        }
                        location.phoneNumber?.let {
                            AssistChip(
                                modifier = Modifier.padding(end = 12.dp),
                                onClick = {
                                    context.tryStartActivity(
                                        Intent(
                                            Intent.ACTION_DIAL, Uri.parse("tel:$it")
                                        )
                                    )
                                },
                                label = { Text(stringResource(R.string.menu_dial)) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Rounded.Phone, null,
                                        modifier = Modifier.size(AssistChipDefaults.IconSize)
                                    )
                                }
                            )
                        }

                        location.websiteUrl?.let {
                            AssistChip(
                                modifier = Modifier.padding(end = 12.dp),
                                onClick = {
                                    context.tryStartActivity(
                                        Intent(
                                            Intent.ACTION_VIEW, Uri.parse(it)
                                        )
                                    )
                                },
                                label = { Text(stringResource(R.string.menu_website)) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Rounded.Language, null,
                                        modifier = Modifier.size(AssistChipDefaults.IconSize)
                                    )
                                }
                            )
                        }
                    }


                    val toolbarActions = mutableListOf<ToolbarAction>()

                    if (LocalFavoritesEnabled.current) {
                        val isPinned by viewModel.isPinned.collectAsState(false)
                        val favAction = if (isPinned) {
                            DefaultToolbarAction(
                                label = stringResource(R.string.menu_favorites_unpin),
                                icon = Icons.Rounded.Star,
                                action = {
                                    viewModel.unpin()
                                }
                            )
                        } else {
                            DefaultToolbarAction(
                                label = stringResource(R.string.menu_favorites_pin),
                                icon = Icons.Rounded.StarOutline,
                                action = {
                                    viewModel.pin()
                                })
                        }
                        toolbarActions.add(favAction)
                    }

                    toolbarActions += DefaultToolbarAction(
                        label = stringResource(id = R.string.menu_map),
                        icon = Icons.AutoMirrored.Rounded.OpenInNew,
                    ) {
                        viewModel.launch(context)
                    }

                    val sheetManager = LocalBottomSheetManager.current
                    val lifecycleOwner = LocalLifecycleOwner.current
                    val snackbarHostState = LocalSnackbarHostState.current

                    toolbarActions.add(DefaultToolbarAction(
                        label = stringResource(R.string.menu_customize),
                        icon = Icons.Rounded.Tune,
                        action = { sheetManager.showCustomizeSearchableModal(location) }
                    ))

                    location.fixMeUrl?.let {
                        toolbarActions += DefaultToolbarAction(
                            label = stringResource(id = R.string.menu_bugreport),
                            icon = Icons.Rounded.BugReport,
                        ) {
                            context.tryStartActivity(
                                Intent(
                                    Intent.ACTION_VIEW, Uri.parse(location.fixMeUrl)
                                )
                            )
                        }
                    }

                    Toolbar(
                        modifier = Modifier.fillMaxWidth(),
                        leftActions = listOf(DefaultToolbarAction(
                            label = stringResource(id = R.string.menu_back),
                            icon = Icons.AutoMirrored.Rounded.ArrowBack
                        ) {
                            onBack()
                        }),
                        rightActions = toolbarActions,
                    )
                }
            }
        }
    }
}

@Composable
fun Compass(
    targetHeading: Float?,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .background(
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.shapes.small
            ),
        contentAlignment = Alignment.Center
    ) {
        if (targetHeading != null) {
            Icon(
                Icons.Rounded.Navigation,
                null,
                modifier = Modifier
                    .size(20f / 48f * size)
                    .rotate(targetHeading)
                    .offset(y = -1f / 48f * size),
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

@Composable
fun LocationItemGridPopup(
    location: Location,
    show: MutableTransitionState<Boolean>,
    animationProgress: Float,
    origin: Rect,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        show,
        enter = expandIn(
            animationSpec = tween(300),
            expandFrom = Alignment.TopEnd,
        ) { origin.roundToIntRect().size },
        exit = shrinkOut(
            animationSpec = tween(300),
            shrinkTowards = Alignment.TopEnd,
        ) { origin.roundToIntRect().size },
    ) {
        LocationItem(
            modifier = Modifier
                .fillMaxWidth()
                .scale(
                    1 - (1 - LocalGridSettings.current.iconSize / 84f) * (1 - animationProgress),
                    transformOrigin = TransformOrigin(1f, 0f)
                )
                .offset(
                    x = 16.dp * (1 - animationProgress).pow(10),
                    y = (-16).dp * (1 - animationProgress),
                ),
            location = location,
            showDetails = true,
            onBack = onDismiss,
        )
    }
}

private fun buildAddress(
    street: String?,
    houseNumber: String?,
): String? {
    val summary = StringBuilder()
    if (street != null) {
        summary.append(street, ' ')
        if (houseNumber != null) {
            summary.append(houseNumber, ' ')
        }
    }
    return if (summary.isEmpty()) null else summary.toString()
}

private fun OpeningSchedule.Hours.getCurrentOpeningHours(): OpeningHours? {
    return openingHours.find { it.isOpen() }
}

private fun OpeningSchedule.Hours.getNextOpeningHours(): OpeningHours {
    val now = LocalDateTime.now()
    val sortedSchedule = this
        .openingHours
        .sortedWith { a, b ->
            if (a.dayOfWeek == b.dayOfWeek) {
                a.startTime.compareTo(b.startTime)
            } else {
                a.dayOfWeek.compareTo(b.dayOfWeek)
            }
        }

    return sortedSchedule
        .find {
            now.dayOfWeek < it.dayOfWeek || now.dayOfWeek == it.dayOfWeek && now.toLocalTime() < it.startTime
        } ?: sortedSchedule.first()
}

@Composable
fun Departure.LineIcon(
    modifier: Modifier
) {
    val harmonizeArgb = MaterialTheme.colorScheme.primary.toArgb()
    var (lineBg, lineFg) = if (lineColor != null) {
        val bg = Color(
            harmonize(lineColor!!.toArgb(), harmonizeArgb)
        )
        val fg = Color(
            harmonize(
                if (0.5f < bg.luminance()) Color.Black.toArgb() else Color.White.toArgb(),
                harmonizeArgb
            )
        )
        bg to fg
    } else {
        MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    }

    val hasDeparted = ZonedDateTime.now().isAfter(time + (delay ?: Duration.ZERO))

    if (hasDeparted) {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(lineBg.toArgb(), hsv)
        val (h, s, v) = hsv
        lineBg = Color.hsv(h, s / 2f, v, lineBg.alpha)
    }

    Row(
        modifier = modifier
            .wrapContentWidth(Alignment.Start)
            .background(
                lineBg,
                MaterialTheme.shapes.small
            )
            .padding(top = 4.dp, bottom = 4.dp, start = 4.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = when (type) {
                LineType.Bus -> Icons.Rounded.DirectionsBus
                LineType.Tram -> Icons.Rounded.Tram
                LineType.Subway -> Icons.Rounded.Subway
                LineType.Monorail -> Icons.Rounded.DirectionsTransit
                LineType.CommuterTrain -> Icons.Rounded.DirectionsRailway
                LineType.Train, LineType.RegionalTrain, LineType.HighSpeedTrain -> Icons.Rounded.Train
                LineType.Boat -> Icons.Rounded.DirectionsBoat
                LineType.CableCar -> Icons.Rounded.CableCar
                LineType.Airplane -> Icons.Rounded.AirplanemodeActive
                null -> Icons.Rounded.Commute
            },
            contentDescription = type?.name, // TODO localize (maybe) with ?.let{ stringResource("departure_line_type_$it") }
            tint = lineFg,
            modifier = Modifier
                .padding(end = 2.dp)
                .size(16.dp),
        )
        MarqueeText(
            text = line,
            style = MaterialTheme.typography.labelSmall,
            color = lineFg,
            textAlign = TextAlign.Center,
            fadeLeft = 2.5.dp,
            fadeRight = 2.5.dp,
            iterations = Int.MAX_VALUE,
            repeatDelayMillis = 0,
            spacing = MarqueeSpacing(10.dp),
            velocity = 20.dp,
            modifier = Modifier
                .wrapContentSize()
                .widthIn(max = 34.dp)
        )
    }
}

@Composable
fun Departure.LazyColumnPart(
    lineWidth: Int?,
    modifier: Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            LineIcon(
                Modifier
                    .padding(end = 8.dp)
                    .widthIn(
                        min = if (lineWidth == null) 0.dp
                        else max(64.dp, lineWidth * 8.dp)
                    )
            )
            if (lastStop != null) {
                MarqueeText(
                    text = lastStop!!,
                    style = MaterialTheme.typography.labelMedium,
                    iterations = Int.MAX_VALUE,
                    repeatDelayMillis = 0,
                    velocity = 20.dp,
                    fadeLeft = 5.dp,
                    fadeRight = 5.dp,
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = time.format(
                    DateTimeFormatter.ofPattern(
                        "HH:mm",
                        Locale.getDefault()
                    )
                ),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(end = 2.dp)
            )
            val delayMinutes = delay?.toMinutes()
            if (null != delayMinutes && 0L < delayMinutes) {
                Text(
                    text = "+$delayMinutes",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = TextUnit(2f, TextUnitType.Em),
                )
            }
        }
    }
}

@Composable
private fun Attribution(
    attribution: Attribution,
    modifier: Modifier = Modifier,
    reverse: Boolean = false,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (attribution.text != null && !reverse) {
            Text(
                text = attribution.text!!,
                style = MaterialTheme.typography.labelSmall,
            )
        }

        if (attribution.iconUrl != null) {
            AsyncImage(
                modifier = Modifier
                    .padding(
                        start = if (reverse) 0.dp else 8.dp,
                        end = if (reverse) 8.dp else 0.dp
                    )
                    .requiredHeight(16.dp),
                model = attribution.iconUrl!!,
                contentDescription = null,
            )
        }
        if (attribution.text != null && reverse) {
            Text(
                text = attribution.text!!,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}