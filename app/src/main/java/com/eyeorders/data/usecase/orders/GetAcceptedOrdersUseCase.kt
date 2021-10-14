package com.eyeorders.data.usecase.orders

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.eyeorders.data.datasource.OrdersDataSource
import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.data.error.ErrorHandler
import com.eyeorders.data.model.OrderStatus
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.data.remote.api.ApiService
import com.eyeorders.screens.activeorders.acceptedorders.AcceptedOrder
import com.eyeorders.util.date.DateFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAcceptedOrdersUseCase @Inject constructor(
    private val apiService: ApiService,
    private val prefsDataManager: PrefsDataManager,
    private val errorHandler: ErrorHandler,
    private val dispatchers: AppDispatchers,
    private val dateFormatter: DateFormatter,
) {

    fun execute(): Flow<PagingData<AcceptedOrder>> {
        return Pager(
            config = PagingConfig(PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            pagingSourceFactory = {
                OrdersDataSource(
                    apiService, prefsDataManager, errorHandler,
                    "${OrderStatus.VENDOR_CONFIRM},${OrderStatus.DRIVER_PENDING}"
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { product ->
                AcceptedOrder(
                    id = product.id,
                    orderNumber = product.orderNum.toString(),
                    status = product.status,
                    orderCount = product.products.size,
                    orderStatus = product.orderStatus,
                    driverName = product.driver,
                    customerName = product.customer,
                    orderDate = dateFormatter.formatDateTime(product.dateCreated)
                )
            }
        }.flowOn(dispatchers.io)
    }

    companion object {
        private const val PAGE_SIZE = 15
        private const val PRE_FETCH_DISTANCE = 1
    }
}