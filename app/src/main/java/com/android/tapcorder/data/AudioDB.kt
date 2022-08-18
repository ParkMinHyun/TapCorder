package com.android.tapcorder.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.android.tapcorder.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

private const val AUDIO_DB_NAME = "audio.db"

@Database(entities = [AudioData::class], version = 1)
abstract class AudioDB : RoomDatabase() {

    abstract fun audioDao(): AudioDao

    companion object {
        private var INSTANCE: AudioDB? = null

        private fun getInstance(): AudioDB {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    App.getContext(),
                    AudioDB::class.java,
                    AUDIO_DB_NAME
                ).build()
            }
            return INSTANCE as AudioDB
        }

        fun insertAudioData(audioData: AudioData) = runBlocking(Dispatchers.IO) {
            getInstance().audioDao().insertAudioData(audioData)
        }

        fun getSavedAudioData(): List<AudioData> = runBlocking(Dispatchers.IO) {
            getInstance().audioDao().getAll()
        }

        fun getAudioData(audioName: String): AudioData = runBlocking(Dispatchers.IO) {
            getInstance().audioDao().findByResult(audioName)
        }

        fun deleteAudioData(audioData: AudioData) = runBlocking(Dispatchers.IO) {
            getInstance().audioDao().delete(audioData)
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}