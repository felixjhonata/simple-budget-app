package com.felixjhonata.simplebudgetapp.data.room.module

import android.content.Context
import androidx.room.Room
import com.felixjhonata.simplebudgetapp.data.room.database.SimpleBudgetAppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomDatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        SimpleBudgetAppDatabase::class.java,
        "simple_budget_app_database"
    ).fallbackToDestructiveMigrationOnDowngrade(
        true
    ).build()

    @Provides
    @Singleton
    fun provideTransactionDao(
        database: SimpleBudgetAppDatabase
    ) = database.transactionDao()
}