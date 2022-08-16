package com.android.tapcorder.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.android.tapcorder.App
import com.android.tapcorder.BuildConfig
import com.android.tapcorder.MainActivity
import com.android.tapcorder.R
import com.android.tapcorder.service.TapcorderService

object TapcorderNotification {

    private val channelId: String
        get() = App.getContext().packageName
    private val channelName: String
        get() = BuildConfig.APPLICATION_ID

    fun createNotification(context: Context): Notification {
        // 클릭 시 MainActivity 로 이동
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.action = Actions.MAIN
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent
            .getActivity(context, 0, notificationIntent, FLAG_UPDATE_CURRENT)

        // 각 버튼에 관한 Intent
        val stopIntent = Intent(context, TapcorderService::class.java)
        stopIntent.action = Actions.STOP
        val stopPendingIntent = PendingIntent
            .getService(context, 0, stopIntent, 0)

        val saveIntent = Intent(context, TapcorderService::class.java)
        saveIntent.action = Actions.SAVE
        val savePendingIntent = PendingIntent
            .getService(context, 0, saveIntent, 0)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_recording)
            .setContentTitle("TapCorder")
            .setContentText("")
            .setOngoing(true)
            .addAction(
                NotificationCompat.Action(
                    R.drawable.ic_notification_stop,
                    "stop", stopPendingIntent
                )
            )
            .addAction(
                NotificationCompat.Action(
                    R.drawable.ic_notification_save,
                    "save", savePendingIntent
                )
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .build()

        createNotificationChannel()

        return notification
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = App.getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

}