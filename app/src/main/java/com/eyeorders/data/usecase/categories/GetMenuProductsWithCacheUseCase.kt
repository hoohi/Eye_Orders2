package com.eyeorders.data.usecase.categories

import android.annotation.SuppressLint
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.eyeorders.data.cache.AppDatabase
import com.eyeorders.data.datasource.MenuProductsMediator
import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.data.language.LanguageChangeObserver
import com.eyeorders.screens.menumgmt.menu.MenuProduct
import com.eyeorders.util.extension.isEnglish
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMenuProductsWithCacheUseCase @Inject constructor(
    private val appDatabase: AppDatabase,
    private val menuProductsMediator: MenuProductsMediator,
    private val dispatchers: AppDispatchers,
    private val languageChangeObserver: LanguageChangeObserver
) {

    @SuppressLint("DefaultLocale")
    fun execute(categoryId: Int): Flow<PagingData<MenuProduct>> {
        menuProductsMediator.categoryId = categoryId
        return languageChangeObserver.observeLangChange().flatMapMerge { language ->
            Pager(
                PagingConfig(PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
                remoteMediator = menuProductsMediator,
                pagingSourceFactory = { appDatabase.menuProductDao().getProducts(categoryId) }
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
        private const val PRE_FETCH_DISTANCE = 3
    }
}