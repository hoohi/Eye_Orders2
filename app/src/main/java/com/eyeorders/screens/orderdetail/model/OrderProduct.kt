package com.eyeorders.screens.orderdetail.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderProduct(
    val id: Int = 0,
    val title: String = "",
    val amount: Int = 0,
    val productPrice: String = "",
    val productSingleOriginalPrice: String = "",
    val productDiscountPercentage: String = "",
    val singleDiscount: String = "",
    val options: List<ProductOption> = emptyList(),
    val addons: List<ProductAddon> = emptyList(),
    val excluded: List<ProductExclusion> = emptyList(),
) : Parcelable