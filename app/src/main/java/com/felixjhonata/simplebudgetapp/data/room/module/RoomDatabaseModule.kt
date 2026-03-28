package com.felixjhonata.simplebudgetapp.data.room.module

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
    ).addCallback(
        object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                val values = ContentValues().apply {
                    put("id", 1)
                    put("totalBalance", "0")
                }
                db.insert("total_balance", SQLiteDatabase.CONFLICT_IGNORE, values)
            }
        }
    ).fallbackToDestructiveMigrationOnDowngrade(
        true
    ).build()

    @Provides
    @Singleton
    fun provideTransactionDao(
        database: SimpleBudgetAppDatabase
    ) = database.transactionDao()

    @Provides
    @Singleton
    fun provideTotalBalanceDao(
        database: SimpleBudgetAppDatabase
    ) = database.totalBalanceDao()
}