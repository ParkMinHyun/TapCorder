package com.android.tapcorder.data.audio

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AudioData(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "duration") val duration: Int,
    @ColumnInfo(name = "date") val date: String
)