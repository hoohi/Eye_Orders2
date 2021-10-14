package com.eyeorders.data.mockdata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
        val id: String,
        val name:String
) : Parcelable