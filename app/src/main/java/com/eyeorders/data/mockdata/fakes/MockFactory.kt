package com.eyeorders.data.mockdata.fakes

import com.eyeorders.data.mockdata.Item
import com.eyeorders.data.mockdata.Order
import com.eyeorders.data.mockdata.OrderExtra
import com.eyeorders.data.mockdata.OrderItem
import com.eyeorders.data.mockdata.OrderMenu
import com.eyeorders.data.mockdata.User
import com.eyeorders.data.mockdata.fakes.Generators.createFakeAddress
import com.eyeorders.data.mockdata.fakes.Generators.createFakeItemName
import com.eyeorders.data.mockdata.fakes.Generators.createFakePhoneNumber
import com.eyeorders.data.mockdata.fakes.Generators.createFakeUserName
import com.eyeorders.data.mockdata.fakes.PrimitiveDataFactory.randomInt
import com.eyeorders.data.mockdata.fakes.PrimitiveDataFactory.randomString
import java.math.BigDecimal

object MockFactory {

    fun generateFakeOrder(): Order {
        return Order(
            randomString(),
            randomInt(to = 10).toString(),
            "Completed",
            "Order Now",
            "12:44",
                BigDecimal.valueOf(randomInt(from = 100, to = 1000).toDouble()),
            "ORM",
            generateFakeUser(),
            "Bring to my house on the road",
            mutableListOf<OrderItem>().apply {
                repeat(randomInt(to = 4)){
                    add(generateFakeOrderItem())
                }
            }
        )
    }

    fun generateFakeOrderExtra(): OrderExtra {
        return OrderExtra(
            randomString(),
            generateFakeItem(),
            randomInt(to = 10),
            randomInt(from = 100, to = 1000).toString()
        )
    }

    fun generateFakeOrderMenu(): OrderMenu {
        return OrderMenu(
            randomString(),
            createFakeItemName(),
           "Available"
        )
    }

    fun generateFakeOrderItem(): OrderItem {
        return OrderItem(
            randomString(),
            generateFakeItem(),
            randomInt(to = 10),
            randomInt(from = 100, to = 1000).toString(),
            mutableListOf<OrderExtra>().apply {
                repeat(randomInt(to = 4)){
                    add(generateFakeOrderExtra())
                }
            }
        )
    }

    fun generateFakeItem(): Item {
        return Item(
            randomString(),
            createFakeItemName()
        )
    }

    fun generateFakeUser(): User {
        return User(
            randomString(),
            createFakeUserName(),
            "https://source.unsplash.com/random/200x200",
            createFakePhoneNumber(),
            createFakeAddress()
        )
    }
}