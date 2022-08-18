package com.android.tapcorder.data.audio

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AudioDao {

    @Query("SELECT * FROM audiodata")
    fun getAll(): List<AudioData>

    @Insert
    fun insertAudioData(audioData: AudioData)

    @Query("SELECT * FROM audiodata WHERE name LIKE :result LIMIT 1")
    fun findByResult(result: String): AudioData

    @Delete
    fun delete(audioData: AudioData)

    @Query("DELETE FROM audiodata")
    fun deleteAll()
}