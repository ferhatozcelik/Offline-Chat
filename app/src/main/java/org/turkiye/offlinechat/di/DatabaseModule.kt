package org.turkiye.offlinechat.di

import android.app.Application
import androidx.room.Room
import org.turkiye.offlinechat.data.local.AppDatabase
import org.turkiye.offlinechat.data.local.UsersDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by ferhat.ozcelik on 12-02-2023.
 */

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application, callback: AppDatabase.Callback): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "local_database")
            .fallbackToDestructiveMigration().addCallback(callback).build()
    }

    @Provides
    fun provideUsersDao(db: AppDatabase): UsersDao {
        return db.getUsersDao()
    }
}