package com.eyeorders.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(
    private val startSpace: Int = 0,
    private val endSpace: Int = 0,
    private val topSpace: Int = 0,
    private val bottomSpace: Int = 0,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = startSpace
        outRect.right = endSpace
        outRect.top = topSpace
        outRect.bottom = bottomSpace
    }
}