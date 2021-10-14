package com.eyeorders.util.text

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan

fun getSpannableBoldText(fullText: String, boldText: String): SpannableStringBuilder {
    if (!fullText.contains(boldText)) {
        throw IllegalStateException("The full text '$fullText' should contain the bold text '$boldText'")
    }

    val boldTextStartIndex = fullText.indexOf(boldText)
    val boldTextEndIndex = boldTextStartIndex + boldText.length
    val spannable = SpannableStringBuilder(fullText)
    spannable.setSpan(
        StyleSpan(Typeface.BOLD),
        boldTextStartIndex,
        boldTextEndIndex,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannable
}


fun getSpannableScaleText(fullText: String, scaleText: String, scaleFactor:Float=0.8f): SpannableStringBuilder? {
    if (!fullText.contains(scaleText)) {
        throw IllegalStateException("The full text '$fullText' should contain the scaled text '$scaleText'")
    }

    val boldTextStartIndex = fullText.indexOf(scaleText)
    val boldTextEndIndex = boldTextStartIndex + scaleText.length
    val spannable = SpannableStringBuilder(fullText)
    spannable.setSpan(
        RelativeSizeSpan(scaleFactor),
        boldTextStartIndex,
        boldTextEndIndex,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannable
}