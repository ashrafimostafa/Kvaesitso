package ir.mostafa.launcher.ui.base

import androidx.appcompat.app.AppCompatActivity
import ir.mostafa.launcher.permissions.PermissionsManager
import org.koin.android.ext.android.inject

abstract class BaseActivity : AppCompatActivity() {
    private val permissionsManager: PermissionsManager by inject()

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()
        permissionsManager.onResume()
    }
}