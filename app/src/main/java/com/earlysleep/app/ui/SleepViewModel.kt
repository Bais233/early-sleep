package com.earlysleep.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.earlysleep.app.alarm.SleepAlarmScheduler
import com.earlysleep.app.data.SleepPrefs
import java.time.LocalDate
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SleepViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = SleepPrefs(application)

    val remindersEnabled = prefs.remindersEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = true
    )

    val checkInDates = prefs.checkInDates.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptySet()
    )

    init {
        // 每次启动时校正一次闹钟，防止被系统清理后不再提醒。
        viewModelScope.launch {
            if (prefs.remindersEnabled.first()) {
                SleepAlarmScheduler.scheduleDaily(getApplication())
            }
        }
    }

    fun setRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setRemindersEnabled(enabled)
            if (enabled) {
                SleepAlarmScheduler.scheduleDaily(getApplication())
            } else {
                SleepAlarmScheduler.cancel(getApplication())
            }
        }
    }

    fun checkInTonight() {
        viewModelScope.launch {
            prefs.checkIn(LocalDate.now())
        }
    }

    fun clearCheckIns() {
        viewModelScope.launch {
            prefs.clearCheckIns()
        }
    }
}
