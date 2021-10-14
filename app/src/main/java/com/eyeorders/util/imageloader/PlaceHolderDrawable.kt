package com.eyeorders.util.imageloader

import android.content.Context
import android.graphics.PorterDuffColorFilter
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.tasleem.orders.R


class PlaceHolderDrawable(
    context: Context,
    radius: Float = 30f,
    stroke: Float = 5f,
    @ColorRes colorRes: Int = R.color.blue
) :
    CircularProgressDrawable(context) {
    init {
        strokeWidth = stroke
        centerRadius = radius
        colorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(
                context,
                colorRes
            ), android.graphics.PorterDuff.Mode.SRC_IN
        )
        start()
    }
}
