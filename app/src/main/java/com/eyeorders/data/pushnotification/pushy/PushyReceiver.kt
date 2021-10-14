package com.eyeorders.data.pushnotification.pushy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.eyeorders.data.analytics.AnalyticEvent
import com.eyeorders.data.analytics.Analytics
import com.eyeorders.data.pushnotification.DisplayPushNotificationWorker
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PushyReceiver : BroadcastReceiver() {

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var analytics: Analytics

    override fun onReceive(context: Context, intent: Intent) {
        val id = DisplayPushNotificationWorker.scheduleWork(context, intent.getJsonData())
        Timber.d("scheduled work: $id")
        analytics.logEvent(AnalyticEvent.ReceivePushyNotification)
    }

    private fun Intent.getJsonData(): String {
        val dataMap = mutableMapOf<String, Any?>()
        extras?.keySet()?.forEach {
            dataMap[it] = extras?.get(it)
        }
        return gson.toJson(dataMap)
    }
}