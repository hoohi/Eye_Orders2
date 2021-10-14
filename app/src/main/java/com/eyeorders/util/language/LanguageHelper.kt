package com.eyeorders.util.language

import android.app.Application
import com.yariksoffice.lingver.Lingver
import com.yariksoffice.lingver.store.PreferenceLocaleStore
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageHelper @Inject constructor(
    private val application: Application,
) {
    private val store = PreferenceLocaleStore(application, Locale(LANGUAGE_ENGLISH))
    private val lingver = Lingver.init(application, store)

    fun changeLanguage(isoCode: String, country: String) {
        lingver.setLocale(application, isoCode, country)
    }

    fun getCurrentLanguage(): String {
        return lingver.getLanguage()
    }

    fun init() {

    }

    companion object {
        const val LANGUAGE_ENGLISH = "en"
        const val COUNTRY_ENGLISH = "US"
        const val LANGUAGE_ARABIC = "ar"
        const val COUNTRY_ARABIC = "AE"
    }
}