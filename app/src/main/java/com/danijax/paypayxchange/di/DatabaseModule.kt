package com.danijax.paypayxchange.di

import android.content.Context
import com.danijax.paypayxchange.data.db.AppDatabase
import com.danijax.paypayxchange.data.db.ExchangeRateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideExchangeRateDaoDao(appDatabase: AppDatabase): ExchangeRateDao {
        return appDatabase.getExchangeDao()
    }
}