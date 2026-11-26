package com.earlysleep.app.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.earlysleep.app.data.SleepPrefs
import com.earlysleep.app.notifications.SleepNotifications
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SleepReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val appContext = context.applicationContext
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = SleepPrefs(appContext)
                when (intent.action) {
                    ACTION_CHECK_IN -> {
                        prefs.checkIn()
                        SleepNotifications.cancel(appContext)
                    }

                    ACTION_SNOOZE -> {
                        SleepNotifications.cancel(appContext)
                        SleepAlarmScheduler.scheduleSnooze(appContext)
                    }

                    else -> {
                        if (prefs.remindersEnabled.first()) {
                            SleepNotifications.showBedtimeReminder(appContext)
                            // 每次触发后重新排下一天的闹钟，避免长期运行后时间漂移。
                            SleepAlarmScheduler.scheduleDaily(appContext)
                        }
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_DAILY_REMINDER = "com.earlysleep.app.action.DAILY_REMINDER"
        const val ACTION_SNOOZE_FIRED = "com.earlysleep.app.action.SNOOZE_FIRED"
        const val ACTION_CHECK_IN = "com.earlysleep.app.action.CHECK_IN"
        const val ACTION_SNOOZE = "com.earlysleep.app.action.SNOOZE"
    }
}
