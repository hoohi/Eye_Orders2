package com.eyeorders.data.mockdata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id:String,
    val name: String,
    val imageUrl: String,
    val phoneNumber:String,
    val address:String
) : Parcelable