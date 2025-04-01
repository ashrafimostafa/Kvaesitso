package ir.mostafa.launcher.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import ir.mostafa.launcher.appshortcuts.AppShortcut
import ir.mostafa.launcher.services.favorites.FavoritesService
import org.koin.android.ext.android.inject

class AddItemActivity : Activity() {

    private val favoritesService: FavoritesService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val shortcut = AppShortcut(this, intent)
        if (shortcut != null) {
            favoritesService.pinItem(shortcut)
        } else {
        }
        finish()
    }
}
