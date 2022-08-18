package com.android.tapcorder.ui.main

import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tapcorder.App
import com.android.tapcorder.data.player.PlayerDuration
import com.android.tapcorder.notification.NotificationAction
import com.android.tapcorder.service.AudioRecordService
import com.android.tapcorder.util.ExtensionUtil.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private lateinit var mediaPlayer: MediaPlayer

    private val _audioPlayLiveData = MutableLiveData<PlayerDuration>()
    val audioPlayLiveData = _audioPlayLiveData

    private var isPauseRequested = false

    fun startRecordService() = with(App.getContext()) {
        Log.d(TAG, "startRecordService")

        startForegroundService(
            Intent(this, AudioRecordService::class.java).apply {
                action = NotificationAction.START
            }
        )
    }

    fun stopRecordService() = with(App.getContext()) {
        Log.d(TAG, "startRecordService")

        startForegroundService(
            Intent(this, AudioRecordService::class.java).apply {
                action = NotificationAction.STOP
            }
        )
    }

    @Synchronized
    fun playAudio(file: File, onCompleted: () -> Unit) {
        Log.d(TAG, "playAudio ${file.name}")

        if (this::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            stopAudio()
        }

        mediaPlayer = MediaPlayer().apply {
            setOnCompletionListener { onCompleted.invoke() }
            setDataSource(file.absolutePath)
            prepare()
            start()
        }

        viewModelScope.launch(Dispatchers.Default) {
            notifyAudioProgressChanged()
        }
    }

    private suspend fun notifyAudioProgressChanged() {
        while (mediaPlayer.isPlaying) {
            if (isPauseRequested || !mediaPlayer.isPlaying) {
                return
            }

            delay(50)
            withContext(Dispatchers.Main) {
                _audioPlayLiveData.value = PlayerDuration(mediaPlayer.currentPosition, mediaPlayer.duration)
            }
        }
    }

    @Synchronized
    fun stopAudio() {
        Log.d(TAG, "stopAudio")

        mediaPlayer.stop()
        mediaPlayer.reset()
    }

    @Synchronized
    fun pauseAudio() {
        Log.d(TAG, "pauseAudio")

        isPauseRequested = true
        mediaPlayer.pause()
    }

    @Synchronized
    fun moveAudioPosition(progress: Int) {
        Log.d(TAG, "moveAudioPosition")

        mediaPlayer.seekTo(progress)
        mediaPlayer.start()

        isPauseRequested = false
        viewModelScope.launch(Dispatchers.Default) {
            notifyAudioProgressChanged()
        }
    }
}