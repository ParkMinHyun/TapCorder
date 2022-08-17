package com.android.tapcorder.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.android.tapcorder.App
import com.android.tapcorder.BuildConfig
import com.android.tapcorder.MainActivity
import com.android.tapcorder.R
import com.android.tapcorder.service.AudioRecordService

object NotificationCreator {

    private val channelId: String
        get() = App.getContext().packageName
    private val channelName: String
        get() = BuildConfig.APPLICATION_ID

    fun createNotification(context: Context): Notification {
        // 클릭 시 MainActivity 로 이동
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.action = NotificationAction.MAIN
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent
            .getActivity(context, 0, notificationIntent, FLAG_UPDATE_CURRENT)

        // 각 버튼에 관한 Intent
        val stopIntent = Intent(context, AudioRecordService::class.java)
        stopIntent.action = NotificationAction.STOP
        val stopPendingIntent = PendingIntent
            .getService(context, 0, stopIntent, 0)

        val saveIntent = Intent(context, AudioRecordService::class.java)
        saveIntent.action = NotificationAction.SAVE
        val savePendingIntent = PendingIntent
            .getService(context, 0, saveIntent, 0)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_recording)
            .setContentTitle(App.getContext().getString(R.string.app_name))
            .setContentText("")
            .setOngoing(true)
            .addAction(NotificationCompat.Action(R.drawable.ic_notification_stop, NotificationAction.STOP, stopPendingIntent))
            .addAction(NotificationCompat.Action(R.drawable.ic_notification_save, NotificationAction.SAVE, savePendingIntent))
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .build()

        createNotificationChannel()

        return notification
    }

    private fun createNotificationChannel() {
        val manager = App.getContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            channelId, channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        manager.createNotificationChannel(channel)
    }
}