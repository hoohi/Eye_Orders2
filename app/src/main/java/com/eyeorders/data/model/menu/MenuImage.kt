package com.eyeorders.data.model.menu

import com.google.gson.annotations.SerializedName

data class MenuImage(
    @SerializedName("image_path") val imagePath: String,
    @SerializedName("thumb_path") val thumbPath: String,
)