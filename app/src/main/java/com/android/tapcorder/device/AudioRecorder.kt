package com.android.tapcorder.device

import android.media.MediaRecorder
import android.util.Log
import com.android.tapcorder.util.ExtensionUtil.TAG
import com.android.tapcorder.util.FileUtil
import java.io.File
import java.io.IOException

class AudioRecorder(
    private val recorderTaskCallback: TaskCallback
) {
    private var mediaRecorder: MediaRecorder? = null
    val audioFileName: String = FileUtil.createFileName()

    fun startRecording() {
        Log.d(TAG, "startRecording($audioFileName)")

        try {
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setOutputFile(audioFileName)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            }

            mediaRecorder?.prepare()
        } catch (e: Exception) {
            when(e) {
                is IOException -> Log.e(TAG, "MediaRecorder prepare failed")
                is RuntimeException -> Log.e(TAG, "MediaRecorder setting failed")
            }
            File(audioFileName).delete()
        }

        mediaRecorder?.start()
    }

    fun stopRecording() {
        Log.d(TAG, "stopRecording($audioFileName)")

        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null

        recorderTaskCallback.onRecordCompleted()
    }

    interface TaskCallback {
        fun onRecordCompleted()
    }
}