package com.eyeorders.data.model.order

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("id")
    @Expose
    val id: Int = 0,
    @SerializedName("customer")
    @Expose
    val customer: String = "",

    @SerializedName("order_num")
    @Expose
    val orderNum: Int = 0,

    @SerializedName("driver")
    @Expose
    val driver: String = "",

    @SerializedName("status")
    @Expose
    val status: String = "",

    @SerializedName("order_status")
    @Expose
    val orderStatus: Int,

    @SerializedName("delivery_type")
    @Expose
    val deliveryType: String = "",

    @SerializedName("total_price")
    @Expose
    val totalPrice: Double = 0.0,

    @SerializedName("payment_method")
    @Expose
    val paymentMethod: String = "",

    @SerializedName("date_created")
    @Expose
    val dateCreated: Long = 0L,

    @SerializedName("date_updated")
    @Expose
    val dateUpdated: String = "",

    @SerializedName("products")
    @Expose
    val products: List<OrderProductResponse> = emptyList(),
) {

    @Suppress("unused")
    constructor() : this(0, "", 0, "", "", 0, "", 0.0, "", 0L, "", emptyList())
}