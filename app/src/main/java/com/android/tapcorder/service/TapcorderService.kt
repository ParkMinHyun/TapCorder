package com.android.tapcorder.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.android.tapcorder.App
import com.android.tapcorder.device.AudioRecorder
import com.android.tapcorder.repository.SettingRepository
import com.android.tapcorder.util.Actions
import com.android.tapcorder.util.ExtensionUtil.TAG
import com.android.tapcorder.util.TapcorderNotification
import kotlinx.coroutines.*
import java.io.File
import java.util.*
import kotlin.concurrent.timer

class TapcorderService: Service() {

    private val audioRecorderQueue = LinkedList<AudioRecorder>()
    private lateinit var timer: Timer

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private val serviceLock = Any()

    private var isServiceRunning = false

    private val audioRecorderTaskCallback = object : AudioRecorder.TaskCallback {
        override fun onRecordCompleted() {
            if (!isServiceRunning) {
                Log.w(TAG, "onRecordCompleted - service was stopped")
                return
            }

            synchronized(serviceLock) {
                Log.d(TAG, "onRecordCompleted1 - audioRecordQueue.size:${audioRecorderQueue.size}, SettingRepository.audioRecordTime${SettingRepository.audioRecordTime}")

                if (audioRecorderQueue.size >= SettingRepository.audioRecordTime) {
                    val oldestAudioRecorder = audioRecorderQueue.pop()
                    val oldestAudioRecordName = oldestAudioRecorder.audioFileName

                    File(oldestAudioRecordName).delete()
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            Actions.START -> startService()
            Actions.STOP -> stopService()
            Actions.SAVE -> saveAudioRecord()
        }
        return START_STICKY
    }

    private fun startService() {
        Log.d(TAG, "startService")

        isServiceRunning = true
        startForeground(NOTIFICATION_ID, TapcorderNotification.createNotification(this))
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
                File(audioRecorder.audioFileName).delete()
            }
            audioRecorderQueue.clear()
        }
    }

    private fun saveAudioRecord() {
        Log.d(TAG, "saveAudioRecord")

        synchronized(serviceLock) {
            with(audioRecorderQueue.pop()) {
                Log.i(TAG, "saveAudioRecord - $audioFileName is saved")
                stopRecording()
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        const val TAG = "TapcorderService"
        const val NOTIFICATION_ID = 1516
    }
}