package com.eyeorders.util.extension

import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.ViewHolder.getString(@StringRes resId: Int, vararg args: Any): String {
    return itemView.context.getString(resId, *args)
}