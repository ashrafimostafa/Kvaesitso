package ir.mostafa.launcher.ktx

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

inline fun <reified T> Json.decodeFromStringOrNull(json: String?): T? {
    if (json == null) return null
    return try {
        decodeFromString(json)
    } catch (e: SerializationException) {
        null
    }
}