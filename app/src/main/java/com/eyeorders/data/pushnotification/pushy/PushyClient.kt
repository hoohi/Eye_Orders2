package com.eyeorders.data.pushnotification.pushy

import android.app.Activity
import android.content.Context
import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.data.prefs.PrefsDataManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.pushy.sdk.Pushy
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushyClient @Inject constructor(
    private val prefsDataManager: PrefsDataManager,
    private val dispatchers: AppDispatchers,
    @ApplicationContext private val context: Context,
) {

    fun initPusher() {
        GlobalScope.launch {
            withContext(dispatchers.io) {
                if (!Pushy.isRegistered(context)) {
                    val token = Pushy.register(context)
                    Timber.d("Gotten pushy token: $token")
                    prefsDataManager.setPushToken(token)
                }
            }
        }
    }

    fun listen(activity: Activity) {
        Pushy.listen(activity)
    }

    fun subscribeUserToTopic(topicId: String) {
        if (topicId.isNotEmpty()) {
            GlobalScope.launch {
                withContext(dispatchers.io) {
                    Pushy.subscribe(topicId, context)
                }
            }
        }
    }

    fun unSubscribeUserFromTopic(topicId: String) {
        if (topicId.isNotEmpty()) {
            GlobalScope.launch {
                withContext(dispatchers.io) {
                    Pushy.unsubscribe(topicId, context)
                }
            }
        }
    }

}