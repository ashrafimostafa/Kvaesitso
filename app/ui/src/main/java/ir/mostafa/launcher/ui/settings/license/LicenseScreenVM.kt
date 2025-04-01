package ir.mostafa.launcher.ui.settings.license

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ir.mostafa.launcher.licenses.OpenSourceLibrary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class LicenseScreenVM(private val context: Application) : AndroidViewModel(context) {
    fun getLicenseText(library: OpenSourceLibrary) = flow<String?> {
        val text = withContext(Dispatchers.IO) {
            context.resources.openRawResource(library.licenseText).reader()
                .readText()
        }
        emit(text)
    }
}