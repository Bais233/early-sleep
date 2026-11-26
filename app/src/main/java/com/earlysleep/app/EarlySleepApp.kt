package com.earlysleep.app

import android.app.Application
import com.earlysleep.app.notifications.SleepNotifications

class EarlySleepApp : Application() {

    override fun onCreate() {
        super.onCreate()
        SleepNotifications.createChannel(this)
    }
}
