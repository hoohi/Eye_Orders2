package com.eyeorders.data.language

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.eyeorders.util.language.LanguageHelper.Companion.LANGUAGE_ENGLISH
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageChangeObserver @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) {

    fun observeLangChange(): Flow<String> = callbackFlow {
        val lang = getLanguage()
        Timber.d("LANGUAGE: $lang")
        offer(lang)
        val listener = OnSharedPreferenceChangeListener { _, key ->
            if (key == LANGUAGE) {
                val langChange = getLanguage()
                Timber.d("langChange: $langChange")
                offer(langChange)
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    private fun getLanguage() =
        sharedPreferences.getString(LANGUAGE, LANGUAGE_ENGLISH) ?: LANGUAGE_ENGLISH

    companion object {
        const val LANGUAGE = "LANGUAGE"
    }
}