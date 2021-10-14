package com.eyeorders.data.model.order.neworder

import com.google.gson.annotations.SerializedName

data class NewOrderProduct(
    @SerializedName("amount")
    val amount: Int = 0,
    @SerializedName("rate")
    val rate: String = "",
    @SerializedName("description_arab")
    val descriptionArab: String = "",
    @SerializedName("product_id")
    val productId: Int = 0,
    @SerializedName("description")
    val description: String = "",
    @SerializedName("is_image")
    val isImage: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("product_price")
    val productPrice: Double = 0.0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("title_arab")
    val titleArab: String = "",
    @SerializedName("total_product_price")
    val totalProductPrice: Double = 0.0
)