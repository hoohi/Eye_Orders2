package com.eyeorders.screens.activeorders.neworders

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewOrder(
    val id: Int,
    val orderStatus: Int,
    val orderNumber: String,
    val itemCount: String,
    val price: String,
    val customerName: String,
) : Parcelable