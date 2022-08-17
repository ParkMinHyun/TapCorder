package com.android.tapcorder.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.android.tapcorder.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private const val DATA_STORE_FIELD_SETTING = "setting"
private const val AUDIO_RECORD_TIME_SETTING = "audioRecordTimeSetting"

object SettingRepository {
    private val audioRecordTimeKey by lazy { intPreferencesKey(AUDIO_RECORD_TIME_SETTING) }

    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_FIELD_SETTING)

    var audioRecordTime: Int
        get() {
            return getDataStore(audioRecordTimeKey, 10)
        }
        set(value) {
            setDataStore(audioRecordTimeKey, value)
        }

    private fun <T> getDataStore(key: Preferences.Key<T>, defaultValue: T) =
        runBlocking(Dispatchers.IO) {
            App.getContext().datastore.data.map {
                it[key] ?: defaultValue
            }.first()
        }

    private fun <T> setDataStore(key: Preferences.Key<T>, value: T) =
        runBlocking(Dispatchers.IO) {
            App.getContext().datastore.edit {
                it[key] = value
            }
        }
}