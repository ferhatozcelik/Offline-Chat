package org.turkiye.offlinechat.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope
import org.turkiye.offlinechat.data.entity.User
import org.turkiye.offlinechat.di.ApplicationScope
import javax.inject.Inject
import javax.inject.Provider

/**
 * Created by ferhat.ozcelik on 12-02-2023.
 */

@Database(entities = [User::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getUsersDao(): UsersDao

    class Callback @Inject constructor(
        private val database: Provider<AppDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback()
}