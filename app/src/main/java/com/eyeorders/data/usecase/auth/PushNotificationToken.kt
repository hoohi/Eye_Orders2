package com.eyeorders.data.usecase.auth

import android.content.Context
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.data.pushnotification.firebase.FcmToken
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PushNotificationToken @Inject constructor(
    private val prefsDataManager: PrefsDataManager,
    @ApplicationContext private val context: Context,
    private val fcmToken: FcmToken,
) {

    private val googleApiAvailability = GoogleApiAvailability.getInstance()

    suspend fun getToken(): String {
        val result = googleApiAvailability.isGooglePlayServicesAvailable(context)
        return if (result == ConnectionResult.SUCCESS) {
            fcmToken.getToken()
        } else {
            prefsDataManager.getPushToken()
        }
    }
}