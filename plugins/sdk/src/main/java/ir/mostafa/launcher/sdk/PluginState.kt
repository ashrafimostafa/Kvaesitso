package ir.mostafa.launcher.sdk

import android.content.Intent


sealed class PluginState {
    /**
     * Plugin is ready to be used.
     */
    data class Ready(
        /**
         * Status text, providing additional info what this plugin is currently configured to do.
         * For example "Search %user's files on %service"
         */
        val text: String? = null,
    ) : PluginState()

    /**
     * Plugin requires some setup, e.g. user needs to login to a service.
     */
    data class SetupRequired(
        /**
         * Activity to start to setup the plugin.
         */
        val setupActivity: Intent,
        /**
         * Optional message to display to the user, describing what needs to be done to setup the plugin.
         */
        val message: String? = null,
    ) : PluginState()
}