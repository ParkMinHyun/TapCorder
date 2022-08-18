package com.android.tapcorder.ui.main

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.tapcorder.App
import com.android.tapcorder.notification.NotificationAction
import com.android.tapcorder.service.AudioRecordService
import com.android.tapcorder.util.ExtensionUtil.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private var mediaPlayer: MediaPlayer? = null

    private val _recordedAudioLiveData = MutableLiveData<Uri>()
    val recordedAudioLiveData = _recordedAudioLiveData

    fun startRecordService() {
        Log.d(TAG, "startRecordService")

        with(App.getContext()) {
            startForegroundService(Intent(this,
                AudioRecordService::class.java).apply {
                    action = NotificationAction.START
                }
            )
        }
    }

    fun stopRecordService() {
        Log.d(TAG, "stopRecordService")

        with(App.getContext()) {
            startForegroundService(
                Intent(this, AudioRecordService::class.java).apply {
                    action = NotificationAction.STOP
                }
            )
        }
    }

    @Synchronized
    fun playAudio(file: File, onCompleted: () -> Unit) {
        Log.d(TAG, "playAudio ${file.name}")

        if (mediaPlayer?.isPlaying == true) {
            stopAudio()
        }

        mediaPlayer = MediaPlayer().apply {
            setOnCompletionListener { onCompleted.invoke() }
            setDataSource(file.absolutePath)
            prepare()
            start()
        }
    }

    @Synchronized
    fun stopAudio() {
        Log.d(TAG, "stopAudio")

        mediaPlayer?.stop()
        mediaPlayer?.reset()
    }
}