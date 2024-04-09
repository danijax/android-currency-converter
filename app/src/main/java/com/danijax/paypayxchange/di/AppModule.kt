package com.danijax.paypayxchange.di

import android.content.Context
import com.danijax.paypayxchange.data.datasource.CurrencyDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton // Provide always the same instance
    @Provides
    fun providesDataStore(@ApplicationContext context: Context): CurrencyDataSource {
        return CurrencyDataSource(context)
    }

}
