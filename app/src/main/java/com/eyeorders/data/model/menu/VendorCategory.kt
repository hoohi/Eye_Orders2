package com.eyeorders.data.model.menu

import com.google.gson.annotations.SerializedName

data class VendorCategory(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("title_ar") val titleArab: String,
    @SerializedName("ordering") val ordering: Int? = 0,
)