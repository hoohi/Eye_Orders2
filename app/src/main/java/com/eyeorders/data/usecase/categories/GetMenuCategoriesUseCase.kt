package com.eyeorders.data.usecase.categories

import android.annotation.SuppressLint
import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.data.error.ErrorHandler
import com.eyeorders.data.language.LanguageChangeObserver
import com.eyeorders.data.model.DataState
import com.eyeorders.data.model.menu.VendorCategory
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.data.remote.api.ApiService
import com.eyeorders.data.usecase.base.processResponse
import com.eyeorders.screens.menumgmt.MenuCategory
import com.eyeorders.util.extension.isEnglish
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class GetMenuCategoriesUseCase @Inject constructor(
    private val service: ApiService,
    private val prefsDataManager: PrefsDataManager,
    private val errorHandler: ErrorHandler,
    private val dispatchers: AppDispatchers,
    private val languageChangeObserver: LanguageChangeObserver
) {

    @SuppressLint("DefaultLocale")
    fun execute(): Flow<DataState<List<MenuCategory>>> {
        return languageChangeObserver.observeLangChange()
            .flatMapMerge { language ->
                flow {
                    try {
                        Timber.d("NEw Lang:$language ")
                        emit(DataState.Loading)
                        val cachedMenuCategories = prefsDataManager.getVendorCategories()
                        val mapper = { vendor: VendorCategory ->
                            MenuCategory(
                                vendor.id,
                                if (language.isEnglish()) {
                                    vendor.title.capitalize()
                                } else {
                                    vendor.titleArab
                                }
                            )
                        }
                        if (cachedMenuCategories.isNotEmpty()) {
                            emit(DataState.Success(cachedMenuCategories.map(mapper)))
                        }
                        val userId = prefsDataManager.getUserId()
                        val data = processResponse {
                            service.getCategories(userId)
                        }
                        val categories = data.sortedBy { it.ordering }
                        Timber.d("Gotten categories: $categories")
                        prefsDataManager.saveVendorCategories(categories)
                        val menuCategories = categories.map(mapper)
                        Timber.d("menuCategories: $menuCategories")
                        emit(DataState.Success(menuCategories))
                    } catch (e: Exception) {
                        emit(DataState.Error(errorHandler.getErrorMessage(e)))
                    }
                }.flowOn(dispatchers.io)
            }
    }
}