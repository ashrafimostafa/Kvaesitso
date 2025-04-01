package ir.mostafa.launcher.data.customattrs

enum class CustomAttributeType(val value: String) {
    Icon("icon"),
    Label("label"),
    Tag("tag");

    companion object {
        internal fun fromValue(value: String): CustomAttributeType {
            return values().first { it.value == value }
        }
    }
}