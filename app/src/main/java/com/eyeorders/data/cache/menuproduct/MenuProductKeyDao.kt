package com.eyeorders.data.cache.menuproduct

import androidx.room.Dao
import androidx.room.Query
import com.eyeorders.data.cache.base.BaseDao

@Dao
abstract class MenuProductKeyDao : BaseDao<MenuProductKey> {

    @Query("SELECT * FROM menuproductkey WHERE id = :id")
    abstract suspend fun getRemoteKeysById(id: Int): MenuProductKey?

    @Query("DELETE FROM menuproductkey")
    abstract suspend fun clearRemoteKeys()
}

