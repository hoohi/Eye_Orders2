package com.eyeorders.screens.orderdetail.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductOption(
    val paramTitle: String? = "",
    val optionTitle: String? = "",
    val optionPrice: String = "",
) : Parcelable