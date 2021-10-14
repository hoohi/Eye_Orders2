package com.eyeorders.data.usecase.orders

import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.data.error.ErrorHandler
import com.eyeorders.data.model.DataState
import com.eyeorders.data.model.OrderStatus
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.data.remote.api.ApiService
import com.eyeorders.data.usecase.base.processResponse
import com.eyeorders.screens.orderdetail.model.OrderDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DeclineOrderUseCase @Inject constructor(
    private val service: ApiService,
    private val prefsDataManager: PrefsDataManager,
    private val errorHandler: ErrorHandler,
    private val dispatchers: AppDispatchers,
) {

    fun execute(orderDetail: OrderDetail): Flow<DataState<OrderDetail>> = flow {
        try {
            emit(DataState.Loading)
            val userId = prefsDataManager.getUserId()

            processResponse {
                service.declineOrder(userId, orderDetail.id)
            }

            val newOrderDetail = orderDetail.copy(status = OrderStatus.VENDOR_CANCELED)
            prefsDataManager.setUpdatedOrderStatus(false)
            emit(DataState.Success(newOrderDetail))
        } catch (e: Exception) {
            prefsDataManager.setUpdatedOrderStatus(false)
            emit(DataState.Error(errorHandler.getErrorMessage(e)))
        }
    }.flowOn(dispatchers.io)
}