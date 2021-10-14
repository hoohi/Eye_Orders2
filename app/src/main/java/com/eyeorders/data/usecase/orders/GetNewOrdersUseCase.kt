package com.eyeorders.data.usecase.orders

import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.data.error.ErrorHandler
import com.eyeorders.data.model.DataState
import com.eyeorders.data.model.OrderStatus
import com.eyeorders.data.model.order.OrderResponse
import com.eyeorders.data.orderstats.OrderStats
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.data.remote.api.ApiService
import com.eyeorders.data.usecase.base.processOrdersResponse
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetNewOrdersUseCase @Inject constructor(
    private val prefsDataManager: PrefsDataManager,
    private val service: ApiService,
    private val dispatchers: AppDispatchers,
    private val errorHandler: ErrorHandler,
) {

    suspend fun execute(): DataState<List<OrderResponse>> {
        return withContext(dispatchers.io) {
            try {
                val userId = prefsDataManager.getUserId()
                val result = processOrdersResponse {
                    service.getOrders(userId, OrderStatus.PENDING.toString(), 0)
                }
                prefsDataManager.saveOrderStats(
                    OrderStats(
                        result.isStopOrder,
                        result.completedOrders,
                        result.cancelledOrders,
                        result.newOrder,
                        result.acceptedOrder,
                    )
                )
                DataState.Success(result.data)
            } catch (e: Exception) {
                DataState.Error(errorHandler.getErrorMessage(e))
            }
        }
    }
}