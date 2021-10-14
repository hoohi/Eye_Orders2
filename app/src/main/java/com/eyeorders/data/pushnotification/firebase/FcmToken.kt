package com.eyeorders.data.pushnotification.firebase

import com.eyeorders.data.dispatcher.AppDispatchers
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FcmToken @Inject constructor(
    private val dispatchers: AppDispatchers
) {

    private val firebaseMessaging = FirebaseMessaging.getInstance()

    suspend fun getToken(): String {
        return withContext(dispatchers.io) {
            firebaseMessaging.token.await()
        }
    }

    suspend fun deleteToken() {
        return withContext(dispatchers.io) {
            firebaseMessaging.deleteToken().await()
        }
    }
}