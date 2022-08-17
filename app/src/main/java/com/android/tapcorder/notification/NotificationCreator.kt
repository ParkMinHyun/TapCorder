package com.android.tapcorder.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
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
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            action = NotificationAction.MAIN
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val stopIntent = Intent(context, AudioRecordService::class.java).apply {
            action = NotificationAction.STOP
        }
        val saveIntent = Intent(context, AudioRecordService::class.java).apply {
            action = NotificationAction.SAVE
        }

        val stopPendingIntent =
            PendingIntent.getService(context, 0, stopIntent, getPendingIntentFlags())
        val savePendingIntent =
            PendingIntent.getService(context, 0, saveIntent, getPendingIntentFlags())

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_recording)
            .setContentTitle(App.getContext().getString(R.string.app_name))
            .setContentText("")
            .setOngoing(true)
            .addAction(NotificationCompat.Action(R.drawable.ic_notification_stop, NotificationAction.STOP, stopPendingIntent))
            .addAction(NotificationCompat.Action(R.drawable.ic_notification_save, NotificationAction.SAVE, savePendingIntent))
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, getPendingIntentFlags()))
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

    private fun getPendingIntentFlags(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            FLAG_IMMUTABLE
        } else {
            FLAG_UPDATE_CURRENT
        }
    }
}