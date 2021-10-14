package com.eyeorders.data.cache.menuproduct

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.eyeorders.data.model.menu.MenuProduct
import com.eyeorders.data.cache.base.BaseDao

@Dao
abstract class MenuProductDao : BaseDao<MenuProduct> {

    @Query("SELECT * FROM menuproduct WHERE subcategoryId=:categoryId")
    abstract fun getProducts(categoryId:Int): PagingSource<Int, MenuProduct>

    @Query("SELECT * FROM menuproduct WHERE id=:id")
    abstract fun getProductById(id: Int): MenuProduct

    @Query("DELETE FROM menuproduct")
    abstract suspend fun clearProducts()
}