package com.earlysleep.app.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.earlysleep.app.data.SleepPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 开机、修改时间、修改时区、应用更新后恢复每日提醒。
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val appContext = context.applicationContext
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (SleepPrefs(appContext).remindersEnabled.first()) {
                    SleepAlarmScheduler.scheduleDaily(appContext)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
