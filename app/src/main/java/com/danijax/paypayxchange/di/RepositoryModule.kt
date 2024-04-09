package com.danijax.paypayxchange.di

import com.danijax.paypayxchange.data.repository.ExchangeRateRepository
import com.danijax.paypayxchange.data.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindRepository(
        repository: ExchangeRateRepository
    ): Repository
}
