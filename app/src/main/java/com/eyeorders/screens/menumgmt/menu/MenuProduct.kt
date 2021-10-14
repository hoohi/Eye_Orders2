package com.eyeorders.screens.menumgmt.menu

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuProduct(
    val id: Int,
    val title: String,
    val isAvailable: Boolean
):Parcelable