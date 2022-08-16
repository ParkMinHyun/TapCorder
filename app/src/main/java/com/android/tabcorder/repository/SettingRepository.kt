package com.android.tabcorder.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.android.tabcorder.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private const val DATA_STORE_FIELD_SETTING = "setting"
private const val RECORD_TIME_SETTING = "recordTimeSetting"

object SettingRepository {
    private val recordTimeKey by lazy { intPreferencesKey(RECORD_TIME_SETTING) }

    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_FIELD_SETTING)

    var recordTime: Int
        get() {
            return getDataStore(recordTimeKey, 10)
        }
        set(value) {
            setDataStore(recordTimeKey, value)
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