package com.eyeorders.data.usecase.vendor

import android.annotation.SuppressLint
import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.data.language.LanguageChangeObserver
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.screens.main.VendorDetails
import com.eyeorders.util.extension.isEnglish
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetVendorDetailsUseCase @Inject constructor(
    private val prefsDataManager: PrefsDataManager,
    private val dispatchers: AppDispatchers,
    private val languageChangeObserver: LanguageChangeObserver
) {
    @SuppressLint("DefaultLocale")
    fun execute(): Flow<VendorDetails> {
        return languageChangeObserver.observeLangChange()
            .flatMapMerge { language ->
                flow {
                    try {
                        val user = prefsDataManager.getUser()
                        emit(
                            VendorDetails(
                                name = if (language.isEnglish()) {
                                    user.companyName.capitalize()
                                } else {
                                    user.companyNameArab
                                },
                                imageUrl = user.imagePath ?: ""
                            )
                        )
                    } catch (e: Exception) {
                        emit(VendorDetails())
                    }
                }
            }.flowOn(dispatchers.io)
    }
}