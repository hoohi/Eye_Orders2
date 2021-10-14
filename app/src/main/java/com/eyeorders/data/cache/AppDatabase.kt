package com.eyeorders.data.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.eyeorders.data.cache.menuproduct.MenuProductDao
import com.eyeorders.data.cache.menuproduct.MenuProductKey
import com.eyeorders.data.cache.menuproduct.MenuProductKeyDao
import com.eyeorders.data.model.menu.MenuProduct

/**
 * Database schema that holds the list of repos.
 */
@Database(
    entities = [MenuProduct::class, MenuProductKey::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun menuProductDao(): MenuProductDao
    abstract fun menuProductKeyDao(): MenuProductKeyDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "farmz.db"
            )
                .build()
    }

}
