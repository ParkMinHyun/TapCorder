package com.android.tapcorder.device

import android.media.MediaRecorder
import android.util.Log
import com.android.tapcorder.util.ExtensionUtil.TAG
import com.android.tapcorder.util.FileUtil
import java.io.File
import java.io.IOException
import kotlin.math.roundToInt

class AudioRecorder(
    private val recorderTaskCallback: TaskCallback
) {
    private var mediaRecorder: MediaRecorder? = null
    val audioFilePath: String = FileUtil.createTempFilePath()

    private var startTime: Long = 0L
    private var endTime: Long = 0L
    val recordTime: Int by lazy {
        val duration = (endTime - startTime) / 1000f
        duration.roundToInt()
    }

    @Synchronized
    fun startRecording() {
        Log.d(TAG, "startRecording($audioFilePath)")

        try {
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setOutputFile(audioFilePath)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            }

            mediaRecorder?.prepare()
        } catch (e: Exception) {
            when(e) {
                is IOException -> Log.e(TAG, "MediaRecorder prepare failed")
                is RuntimeException -> Log.e(TAG, "MediaRecorder setting failed")
            }
            File(audioFilePath).delete()
        }

        startTime = System.currentTimeMillis()
        mediaRecorder?.start()
    }

    @Synchronized
    fun stopRecording() {
        Log.d(TAG, "stopRecording($audioFilePath)")

        // TODO error handling
        if (System.currentTimeMillis() - startTime < 1000) {
            return
        }

        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null

        endTime = System.currentTimeMillis()
        recorderTaskCallback.onRecordCompleted()
    }

    interface TaskCallback {
        fun onRecordCompleted()
    }
}