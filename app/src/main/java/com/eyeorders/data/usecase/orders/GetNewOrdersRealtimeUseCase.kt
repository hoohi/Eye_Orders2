package com.eyeorders.data.usecase.orders

import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.data.error.ErrorHandler
import com.eyeorders.data.model.DataState
import com.eyeorders.data.model.order.neworder.NewOrderItem
import com.eyeorders.data.remote.firebase.FirebaseSource
import com.eyeorders.screens.activeorders.neworders.NewOrder
import com.eyeorders.util.extension.formatMoney
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GetNewOrdersRealtimeUseCase @Inject constructor(
    private val firebaseSource: FirebaseSource,
    private val errorHandler: ErrorHandler,
    private val dispatchers: AppDispatchers,
) {

    fun execute(): Flow<DataState<List<NewOrder>>> {
        return firebaseSource.getNewOrders()
            .map<List<NewOrderItem>, DataState<List<NewOrder>>> { list ->
                DataState.Success(
                    list.map { product ->
                        NewOrder(
                            id = product.id,
                            orderNumber = product.orderNum.toString(),
                            orderStatus = product.orderStatus,
                            customerName = product.customerName,
                            itemCount = product.products.size.toString(),
                            price = product.totalPrice.formatMoney()
                        )
                    }
                )
            }
            .onStart { emit(DataState.Loading) }
            .catch { emit(DataState.Error(errorHandler.getErrorMessage(it))) }
            .flowOn(dispatchers.io)
    }
}