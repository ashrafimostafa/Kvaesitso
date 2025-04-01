package ir.mostafa.launcher.searchactions.actions

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import ir.mostafa.launcher.ktx.tryStartActivity
import java.time.Duration

data class TimerAction(
    override val label: String,
    val length: Duration
): SearchAction {

    override val icon: SearchActionIcon = SearchActionIcon.Timer
    override val iconColor: Int = 0
    override val customIcon: String? = null
    override fun start(context: Context) {
        val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
            putExtra(AlarmClock.EXTRA_LENGTH, length.seconds.toInt())
        }
        context.tryStartActivity(intent)
    }
}