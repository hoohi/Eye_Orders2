package com.eyeorders.data.model.order

import com.google.gson.annotations.SerializedName

data class OrderProductResponse(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("title")
    val title: String = "",

    @SerializedName("title_arab")
    val titleArab: String = "",

    @SerializedName("amount")
    val amount: Double = 0.0,

    @SerializedName("product_price")
    val productPrice: Double = 0.0,

    @SerializedName("product_single_original_price")
    val productSingleOriginalPrice: Double = 0.0,

    @SerializedName("product_discount_percentage")
    val productDiscountPercentage: Double = 0.0,

    @SerializedName("single_discount")
    val singleDiscount: Double = 0.0,

    @SerializedName("options")
    val options: List<ProductOptionResponse> = emptyList(),

    @SerializedName("addons")
    val addons: List<ProductAddonResponse> = emptyList(),

    @SerializedName("excluded")
    val excluded: List<ProductExclusionResponse> = emptyList(),
)