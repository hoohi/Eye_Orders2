package com.eyeorders.data.model.order

import com.google.gson.annotations.SerializedName

data class ProductAddonResponse(
    @SerializedName("title")
    val title: String = "",

    @SerializedName("title_arab")
    val title_arab: String = "",

    @SerializedName("description")
    val description: String = "",

    @SerializedName("description_arab")
    val description_arab: String = "",

    @SerializedName("addon_price")
    val addon_price: Double = 0.0,
)