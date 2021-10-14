package com.eyeorders.data.usecase.auth

import com.eyeorders.data.error.ErrorHandler
import com.eyeorders.data.model.DataState
import com.eyeorders.data.orderstats.OrderStats
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.data.pushnotification.pushy.PushyClient
import com.eyeorders.data.remote.api.ApiService
import com.eyeorders.data.usecase.base.processResponse
import com.eyeorders.data.usecase.workhours.SyncWorkHoursUseCase
import com.eyeorders.util.StringResource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val service: ApiService,
    private val prefsDataManager: PrefsDataManager,
    private val errorHandler: ErrorHandler,
    private val pushNotificationToken: PushNotificationToken,
    private val stringResource: StringResource,
    private val firebaseAuth: FirebaseAuth,
    private val syncWorkHoursUseCase: SyncWorkHoursUseCase,
    private val pushyClient: PushyClient,
) {
    fun execute(email: String, password: String): Flow<DataState<Unit>> = flow {
        try {
            emit(DataState.Loading)
            val token = pushNotificationToken.getToken()
            val data = processResponse {
                service.login(email, password, token, DEVICE_PLATFORM)
            }.first()

            prefsDataManager.setLoggedIn(true)
            prefsDataManager.saveUser(data.userRetailer)
            prefsDataManager.saveOrderStats(
                OrderStats(
                    isStopOrder = data.userRetailer.isStopOrder
                )
            )
            prefsDataManager.setAuthToken(data.user.apiToken)
            prefsDataManager.setPushChannelId(data.user.channelId)
            pushyClient.subscribeUserToTopic(data.user.channelId)
            firebaseAuth.signInAnonymously().await()
            syncWorkHoursUseCase.execute()
            emit(DataState.Success(Unit))
        } catch (e: Exception) {
            emit(DataState.Error(errorHandler.getErrorMessage(e)))
        }
    }

    companion object {
        private const val DEVICE_PLATFORM = "android"
    }
}