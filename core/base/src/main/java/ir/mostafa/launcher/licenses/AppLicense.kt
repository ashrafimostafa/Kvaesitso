package ir.mostafa.launcher.licenses

import android.content.Context
import ir.mostafa.launcher.base.R

object AppLicense {
    fun get(context: Context): OpenSourceLibrary {
        return OpenSourceLibrary(
            name = context.getString(R.string.app_name),
            description = context.getString(R.string.preference_about_license),
            copyrightNote = "Copyright",
            licenseName = R.string.gpl3_name,
            licenseText = R.raw.license_gpl_3,
            url = "mostafaashrafi.ir"
        )
    }
}
