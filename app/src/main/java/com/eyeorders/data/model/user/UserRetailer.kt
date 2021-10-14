package com.eyeorders.data.model.user

import com.google.gson.annotations.SerializedName

data class UserRetailer(
    @SerializedName("id")
    val id: Int,

    @SerializedName("company_name")
    val companyName: String,

    @SerializedName("company_name_arab")
    val companyNameArab: String,

    @SerializedName("image_path")
    val imagePath: String? = null,

    @SerializedName("is_stop_order")
    val isStopOrder: Boolean = false,
)