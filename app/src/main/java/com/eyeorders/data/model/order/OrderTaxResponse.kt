package com.eyeorders.data.model.order

import com.google.gson.annotations.SerializedName

data class OrderTaxResponse(
    @SerializedName("tax_name")
    val taxName: String = "",

    @SerializedName("tax_percentage")
    val taxPercentage: Double = 0.0,

    @SerializedName("tax_price")
    val taxPrice: String = "",
)