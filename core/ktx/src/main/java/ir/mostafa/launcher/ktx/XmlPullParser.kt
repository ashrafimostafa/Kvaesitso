package ir.mostafa.launcher.ktx

import org.xmlpull.v1.XmlPullParser

fun XmlPullParser.skipToNextTag(): Boolean {
    while (next() != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG) {
            return true
        }
    }
    return false
}