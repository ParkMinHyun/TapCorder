package com.android.tapcorder.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.tapcorder.App
import com.android.tapcorder.Constant.INTENT_AUDIO_DATA
import com.android.tapcorder.Constant.INTENT_NOTIFY_SAVE_AUDIO
import com.android.tapcorder.data.audio.AudioData
import com.android.tapcorder.device.AudioRecorder
import com.android.tapcorder.notification.NotificationAction
import com.android.tapcorder.notification.NotificationCreator
import com.android.tapcorder.repository.AudioRepository
import com.android.tapcorder.repository.SettingRepository
import com.android.tapcorder.util.ExtensionUtil.TAG
import com.android.tapcorder.util.FileUtil
import kotlinx.coroutines.*
import java.io.File
import java.util.*
import kotlin.concurrent.timer

class AudioRecordService: Service() {

    private val audioRecorderQueue = LinkedList<AudioRecorder>()
    private lateinit var timer: Timer

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private val serviceLock = Any()

    private val audioRecorderTaskCallback = object : AudioRecorder.TaskCallback {
        override fun onRecordCompleted() {
            if (!isServiceRunning) {
                Log.w(TAG, "onRecordCompleted - service was stopped")
                return
            }

            synchronized(serviceLock) {
                if (audioRecorderQueue.size >= SettingRepository.audioRecordTime) {
                    with(audioRecorderQueue.pop()) {
                        File(audioFilePath).delete()
                        Log.w(TAG, "onRecordCompleted - $audioFilePath is deleted")
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            NotificationAction.START -> startService()
            NotificationAction.STOP -> stopService()
            NotificationAction.SAVE -> saveAudioRecord()
        }
        return START_STICKY
    }

    private fun startService() {
        Log.d(TAG, "startService")

        isServiceRunning = true
        startForeground(NOTIFICATION_ID, NotificationCreator.createNotification(this))
        startAudioRecordingTimer()
        App.getCurrentActivity().finish()
    }

    private fun startAudioRecordingTimer() {
        Log.d(TAG, "startAudioRecordingTimer")

        timer = timer(period = 1000L) {
            synchronized(serviceLock) {
                startAudioRecordingTask()
            }
        }
    }

    private fun startAudioRecordingTask() {
        Log.d(TAG, "startAudioRecordingTask")

        serviceScope.launch {
            audioRecorderQueue.offer(AudioRecorder(audioRecorderTaskCallback))

            with(audioRecorderQueue.last) {
                startRecording()
                delay(SettingRepository.audioRecordTime * 1000L)
                stopRecording()
            }
        }
    }

    private fun stopService() {
        Log.d(TAG, "stopService")

        isServiceRunning = false
        stopForeground(true)
        stopSelf()
        stopAudioRecordingTask()
    }

    private fun stopAudioRecordingTask() {
        Log.d(TAG, "stopAudioRecordingTask")

        synchronized(serviceLock) {
            serviceScope.cancel()
            timer.cancel()

            for (audioRecorder in audioRecorderQueue) {
                File(audioRecorder.audioFilePath).delete()
            }
            audioRecorderQueue.clear()
        }
    }

    private fun saveAudioRecord() {
        Log.d(TAG, "saveAudioRecord")

        synchronized(serviceLock) {
            if (audioRecorderQueue.isEmpty()) {
                return
            }

            with(audioRecorderQueue.pop()) {
                stopRecording()

                val saveFilePath = FileUtil.createSaveFilePath()
                val saveFileName = saveFilePath.split('/').last()
                val saveFileDate = audioFilePath.split('/').last().split('.').first()

                File(audioFilePath).copyTo(File(saveFilePath))
                File(audioFilePath).delete()

                Log.i(TAG, "saveAudioRecord - $saveFilePath(${recordTime}s) is saved")

                AudioRepository.insertAudioData(
                    AudioData(
                    saveFileName,
                    recordTime,
                    saveFileDate)
                )

                sendAudioData(saveFileName)
            }
        }
    }

    private fun sendAudioData(saveFileName: String) {
        if (App.getCurrentActivity().isDestroyed) {
            return
        }

        LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(
            Intent(INTENT_NOTIFY_SAVE_AUDIO).apply {
                putExtra(INTENT_AUDIO_DATA, saveFileName)
            }
        )
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {
        const val NOTIFICATION_ID = 1516

        var isServiceRunning = false
            private set
    }
}