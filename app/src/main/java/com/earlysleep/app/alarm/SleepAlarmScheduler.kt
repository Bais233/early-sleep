package com.earlysleep.app.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.time.ZonedDateTime

object SleepAlarmScheduler {

    const val BEDTIME_HOUR = 23
    const val BEDTIME_MINUTE = 0

    private const val DAILY_REQUEST_CODE = 2300
    private const val SNOOZE_REQUEST_CODE = 2301

    fun scheduleDaily(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val pendingIntent = dailyPendingIntent(context)
        setAlarm(alarmManager, nextTrigger().toInstant().toEpochMilli(), pendingIntent)
    }

    fun scheduleSnooze(context: Context, delayMinutes: Long = 10L) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        setAlarm(
            alarmManager = alarmManager,
            triggerAtMillis = System.currentTimeMillis() + delayMinutes * 60_000L,
            pendingIntent = snoozePendingIntent(context)
        )
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        alarmManager.cancel(dailyPendingIntent(context))
        alarmManager.cancel(snoozePendingIntent(context))
    }

    /**
     * 距离当前最近的一次 23:00（今天还没到就是今天，否则是明天）。
     */
    fun nextTrigger(): ZonedDateTime {
        val now = ZonedDateTime.now()
        var target = now
            .withHour(BEDTIME_HOUR)
            .withMinute(BEDTIME_MINUTE)
            .withSecond(0)
            .withNano(0)
        if (!target.isAfter(now)) target = target.plusDays(1)
        return target
    }

    private fun setAlarm(alarmManager: AlarmManager, triggerAtMillis: Long, pendingIntent: PendingIntent) {
        val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            alarmManager.canScheduleExactAlarms()
        if (canExact) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } else {
            // 没有精确闹钟权限时降级为非精确闹钟，避免提醒完全丢失。
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    private fun dailyPendingIntent(context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            DAILY_REQUEST_CODE,
            Intent(context, SleepReminderReceiver::class.java)
                .setAction(SleepReminderReceiver.ACTION_DAILY_REMINDER),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    private fun snoozePendingIntent(context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            SNOOZE_REQUEST_CODE,
            Intent(context, SleepReminderReceiver::class.java)
                .setAction(SleepReminderReceiver.ACTION_SNOOZE_FIRED),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
}
