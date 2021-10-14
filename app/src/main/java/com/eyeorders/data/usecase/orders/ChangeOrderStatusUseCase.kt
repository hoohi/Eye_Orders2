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

class ChangeOrderStatusUseCase @Inject constructor(
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
                when (orderDetail.status) {
                    OrderStatus.PENDING -> service.confirmOrder(
                        userId,
                        orderDetail.id,
                        DEFAULT_MIN,
                        DEFAULT_ORDER_VERSION
                    )
                    OrderStatus.VENDOR_CONFIRM,
                    OrderStatus.DRIVER_PENDING -> {
                        service.markOrderAsReady(
                            userId, orderDetail.id
                        )
                    }
                    else -> throw IllegalStateException("Unexpected status: ${orderDetail.status}")
                }
            }

            val newOrderDetail = when (orderDetail.status) {
                OrderStatus.PENDING -> orderDetail.copy(status = OrderStatus.VENDOR_CONFIRM)
                OrderStatus.VENDOR_CONFIRM, OrderStatus.DRIVER_PENDING -> orderDetail.copy(status = OrderStatus.NEW_ORDER_READY)
                else -> throw IllegalStateException("Unexpected status: ${orderDetail.status}")
            }
            prefsDataManager.setUpdatedOrderStatus(true)
            emit(DataState.Success(newOrderDetail))
        } catch (e: Exception) {
            prefsDataManager.setUpdatedOrderStatus(false)
            emit(DataState.Error(errorHandler.getErrorMessage(e)))
        }
    }.flowOn(dispatchers.io)

    companion object {
        private const val DEFAULT_MIN = 10
        private const val DEFAULT_ORDER_VERSION = 2
    }
}