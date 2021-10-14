package com.eyeorders.screens.orderdetail.model

import android.os.Parcelable
import com.eyeorders.data.model.OrderStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderDetail(
    val id: Int = 0,
    val customerAvatarPath: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val customerThumbPath: String = "",
    val customerZone: String = "",
    val dateAccepted: Long = 0L,
    val dateCreated: Long = 0L,
    val deliveryPrice: Double = 0.0,
    val notes: String = "",
    val status: Int = OrderStatus.PENDING,
    val orderComment: String = "",
    val orderNum: Int = 0,
    val paymentMethod: String = "",
    val products: List<OrderProduct> = emptyList(),
    val subTotal: Double = 0.0,
    val tasleemDiscount: Double = 0.0,
    val taxes: List<OrderTax> = emptyList(),
    val totalPrice: Double = 0.0,
    val vendorDiscount: Double = 0.0
):Parcelable