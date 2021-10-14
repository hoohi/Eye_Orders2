package com.eyeorders.neworderschecker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.eyeorders.screens.splash.SplashActivity
import com.tasleem.orders.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


/**
 * Helper class to manage notification channels, and create notifications.
 */
class ServiceNotificationHelper @Inject constructor(@ApplicationContext context: Context) : ContextWrapper(context) {
    private var manager: NotificationManager? = null

    /**
     * Get the small icon for this app
     *
     * @return The small icon resource id
     */
    private val smallIcon: Int
        get() = R.drawable.ic_stat_name

    private val largeIcon: Int
        get() = R.drawable.ic_stat_name

    init {
        createAppNotificationChannel()
    }

    private fun createAppNotificationChannel() {
        createNotificationChannel(
            APP_NOTIFICATION_CHANNEL,
            getString(R.string.service_notification_channel_desc)
        )
    }


    private fun createNotificationChannel(channelId: String, channelName: String) {
        val channel: NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = NotificationChannel(
                channelId,
                channelName, NotificationManager.IMPORTANCE_DEFAULT
            )

            channel.lightColor = Color.GREEN
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            getManager().createNotificationChannel(channel)
        }
    }

    private fun createForPostLollipop(): Notification {
        return NotificationCompat.Builder(applicationContext, APP_NOTIFICATION_CHANNEL)
            .setSmallIcon(smallIcon)
            .setContentIntent(getLauncherActivityPendingIntent())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCustomContentView(createFlashNotificationView())
            .build()
    }

    private fun createForPreLollipop(): Notification {
        return createNotification(
            getString(R.string.app_name),
            getString(R.string.notification_text),
            APP_NOTIFICATION_CHANNEL, getLauncherActivityPendingIntent()
        ).build()
    }


    fun createServiceNotification(): Notification {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createForPostLollipop()
        } else {
            createForPreLollipop()
        }
    }

    private fun createFlashNotificationView(): RemoteViews {
        return RemoteViews(packageName, R.layout.layout_service_notification)
    }


    private fun getLauncherActivityPendingIntent(): PendingIntent {
        val resultIntent = Intent(this, SplashActivity::class.java)
        return PendingIntent.getActivity(
            this,
            0 /* Request code */, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    }

    private fun createNotification(
        title: String,
        body: String,
        channelId: String,
        resultPendingIntent: PendingIntent
    ): NotificationCompat.Builder {
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(
                applicationContext,
                channelId
            ).setContentIntent(resultPendingIntent)
                .setSmallIcon(smallIcon)
                .setLargeIcon(BitmapFactory.decodeResource(resources, largeIcon))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(title)
                .setContentText(body)
        builder.priority = NotificationCompat.PRIORITY_MAX
        return builder
    }


    private fun getManager(): NotificationManager {
        if (manager == null) {
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return manager!!
    }


    companion object {
        const val APP_NOTIFICATION_ID = 1223
        const val APP_NOTIFICATION_CHANNEL = "foreground_service"
    }
}
