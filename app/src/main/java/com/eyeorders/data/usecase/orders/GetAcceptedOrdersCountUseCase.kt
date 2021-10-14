package com.eyeorders.data.usecase.orders

import com.eyeorders.data.orderstats.OrderStatsChangeObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAcceptedOrdersCountUseCase @Inject constructor(
    private val orderStatsChangeObserver: OrderStatsChangeObserver,
) {

    fun execute(): Flow<String> {
        //you could wrap in data state and
        // handle error but I feel it is overkill for this use case
        return orderStatsChangeObserver.observeOrderStatsChange().map {
            it.acceptedOrder
        }
    }
}