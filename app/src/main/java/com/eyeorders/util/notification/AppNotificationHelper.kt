package com.eyeorders.util.notification

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
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.eyeorders.screens.main.MainActivity
import com.eyeorders.screens.orderdetail.OrderDetailFragment
import com.eyeorders.screens.splash.SplashActivity
import com.tasleem.orders.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


/**
 * Helper class to manage notification channels, and create notifications.
 */
class AppNotificationHelper @Inject constructor(
    @ApplicationContext context: Context,
) :
    ContextWrapper(context) {
    private var manager: NotificationManager? = null

    /**
     * Get the small icon for this app
     *
     * @return The small icon resource id
     */
    private val smallIcon: Int
        get() = R.drawable.ic_stat_name

    private val largeIcon: Int
        get() = R.drawable.eye_orders

    init {
        createAppNotificationChannel()
    }

    private fun getManager(): NotificationManager {
        if (manager == null) {
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return manager!!
    }

    fun showOrderNotification(data: NotificationData) {
        getManager().notify(
            APP_NOTIFICATION_ID,
            createOrderNotification(data)
        )
    }

    fun showNotification(data: NotificationData) {
        getManager().notify(
            APP_NOTIFICATION_ID,
            createNotification(data.title, data.message)
        )
    }

    fun cancelOrderNotification() {
        getManager().cancel(APP_NOTIFICATION_ID)
    }

    private fun createAppNotificationChannel() {
        createNotificationChannel(
            APP_NOTIFICATION_CHANNEL,
            getString(R.string.app_name)
        )
    }


    private fun createNotificationChannel(channelId: String, channelName: String) {
        val channel: NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = NotificationChannel(
                channelId,
                channelName, NotificationManager.IMPORTANCE_HIGH
            )
            channel.lightColor = Color.GREEN
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            getManager().createNotificationChannel(channel)
        }
    }


    private fun createOrderNotification(data: NotificationData): Notification {
        return NotificationCompat.Builder(applicationContext, APP_NOTIFICATION_CHANNEL)
            .setSmallIcon(smallIcon)
            .setLargeIcon(BitmapFactory.decodeResource(resources, largeIcon))
            .setContentTitle(data.title)
            .setContentText(
                getString(
                    R.string.new_order_notification_items,
                    data.message,
                    data.orderCount.toString()
                )
            )
            .setDefaults(Notification.DEFAULT_ALL)
            .setOngoing(true)
            .setAutoCancel(true)
            .setContentIntent(createNewOrderPendingIntent(data.orderId))
            .addAction(0, getString(R.string.view_order_action), createNewOrderPendingIntent(data.orderId))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun createNewOrderPendingIntent(orderId: Int): PendingIntent {
        return createDeepLink(orderId)
            .createPendingIntent()
    }

    private fun getLauncherActivityPendingIntent(): PendingIntent {
        val resultIntent = Intent(this, SplashActivity::class.java)
        return PendingIntent.getActivity(
            this,
            0, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun createNotification(title: String, message: String): Notification {
        return createNotification(
            title,
            message,
            APP_NOTIFICATION_CHANNEL, getLauncherActivityPendingIntent()
        ).build()
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


    private fun createDeepLink(orderId: Int): NavDeepLinkBuilder {
        return NavDeepLinkBuilder(this)
            .setGraph(R.navigation.main_nav)
            .setDestination(R.id.orderDetailFragment)
            .setComponentName(MainActivity::class.java)
            .setArguments(OrderDetailFragment.makeArguments(orderId))
    }

    companion object {
        const val APP_NOTIFICATION_ID = 32
        const val APP_NOTIFICATION_CHANNEL = "order_app"
    }
}

data class NotificationData(
    val title: String = "",
    val message: String = "",
    val orderId: Int = -1,
    val orderCount: Int = 0,
)
