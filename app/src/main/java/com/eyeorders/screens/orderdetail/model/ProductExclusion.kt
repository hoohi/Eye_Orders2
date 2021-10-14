package com.eyeorders.screens.orderdetail.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductExclusion(
    val title: String = "",
    val description: String = "",
    val parameterPrice: String = "",
) : Parcelable