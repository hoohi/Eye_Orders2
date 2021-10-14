package com.eyeorders.util.extension

import com.eyeorders.util.language.LanguageHelper

fun String.isEnglish(): Boolean {
    return this == LanguageHelper.LANGUAGE_ENGLISH
}