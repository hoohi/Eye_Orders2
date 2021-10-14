package com.eyeorders.data.usecase.storestatus

import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.data.error.ErrorHandler
import com.eyeorders.data.model.DataState
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.screens.storestatus.StoreStatus
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

class GetStoreStatusUseCase @Inject constructor(
    private val prefsDataManager: PrefsDataManager,
    private val errorHandler: ErrorHandler,
    private val dispatchers: AppDispatchers,
) {

    fun execute(): Flow<DataState<StoreStatus>> = flow {
        try {
            emit(DataState.Loading)
            val orderStats = prefsDataManager.getOrderStats()
            Timber.d("GetStoreStatusUseCase: $orderStats")
            if (orderStats.isStopOrder) {
                prefsDataManager.setStoreStatus(StoreStatus.CLOSED)
            } else {
                prefsDataManager.setStoreStatus(StoreStatus.OPEN)
            }
            emitAll(prefsDataManager.storeStatus().map {
                DataState.Success(it)
            })
        } catch (e: Exception) {
            emit(DataState.Error(errorHandler.getErrorMessage(e)))
        }
    }.flowOn(dispatchers.io)
}