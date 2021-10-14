package com.eyeorders.screens.productavailability

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ProductAvailability : Parcelable {
    UNAVAILABLE,
    AVAILABLE
}

fun Boolean.toAvailability(): ProductAvailability {
    return if (this) ProductAvailability.AVAILABLE else ProductAvailability.UNAVAILABLE
}