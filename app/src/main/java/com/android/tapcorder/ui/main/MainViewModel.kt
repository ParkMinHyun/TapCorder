package com.android.tapcorder.ui.main

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.tapcorder.util.FileUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var mediaPlayer: MediaPlayer

    private var audioFileName: String? = null

    var isAudioRecording = false
        private set

    var isAudioPlaying = false
        private set

    private val _recordedAudioLiveData = MutableLiveData<Uri>()
    val recordedAudioLiveData = _recordedAudioLiveData

    @SuppressLint("SimpleDateFormat")
    fun startRecording() {
        audioFileName = FileUtil.createFileName()
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(audioFileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        }

        try {
            mediaRecorder.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        isAudioRecording = true
        mediaRecorder.start()
    }

    fun stopRecording() {
        mediaRecorder.stop()
        mediaRecorder.release()

        isAudioRecording = false
        _recordedAudioLiveData.value = Uri.parse(audioFileName)
    }

    fun playAudio(file: File) {
        isAudioPlaying = true
        mediaPlayer = MediaPlayer().apply {
            setOnCompletionListener {
                stopAudio()
            }
            setDataSource(file.absolutePath)
            prepare()
            start()
        }
    }

    fun stopAudio() {
        isAudioPlaying = false
        mediaPlayer.stop()
    }
}