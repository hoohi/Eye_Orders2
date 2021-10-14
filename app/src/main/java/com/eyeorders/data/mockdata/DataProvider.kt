package com.eyeorders.data.mockdata

import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.data.mockdata.fakes.MockFactory
import com.eyeorders.data.mockdata.fakes.PrimitiveDataFactory.randomInt
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DataProvider @Inject constructor(
    private val dispatchers: AppDispatchers
) {

    suspend fun getNewOrders(): List<Order> {
        return withContext(dispatchers.io) {
            val orders = mutableListOf<Order>()
            repeat(randomInt(from = 1, to = 2)) {
                orders.add(MockFactory.generateFakeOrder())
            }
            orders
        }
    }

    suspend fun getOrderMenus(): List<OrderMenu> {
        return withContext(dispatchers.io) {
            val orders = mutableListOf<OrderMenu>()
            repeat(randomInt(from = 2, to = 10)) {
                orders.add(MockFactory.generateFakeOrderMenu())
            }
            orders
        }
    }

    suspend fun getAcceptedOrders(): List<Order> {
        return withContext(dispatchers.io) {
            val orders = mutableListOf<Order>()
            repeat(randomInt(from = 3, to = 20)) {
                orders.add(MockFactory.generateFakeOrder())
            }
            orders
        }

    }
}