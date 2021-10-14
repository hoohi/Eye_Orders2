package com.eyeorders.data.model.order

import com.google.gson.annotations.SerializedName

data class ProductOptionResponse(
    @SerializedName("param_title")
    val paramTitle: String? = "",

    @SerializedName("param_title_arab")
    val paramTitleArab: String? = "",

    @SerializedName("option_title")
    val optionTitle: String? = "",

    @SerializedName("option_title_arab")
    val optionTitleArab: String? = "",

    @SerializedName("option_price")
    val optionPrice: Double = 0.0,
)