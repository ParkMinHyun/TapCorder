package com.android.tapcorder.ui.main

import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.tapcorder.util.ExtensionUtil.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private var mediaPlayer: MediaPlayer? = null

    private val _recordedAudioLiveData = MutableLiveData<Uri>()
    val recordedAudioLiveData = _recordedAudioLiveData

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