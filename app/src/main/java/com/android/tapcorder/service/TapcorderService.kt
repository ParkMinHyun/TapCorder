package com.android.tapcorder.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.android.tapcorder.util.Actions
import com.android.tapcorder.util.TapcorderNotification

class TapcorderService: Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START -> {
                startForegroundService()
            }
            Actions.STOP -> {
                stopForegroundService()
            }
            Actions.SAVE -> {
                Log.e(TAG, "Clicked = save")
            }
        }
        return START_STICKY
    }

    private fun startForegroundService() {
        val notification = TapcorderNotification.createNotification(this)
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun stopForegroundService() {
        stopForeground(true)
        stopSelf()
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