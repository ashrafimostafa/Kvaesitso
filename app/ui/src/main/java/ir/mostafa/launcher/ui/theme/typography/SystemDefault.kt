package ir.mostafa.launcher.ui.theme.typography

import android.content.Context
import androidx.compose.material3.Typography
import ir.mostafa.launcher.ui.theme.typography.fontfamily.getDeviceHeadlineFontFamily

fun getDeviceDefaultTypography(context: Context): Typography {
    return makeTypography(headlineFamily = getDeviceHeadlineFontFamily(context))
}