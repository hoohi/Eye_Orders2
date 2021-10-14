package com.eyeorders.screens.activeorders.acceptedorders

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AcceptedOrder(
    val id: Int,
    val status: String,
    val orderStatus: Int,
    val orderCount: Int,
    val orderNumber: String,
    val customerName: String,
    val orderDate: String,
    val driverName: String,
) : Parcelable