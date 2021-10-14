package com.eyeorders.data.pushnotification

import androidx.annotation.IntDef
import com.eyeorders.data.pushnotification.NotificationType.Companion.TYPE_DEFAULT
import com.google.gson.Gson
import timber.log.Timber
import javax.inject.Inject


@IntDef(TYPE_DEFAULT)
annotation class NotificationType {
    companion object {
        const val TYPE_DEFAULT = 8
    }
}

class NotificationParser @Inject constructor(
    private val gson: Gson
) {

    fun getNotificationExtras(data: String?): PushNotificationData? {
        Timber.d("data: $data")
        return data?.let {
            gson.fromJson(data, PushNotificationData::class.java)
        }
    }
}