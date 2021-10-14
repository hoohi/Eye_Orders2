package com.eyeorders.screens.orderdetail

sealed class OrderDetailIntent {
    data class LoadInitial(val orderId: Int) : OrderDetailIntent()
    object ChangeOrderStatus : OrderDetailIntent()
    object DeclineOrder : OrderDetailIntent()
}