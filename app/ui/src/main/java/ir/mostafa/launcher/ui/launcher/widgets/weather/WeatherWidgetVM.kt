package ir.mostafa.launcher.ui.launcher.widgets.weather

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import ir.mostafa.launcher.permissions.PermissionGroup
import ir.mostafa.launcher.permissions.PermissionsManager
import ir.mostafa.launcher.preferences.weather.WeatherSettings
import ir.mostafa.launcher.ui.settings.SettingsActivity
import ir.mostafa.launcher.weather.DailyForecast
import ir.mostafa.launcher.weather.Forecast
import ir.mostafa.launcher.weather.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.max
import kotlin.math.min

class WeatherWidgetVM : ViewModel(), KoinComponent {
    private val weatherRepository: WeatherRepository by inject()
    private val weatherSettings: WeatherSettings by inject()

    private val permissionsManager: PermissionsManager by inject()

    /**
     * Index of the currently selected day in [dailyForecasts]
     */
    private var selectedDayIndex = 0
        set(value) {
            field = min(value, forecasts.lastIndex)
            if (field < 0) {
                currentForecast.value = null
                return
            }
            selectedForecastIndex = min(
                selectedForecastIndex,
                forecasts[value].hourlyForecasts.lastIndex
            )
            currentDayForecasts.value = forecasts[value].hourlyForecasts
            currentDailyForecast.value = forecasts[value]
            currentForecast.value = getCurrentlySelectedForecast()
        }

    /**
     * Index of the currently selected forecast in [currentDayForecasts]
     */
    private var selectedForecastIndex = 0
    set(value) {
        if (selectedDayIndex < 0)  {
            currentForecast.value = null
            return
        }
        field = min(value, forecasts[selectedDayIndex].hourlyForecasts.lastIndex)
        currentForecast.value = getCurrentlySelectedForecast()
    }

    private val forecastsFlow = weatherRepository.getDailyForecasts()

    /**
     * All available forecasts, grouped by day
     */
    private var forecasts: List<DailyForecast> = emptyList()
    set(value) {
        field = value
        selectedDayIndex = 0
        selectedForecastIndex = 0
        dailyForecasts.value = value
    }

    /**
     * Currently selected forecast, one of [currentDayForecasts]
     */
    val currentForecast = mutableStateOf<Forecast?>(getCurrentlySelectedForecast())

    /**
     * List of forecast summaries for each day
     */
    val dailyForecasts = mutableStateOf<List<DailyForecast>>(emptyList())

    /**
     * Forecasts of the currently selected day (hourly in most cases).
     * This is [DailyForecast.hourlyForecasts] of [currentDailyForecast]
     */
    val currentDayForecasts = mutableStateOf<List<Forecast>>(emptyList())

    /**
     * Daily forecast summary for the currently selected day, one of [dailyForecasts] or null
     */
    val currentDailyForecast = mutableStateOf<DailyForecast?>(null)

    init {
        viewModelScope.launch {
            forecastsFlow.collectLatest {
                forecasts = it
                selectNow()
            }
        }
    }

    val hasLocationPermission = permissionsManager.hasPermission(PermissionGroup.Location)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    fun requestLocationPermission(context: AppCompatActivity) {
        permissionsManager.requestPermission(context, PermissionGroup.Location)
    }
    val autoLocation = weatherSettings.autoLocation
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val imperialUnits = weatherSettings.imperialUnits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun selectDay(index: Int) {
        selectedDayIndex = min(index, forecasts.lastIndex)
    }

    fun selectForecast(index: Int) {
        selectedForecastIndex = index
    }

    private fun getCurrentlySelectedForecast(): Forecast? {
        return forecasts.getOrNull(selectedDayIndex)?.hourlyForecasts?.getOrNull(selectedForecastIndex)
    }

    fun selectNow() {
        if (forecasts.isEmpty()) return
        val now = System.currentTimeMillis()
        val dayIndex = max(0, forecasts.indexOfLast { it.timestamp < now })
        val day = forecasts[dayIndex]
        val forecastIndex = max(0, day.hourlyForecasts.indexOfLast { it.timestamp < now })
        selectDay(dayIndex)
        selectForecast(forecastIndex)
    }

    fun openSettings(context: Context) {
        context.startActivity(
            Intent(context, SettingsActivity::class.java).apply {
                putExtra(SettingsActivity.EXTRA_ROUTE, SettingsActivity.ROUTE_WEATHER_INTEGRATION)
            }
        )
    }

    val isProviderAvailable: Flow<Boolean> = weatherRepository.getActiveProvider().map {
        it != null
    }
}