package ir.mostafa.launcher.ktx

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.InputStream

fun InputStream.asBitmap(options : BitmapFactory.Options? = null): Bitmap? {
    return BitmapFactory.decodeStream(this, null, options)
}