package com.eyeorders.neworderschecker

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.eyeorders.util.coroutine.loop
import com.eyeorders.util.message.ToastHelper
import com.eyeorders.util.network.NetworkListener
import com.tasleem.orders.R
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class AppService : LifecycleService() {

    @Inject
    lateinit var notificationHelper: ServiceNotificationHelper

    @Inject
    lateinit var networkListener: NetworkListener

    @Inject
    lateinit var newOrderNotifier: NewOrderNotifier

    @Inject
    lateinit var toastHelper: ToastHelper

    @Inject
    lateinit var checkForNewOrdersTask: CheckForNewOrdersTask

    private var isForeground = false

    override fun onCreate() {
        super.onCreate()
        startServiceInForeground()
        startNetworkListener()
        startCheckingForOrders()
    }

    private fun startNetworkListener() {
        networkListener.bindToLifecycle(lifecycle)
        networkListener.networkStateLive.observe(this) { connected ->
            lifecycleScope.launchWhenStarted {
                if (connected) {
                    newOrderNotifier.stopAlert()
                } else {
                    newOrderNotifier.playAlert()
                    Toast.makeText(
                        this@AppService,
                        getString(R.string.internet_disconnected_msg),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun startCheckingForOrders() {
        lifecycleScope.launchWhenStarted {
            loop(TimeUnit.MINUTES.toMillis(CHECK_TIME_MINUTES)) {
                if (networkListener.isOffline().not()) {
                    checkForNewOrdersTask.execute()
                } else {
                    newOrderNotifier.playAlert()
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val stopService = intent?.extras?.getBoolean(STOP_SERVICE) ?: false
        if (stopService) {
            Timber.i("Stopping service")
            stopThisService()
        } else {
            startServiceInForeground()

        }
        return START_STICKY
    }

    private fun startServiceInForeground() {
        if (isForeground.not()) {
            startForeground(
                ServiceNotificationHelper.APP_NOTIFICATION_ID,
                notificationHelper.createServiceNotification()
            )
            isForeground = true
        }
    }

    private fun stopThisService() {
        if (isForeground) {
            stopForeground(true)
            isForeground = false
        }
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        isForeground = false
    }

    companion object {
        private const val STOP_SERVICE = "stop_service"
        private const val CHECK_TIME_MINUTES = 5L

        fun start(context: Context) {
            val intent = Intent(context, AppService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, AppService::class.java)
            intent.putExtra(STOP_SERVICE, true)
            context.startService(intent)
        }
    }
}