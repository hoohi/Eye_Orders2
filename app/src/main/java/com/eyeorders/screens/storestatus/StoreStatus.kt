package com.eyeorders.screens.storestatus

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class StoreStatus : Parcelable {
    OPEN,
    CLOSED,
    BUSY,
}