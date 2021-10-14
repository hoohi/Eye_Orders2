package com.eyeorders.data.eventbus.event

data class NewOrderEvent(
    val type: Int,
    val orderId: Int,
)