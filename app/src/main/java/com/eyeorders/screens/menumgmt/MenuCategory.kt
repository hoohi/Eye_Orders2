package com.eyeorders.screens.menumgmt

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuCategory(
    val id:Int,
    val title:String,
):Parcelable
