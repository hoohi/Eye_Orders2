package com.eyeorders.data.model.order

import com.google.gson.annotations.SerializedName

data class ProductExclusionResponse(
    @SerializedName("title")
    val title: String = "",

    @SerializedName("title_arab")
    val title_arab: String = "",

    @SerializedName("description")
    val description: String = "",

    @SerializedName("description_arab")
    val description_arab: String = "",

    @SerializedName("parameter_price")
    val parameter_price: Double = 0.0
)