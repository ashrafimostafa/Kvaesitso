package ir.mostafa.launcher.preferences.ui

import ir.mostafa.launcher.preferences.BaseLayout
import ir.mostafa.launcher.preferences.ColorScheme
import ir.mostafa.launcher.preferences.Font
import ir.mostafa.launcher.preferences.IconShape
import ir.mostafa.launcher.preferences.LauncherDataStore
import ir.mostafa.launcher.preferences.ScreenOrientation
import ir.mostafa.launcher.preferences.SearchBarColors
import ir.mostafa.launcher.preferences.SearchBarStyle
import ir.mostafa.launcher.preferences.SurfaceShape
import ir.mostafa.launcher.preferences.SystemBarColors
import ir.mostafa.launcher.preferences.ThemeDescriptor
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

data class CardStyle(
    val opacity: Float = 1f,
    val cornerRadius: Int = 0,
    val shape: SurfaceShape = SurfaceShape.Rounded,
    val borderWidth: Int = 0,
)

data class GridSettings(
    val columnCount: Int = 5,
    val iconSize: Int = 48,
    val showLabels: Boolean = true,
)

class UiSettings internal constructor(
    private val launcherDataStore: LauncherDataStore,
) {
    val favoritesEnabled
        get() = launcherDataStore.data.map { it.favoritesEnabled || it.homeScreenDock }

    val iconShape
        get() = launcherDataStore.data.map {
            it.iconsShape
        }

    fun setIconShape(iconShape: IconShape) {
        launcherDataStore.update {
            it.copy(iconsShape = iconShape)
        }
    }

    val gridSettings
        get() = launcherDataStore.data.map {
            GridSettings(
                showLabels = it.gridLabels,
                iconSize = it.gridIconSize,
                columnCount = it.gridColumnCount,
            )
        }

    fun setGridColumnCount(columnCount: Int) {
        launcherDataStore.update {
            it.copy(gridColumnCount = columnCount)
        }
    }

    fun setGridIconSize(iconSize: Int) {
        launcherDataStore.update {
            it.copy(gridIconSize = iconSize)
        }
    }

    fun setGridShowLabels(showLabels: Boolean) {
        launcherDataStore.update {
            it.copy(gridLabels = showLabels)
        }
    }


    val cardStyle
        get() = launcherDataStore.data.map {
            CardStyle(
                opacity = it.surfacesOpacity,
                cornerRadius = it.surfacesRadius,
                shape = it.surfacesShape,
                borderWidth = it.surfacesBorderWidth,
            )
        }

    fun setCardOpacity(opacity: Float) {
        launcherDataStore.update {
            it.copy(surfacesOpacity = opacity)
        }
    }

    fun setCardRadius(radius: Int) {
        launcherDataStore.update {
            it.copy(surfacesRadius = radius)
        }
    }

    fun setCardBorderWidth(borderWidth: Int) {
        launcherDataStore.update {
            it.copy(surfacesBorderWidth = borderWidth)
        }
    }

    fun setCardShape(shape: SurfaceShape) {
        launcherDataStore.update {
            it.copy(surfacesShape = shape)
        }
    }

    val dimWallpaper
        get() = launcherDataStore.data.map {
            it.wallpaperDim
        }

    fun setDimWallpaper(dimWallpaper: Boolean) {
        launcherDataStore.update {
            it.copy(wallpaperDim = dimWallpaper)
        }
    }

    val blurWallpaper
        get() = launcherDataStore.data.map {
            it.wallpaperBlur
        }.distinctUntilChanged()

    fun setBlurWallpaper(blurWallpaper: Boolean) {
        launcherDataStore.update {
            it.copy(wallpaperBlur = blurWallpaper)
        }
    }

    val wallpaperBlurRadius
        get() = launcherDataStore.data.map {
            it.wallpaperBlurRadius
        }.distinctUntilChanged()

    fun setWallpaperBlurRadius(wallpaperBlurRadius: Int) {
        launcherDataStore.update {
            it.copy(wallpaperBlurRadius = wallpaperBlurRadius)
        }
    }

    val colorScheme
        get() = launcherDataStore.data.map {
            it.uiColorScheme
        }.distinctUntilChanged()

    val compatModeColors
        get() = launcherDataStore.data.map {
            it.uiCompatModeColors
        }.distinctUntilChanged()

    fun setCompatModeColors(enabled: Boolean) {
        launcherDataStore.update {
            it.copy(uiCompatModeColors = enabled)
        }
    }

    val statusBarColor
        get() = launcherDataStore.data.map {
            it.systemBarsStatusColors
        }.distinctUntilChanged()

    val hideStatusBar
        get() = launcherDataStore.data.map {
            it.systemBarsHideStatus
        }.distinctUntilChanged()

    val hideNavigationBar
        get() = launcherDataStore.data.map {
            it.systemBarsHideNav
        }.distinctUntilChanged()

    fun setHideStatusBar(hideStatusBar: Boolean) {
        launcherDataStore.update {
            it.copy(systemBarsHideStatus = hideStatusBar)
        }
    }

    fun setHideNavigationBar(hideNavigationBar: Boolean) {
        launcherDataStore.update {
            it.copy(systemBarsHideNav = hideNavigationBar)
        }
    }

    val navigationBarColor
        get() = launcherDataStore.data.map {
            it.systemBarsNavColors
        }.distinctUntilChanged()

    fun setStatusBarColor(statusBarColor: SystemBarColors) {
        launcherDataStore.update {
            it.copy(systemBarsStatusColors = statusBarColor)
        }
    }

    fun setNavigationBarColor(navigationBarColor: SystemBarColors) {
        launcherDataStore.update {
            it.copy(systemBarsNavColors = navigationBarColor)
        }
    }

    val chargingAnimation
        get() = launcherDataStore.data.map {
            it.animationsCharging
        }.distinctUntilChanged()

    fun setChargingAnimation(chargingAnimation: Boolean) {
        launcherDataStore.update {
            it.copy(animationsCharging = chargingAnimation)
        }
    }

    val baseLayout
        get() = launcherDataStore.data.map {
            it.uiBaseLayout
        }.distinctUntilChanged()

    fun setBaseLayout(baseLayout: BaseLayout) {
        launcherDataStore.update {
            it.copy(uiBaseLayout = baseLayout)
        }
    }

    val clockFillScreen
        get() = launcherDataStore.data.map {
            it.clockWidgetFillHeight
        }.distinctUntilChanged()

    val searchBarStyle
        get() = launcherDataStore.data.map {
            it.searchBarStyle
        }.distinctUntilChanged()

    fun setSearchBarStyle(searchBarStyle: SearchBarStyle) {
        launcherDataStore.update {
            it.copy(searchBarStyle = searchBarStyle)
        }
    }

    val searchBarColor
        get() = launcherDataStore.data.map {
            it.searchBarColors
        }.distinctUntilChanged()

    fun setSearchBarColor(color: SearchBarColors) {
        launcherDataStore.update {
            it.copy(searchBarColors = color)
        }
    }

    val bottomSearchBar
        get() = launcherDataStore.data.map {
            it.searchBarBottom
        }.distinctUntilChanged()

    fun setBottomSearchBar(bottomSearchBar: Boolean) {
        launcherDataStore.update {
            it.copy(searchBarBottom = bottomSearchBar)
        }
    }

    val reverseSearchResults
        get() = launcherDataStore.data.map {
            it.searchResultsReversed
        }.distinctUntilChanged()

    fun setReverseSearchResults(reverseSearchResults: Boolean) {
        launcherDataStore.update {
            it.copy(searchResultsReversed = reverseSearchResults)
        }
    }

    val fixedSearchBar
        get() = launcherDataStore.data.map {
            it.searchBarFixed
        }.distinctUntilChanged()

    fun setFixedSearchBar(fixedSearchBar: Boolean) {
        launcherDataStore.update {
            it.copy(searchBarFixed = fixedSearchBar)
        }
    }

    val openKeyboardOnSearch
        get() = launcherDataStore.data.map {
            it.searchBarKeyboard
        }.distinctUntilChanged()


    val orientation
        get() = launcherDataStore.data.map {
            it.uiOrientation
        }.distinctUntilChanged()

    fun setOrientation(orientation: ScreenOrientation) {
        launcherDataStore.update {
            it.copy(uiOrientation = orientation)
        }
    }


    val theme
        get() = launcherDataStore.data.map {
            it.uiTheme
        }.distinctUntilChanged()

    fun setTheme(theme: ThemeDescriptor) {
        launcherDataStore.update {
            it.copy(uiTheme = theme)
        }
    }

    val font
        get() = launcherDataStore.data.map {
            it.uiFont
        }.distinctUntilChanged()

    fun setFont(font: Font) {
        launcherDataStore.update {
            it.copy(uiFont = font)
        }
    }

    fun setColorScheme(colorScheme: ColorScheme) {
        launcherDataStore.update {
            it.copy(uiColorScheme = colorScheme)
        }
    }

    val dock
        get() = launcherDataStore.data.map {
            it.homeScreenDock
        }.distinctUntilChanged()

    val widgetEditButton
        get() = launcherDataStore.data.map {
            it.widgetsEditButton
        }.distinctUntilChanged()

    fun setWidgetEditButton(editButton: Boolean) {
        launcherDataStore.update {
            it.copy(widgetsEditButton = editButton)
        }
    }
}