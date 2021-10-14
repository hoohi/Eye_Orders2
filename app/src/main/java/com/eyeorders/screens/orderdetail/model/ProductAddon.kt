package com.eyeorders.screens.orderdetail.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductAddon(
    val title: String = "",
    val description: String = "",
    val price: String = "",
) : Parcelable