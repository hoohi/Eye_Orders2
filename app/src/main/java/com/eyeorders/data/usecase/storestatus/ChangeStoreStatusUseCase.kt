package com.eyeorders.data.usecase.storestatus

import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.data.error.ErrorHandler
import com.eyeorders.data.model.DataState
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.data.remote.api.ApiService
import com.eyeorders.data.usecase.base.processResponse
import com.eyeorders.screens.storestatus.StoreStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ChangeStoreStatusUseCase @Inject constructor(
    private val service: ApiService,
    private val prefsDataManager: PrefsDataManager,
    private val errorHandler: ErrorHandler,
    private val dispatchers: AppDispatchers,
) {

    fun execute(status: StoreStatus): Flow<DataState<StoreStatus>> = flow {
        try {
            emit(DataState.Loading)
            val userId = prefsDataManager.getUserId()
            processResponse {
                service.changeStoreStatus(userId, status.ordinal)
            }
            prefsDataManager.setStoreStatus(status)
            val orderStats = prefsDataManager.getOrderStats()
            prefsDataManager.saveOrderStats(orderStats.copy(isStopOrder = status == StoreStatus.CLOSED))
            emit(DataState.Success(status))
        } catch (e: Exception) {
            emit(DataState.Error(errorHandler.getErrorMessage(e)))
        }
    }.flowOn(dispatchers.io)
}