package com.eyeorders.data.model.order.neworder

import com.google.gson.annotations.SerializedName

data class NewOrderItem(
    @SerializedName("notes")
    val notes: String = "",
    @SerializedName("total_price")
    val totalPrice: Double = 0.0,
    @SerializedName("date_created")
    val dateCreated: String = "",
    @SerializedName("order_comment")
    val orderComment: String = "",
    @SerializedName("customer_phone")
    val customerPhone: String = "",
    @SerializedName("products")
    val products: List<NewOrderProduct> = emptyList(),
    @SerializedName("order_status")
    val orderStatus: Int = 0,
    @SerializedName("date_accepted")
    val dateAccepted: String = "",
    @SerializedName("sub_total")
    val subTotal: Double = 0.0,
    @SerializedName("vendor_discount")
    val vendorDiscount: Int = 0,
    @SerializedName("customer_name")
    val customerName: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("order_num")
    val orderNum: Int = 0,
    @SerializedName("payment_method")
    val paymentMethod: String = "",
    @SerializedName("tasleem_discount")
    val tasleemDiscount: Int = 0,
    @SerializedName("delivery_price")
    val deliveryPrice: Double = 0.0
)