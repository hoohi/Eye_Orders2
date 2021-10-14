package com.eyeorders.data.pushnotification.firebase

import android.content.Intent
import com.eyeorders.data.pushnotification.DisplayPushNotificationWorker
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("TOKEN RECEIVED: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("onMessageReceived: ${remoteMessage.data}")
        val jsonData = remoteMessage.data["data"]
        Timber.d("jsonData: $jsonData")
        val id = DisplayPushNotificationWorker.scheduleWork(this, jsonData)
        Timber.d("scheduled work: $id")
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate")
    }

    @Suppress("DEPRECATION")
    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        Timber.d("onStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("Destroyed")
    }
}
