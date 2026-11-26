package com.earlysleep.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sleepDataStore: DataStore<Preferences> by preferencesDataStore(name = "sleep_prefs")

class SleepPrefs(context: Context) {

    private val dataStore = context.applicationContext.sleepDataStore

    val remindersEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_REMINDER_ENABLED] ?: true
    }

    val checkInDates: Flow<Set<LocalDate>> = dataStore.data.map { prefs ->
        prefs[KEY_CHECK_INS].orEmpty()
            .mapNotNull { raw -> runCatching { LocalDate.parse(raw) }.getOrNull() }
            .toSet()
    }

    suspend fun setRemindersEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_REMINDER_ENABLED] = enabled }
    }

    suspend fun checkIn(date: LocalDate = LocalDate.now()) {
        dataStore.edit { prefs ->
            val dates = prefs[KEY_CHECK_INS].orEmpty().toMutableSet()
            dates += date.toString()
            prefs[KEY_CHECK_INS] = dates
        }
    }

    suspend fun clearCheckIns() {
        dataStore.edit { prefs -> prefs.remove(KEY_CHECK_INS) }
    }

    private companion object {
        val KEY_REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val KEY_CHECK_INS = stringSetPreferencesKey("check_in_dates")
    }
}
