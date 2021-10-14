package com.eyeorders.data.orderstats

import com.google.gson.annotations.SerializedName

data class OrderStats(
    @SerializedName("is_stop_order")
    val isStopOrder: Boolean = false,
    @SerializedName("completed_order")
    val completedOrder: String = "",
    @SerializedName("cancelled_order")
    val cancelledOrder: String = "",
    @SerializedName("new_order")
    val newOrder: String = "",
    @SerializedName("accepted_order")
    val acceptedOrder: String = ""
){
    companion object {
        const val KEY = "ORDER_COUNT"
    }
}