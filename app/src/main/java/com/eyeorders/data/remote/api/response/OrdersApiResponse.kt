package com.eyeorders.data.remote.api.response

import com.eyeorders.data.remote.api.deserialiser.AlwaysListTypeAdapterFactory
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName


data class OrdersApiResponse<T>(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("is_stop_order") val isStopOrder: Boolean = false,
    @SerializedName("completed_order") val completedOrders: String = "",
    @SerializedName("cancelled_order") val cancelledOrders: String = "",
    @SerializedName("new_order") val newOrder: String = "",
    @SerializedName("accepted_order") val acceptedOrder: String = "",
    @SerializedName("data")
    @JsonAdapter(AlwaysListTypeAdapterFactory::class)
    val data: List<T>,
)