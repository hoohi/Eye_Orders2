package com.eyeorders.screens.base

import android.content.res.Resources
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tasleem.orders.R

abstract class BaseBottomSheetDialog : BottomSheetDialogFragment() {

    protected open val isExpanded = true

    override fun onStart() {
        super.onStart()
        if (isExpanded) expandBottomSheet()
    }

    private fun expandBottomSheet() {
        val dialog = dialog as BottomSheetDialog?
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

     protected fun setDialogMinimumHeight() {
        val root: ConstraintLayout? = view?.findViewById(R.id.root_constraint)
        root?.layoutParams?.apply {
            height = (Resources.getSystem().displayMetrics.heightPixels * 0.7).toInt()
        }
    }
}