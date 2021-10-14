package com.eyeorders.data.remote.api.response

import com.eyeorders.data.model.menu.MenuProduct
import com.google.gson.annotations.SerializedName

data class MenuProductResponse(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("subcategory_id") val subcategoryId: Int?,
    @SerializedName("title_arab") val titleArab: String?,
    @SerializedName("in_stock") val inStock: Boolean,
) {

    fun toMenuProduct(): MenuProduct {
        return MenuProduct(
            id = id ?: 0,
            title = title ?: "No title",
            titleArab = titleArab ?: "No title",
            inStock = inStock,
            subcategoryId = subcategoryId ?: 0
        )
    }
}