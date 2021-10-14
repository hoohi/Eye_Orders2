package com.eyeorders.screens.orderdetail

import com.eyeorders.screens.orderdetail.model.OrderDetail

data class OrderDetailViewState(
    val orderDetail: OrderDetail,
    val loading: Boolean = false,
    val success:Boolean = false,
    val errorMessage: String? = null,
)