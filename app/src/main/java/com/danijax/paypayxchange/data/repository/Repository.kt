package com.danijax.paypayxchange.data.repository

import com.danijax.paypayxchange.api.Result
import com.danijax.paypayxchange.model.Currency
import com.danijax.paypayxchange.model.CurrencyRate
import com.danijax.paypayxchange.model.ExchangeRateData
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface Repository {
    fun getAllCurrencies() : Flow<List<Currency>>
    fun getLastCurrencyUpdateTime() : Flow<LocalDateTime?>
    fun fetchAllCurrencies() : Flow<Result<Boolean>>
    fun fetchLatestRates(base: String) : Flow<Result<Boolean>>
    suspend fun saveCurrencies(data: String)
    suspend fun getBaseCurrency(): Flow<Currency>
    suspend fun getExchangeRates(): Flow<List<CurrencyRate>>
    suspend fun getRateLastUpdatetime(): Long
    fun getUpdateStatus() : Flow<Boolean>
    suspend fun getRatesFor(base: String): Flow<Result<ExchangeRateData>>
}
