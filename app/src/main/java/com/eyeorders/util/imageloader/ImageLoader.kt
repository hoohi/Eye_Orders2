package com.eyeorders.util.imageloader

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.tasleem.orders.R
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class ImageLoader @Inject constructor(
    @ActivityContext private val context: Context
) {

    fun load(
        imageSource: String,
        target: ImageView,
        @DrawableRes errorResId: Int = R.drawable.ic_person
    ) {
        GlideApp.with(context)
            .load(imageSource)
            .placeholder(PlaceHolderDrawable(context))
            .error(errorResId)
            .into(target)
    }

    fun loadDrawableRes(
        @DrawableRes resourceId: Int,
        target: ImageView,
        @DrawableRes errorResId: Int = R.drawable.ic_person
    ) {
        GlideApp.with(context)
            .load(resourceId)
            .placeholder(PlaceHolderDrawable(context))
            .error(errorResId)
            .into(target)
    }
}
