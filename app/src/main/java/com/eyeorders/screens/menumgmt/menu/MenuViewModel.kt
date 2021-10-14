package com.eyeorders.screens.menumgmt.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.eyeorders.data.cache.AppDatabase
import com.eyeorders.data.datasource.MenuProductsMediator
import com.eyeorders.data.language.LanguageChangeObserver
import com.eyeorders.data.usecase.categories.ChangeProductStatusUseCase
import com.eyeorders.screens.productavailability.ProductAvailability
import com.eyeorders.util.extension.isEnglish
import com.eyeorders.util.livedata.event.Event
import com.eyeorders.util.livedata.extension.switchMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val changeProductStatusUseCase: ChangeProductStatusUseCase,
    private val appDatabase: AppDatabase,
    private val menuProductsMediator: MenuProductsMediator,
    private val languageChangeObserver: LanguageChangeObserver,
) : ViewModel() {

    private val storeAvailability = MutableLiveData<AvailabilityParams>()
    val changeAvailability = storeAvailability.switchMap {
        changeProductStatusUseCase.execute(
            it.product.id,
            it.newAvailability
        ).map { dataState ->
            Event(dataState)
        }.asLiveData()
    }

    fun fetchMenu(categoryId: Int): LiveData<PagingData<MenuProduct>> {
        Timber.tag("$categoryId")
        Timber.d("fetchMenu: $categoryId")
        Timber.tag("$categoryId")
        Timber.d("fetchMenu: $this")
        menuProductsMediator.categoryId = categoryId
        return languageChangeObserver.observeLangChange().flatMapLatest { language ->
            Pager(
                PagingConfig(PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
                remoteMediator = menuProductsMediator,
                pagingSourceFactory = {
                    Timber.d("pagingSourceFactory: $categoryId")
                    appDatabase.menuProductDao().getProducts(categoryId)
                }
            ).flow.map { pagingData ->
                Timber.d("flow.map: $categoryId")
                pagingData.map { product ->
                    Timber.d("Product: $product")
                    MenuProduct(
                        id = product.id,
                        isAvailable = product.inStock,
                        title = if (language.isEnglish()) product.title.capitalize(Locale.ENGLISH) else product.titleArab
                    )
                }
            }
        }.asLiveData()
    }

    fun updateProductAvailability(product: MenuProduct, availability: ProductAvailability) {
        storeAvailability.postValue(AvailabilityParams(product, availability))
    }

    data class CategoryParams(
        val categoryId: Int
    )

    data class AvailabilityParams(
        val product: MenuProduct,
        val newAvailability: ProductAvailability
    )

    companion object {
        private const val PAGE_SIZE = 15
        private const val PRE_FETCH_DISTANCE = 3
    }
}