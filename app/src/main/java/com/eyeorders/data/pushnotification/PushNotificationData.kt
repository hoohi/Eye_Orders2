package com.eyeorders.data.pushnotification

import com.google.gson.annotations.SerializedName

data class PushNotificationData(
    @SerializedName("msg")
    val msg: String = "",
    @SerializedName("is_payed_msg")
    val isPayedMsg: String = "",
    @SerializedName("orderID")
    val orderID: Int = 0,
    @SerializedName("numOfItems")
    val numOfItems: Int = 0,
    @SerializedName("vendorId")
    val vendorId: Int = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("type")
    val type: Int = 0,
    @SerializedName("is_payed")
    val isPayed: Boolean = false
)