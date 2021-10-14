package com.eyeorders.data.usecase.categories

import android.annotation.SuppressLint
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.eyeorders.data.datasource.MenuProductsDataSource
import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.data.error.ErrorHandler
import com.eyeorders.data.language.LanguageChangeObserver
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.data.remote.api.ApiService
import com.eyeorders.screens.menumgmt.menu.MenuProduct
import com.eyeorders.util.extension.isEnglish
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class GetMenuProductsUseCase @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val prefsDataManager: PrefsDataManager,
    private val apiService: ApiService,
    private val dispatchers: AppDispatchers,
    private val languageChangeObserver: LanguageChangeObserver
) {

    @SuppressLint("DefaultLocale")
    fun execute(categoryId: Int): Flow<PagingData<MenuProduct>> {
        Timber.d("Executing with parameters: categoryId=$categoryId")

        return languageChangeObserver.observeLangChange().flatMapMerge { language ->
            Pager(
                PagingConfig(PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
                pagingSourceFactory = {
                    Timber.d("Calling data source factory...")
                    MenuProductsDataSource(
                        errorHandler,
                        apiService,
                        prefsDataManager,
                        categoryId
                    )
                }
            ).flow.map { pagingData ->
                pagingData.map { product ->
                    MenuProduct(
                        id = product.id,
                        isAvailable = product.inStock,
                        title = if (language.isEnglish()) product.title.capitalize() else product.titleArab
                    )
                }
            }
        }.flowOn(dispatchers.io)
    }

    companion object {
        private const val PAGE_SIZE = 15
        private const val PRE_FETCH_DISTANCE = 10
    }
}