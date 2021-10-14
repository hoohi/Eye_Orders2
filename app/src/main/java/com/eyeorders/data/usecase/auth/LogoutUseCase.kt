package com.eyeorders.data.usecase.auth

import android.content.Context
import androidx.room.withTransaction
import androidx.work.WorkManager
import com.eyeorders.data.cache.AppDatabase
import com.eyeorders.data.error.ErrorHandler
import com.eyeorders.data.model.DataState
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.data.pushnotification.firebase.FcmToken
import com.eyeorders.data.pushnotification.pushy.PushyClient
import com.eyeorders.neworderschecker.AppService
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefsDataManager: PrefsDataManager,
    private val errorHandler: ErrorHandler,
    private val fcmToken: FcmToken,
    private val appDatabase: AppDatabase,
    private val firebaseAuth: FirebaseAuth,
    private val workManager: WorkManager,
    private val pushyClient: PushyClient,
) {
    fun execute(): Flow<DataState<Unit>> = flow {
        try {
            emit(DataState.Loading)
            fcmToken.deleteToken()
            prefsDataManager.clearAll()
            appDatabase.withTransaction {
                appDatabase.menuProductKeyDao().clearRemoteKeys()
                appDatabase.menuProductDao().clearProducts()
            }
            val channelId = prefsDataManager.getPushChannelId()
            pushyClient.unSubscribeUserFromTopic(channelId)
            firebaseAuth.signOut()
            workManager.cancelAllWork()
            AppService.stop(context)
            emit(DataState.Success(Unit))
        } catch (e: Exception) {
            emit(DataState.Error(errorHandler.getErrorMessage(e)))
        }
    }
}