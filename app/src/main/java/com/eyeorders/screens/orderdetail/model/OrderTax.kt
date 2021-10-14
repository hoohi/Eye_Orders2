package com.eyeorders.screens.orderdetail.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderTax(
    val taxName: String = "",
    val taxPercentage: String = "",
    val taxPrice: String = "",
):Parcelable