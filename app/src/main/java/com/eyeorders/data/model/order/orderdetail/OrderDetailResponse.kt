package com.eyeorders.data.model.order.orderdetail

import com.eyeorders.data.model.OrderStatus
import com.eyeorders.data.model.order.OrderProductResponse
import com.eyeorders.data.model.order.OrderTaxResponse
import com.google.gson.annotations.SerializedName

data class OrderDetailResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("coupon_type")
    val couponType: Any?,
    @SerializedName("customer_avatar_path")
    val customerAvatarPath: String,
    @SerializedName("customer_name")
    val customerName: String,
    @SerializedName("customer_phone")
    val customerPhone: String,
    @SerializedName("customer_thumb_path")
    val customerThumbPath: String,
    @SerializedName("date_accepted")
    val dateAccepted: Long,
    @SerializedName("date_created")
    val dateCreated: Long,
    @SerializedName("delivery_price")
    val deliveryPrice: Double,

    @SerializedName("notes")
    val notes: String = "",
    @SerializedName("customer_zone_name")
    val customerZone: String = "",
    @SerializedName("order_status")
    val status: Int = OrderStatus.PENDING,
    @SerializedName("order_comment")
    val orderComment: String = "",
    @SerializedName("order_num")
    val orderNum: Int,
    @SerializedName("payment_method")
    val paymentMethod: String,
    @SerializedName("products")
    val products: List<OrderProductResponse>,
    @SerializedName("sub_total")
    val subTotal: Double = 0.0,
    @SerializedName("tasleem_discount")
    val tasleemDiscount: Double = 0.0,
    @SerializedName("taxes")
    val taxes: List<OrderTaxResponse> = emptyList(),
    @SerializedName("total_price")
    val totalPrice: Double = 0.0,
    @SerializedName("vendor_discount")
    val vendorDiscount: Double = 0.0
)