package com.danijax.paypayxchange.data.repository

import com.danijax.paypayxchange.api.Result
import com.danijax.paypayxchange.domain.CurrencyManager
import com.danijax.paypayxchange.domain.ExchangeRateInferencer
import com.danijax.paypayxchange.model.Currency
import com.danijax.paypayxchange.model.CurrencyRate
import com.danijax.paypayxchange.model.CurrencyRateResponse
import com.danijax.paypayxchange.model.ExchangeRateData
import com.danijax.paypayxchange.utils.AssetManager
import com.danijax.paypayxchange.utils.RateParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

class TestRepository(private val assetManager: AssetManager) : Repository {
    override fun getAllCurrencies(): Flow<List<Currency>> {
        val currenciesJson = assetManager.loadString("all_currencies.json") ?:""
        val currenciesMap = Json.decodeFromString<Map<String, String>>(currenciesJson)
        val currencies = currenciesMap.map {
            Currency(it.key, it.value)
        }
        return flowOf(currencies)
    }

    override fun getLastCurrencyUpdateTime(): Flow<LocalDateTime?> {
        TODO("Not yet implemented")
    }

    override fun fetchAllCurrencies(): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun fetchLatestRates(base: String): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveCurrencies(data: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getBaseCurrency(): Flow<Currency> {
        TODO("Not yet implemented")
    }

    override suspend fun getExchangeRates(): Flow<List<CurrencyRate>> {
        TODO("Not yet implemented")
    }

    override suspend fun getRateLastUpdatetime(): Long {
        TODO("Not yet implemented")
    }

    override fun getUpdateStatus(): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getRatesFor(base: String): Flow<Result<ExchangeRateData>> {
        val db = mutableMapOf<String, String>()
        val currenciesJson = assetManager.loadString("samplecurrency.json") ?:""
        val items = Json.decodeFromString<CurrencyRateResponse>(currenciesJson)
        val rates = RateParser.invoke(items.rates)
        db["USD"] = currenciesJson

        return if (base == "USD") flowOf(Result.Success(ExchangeRateData(1L, rates))) else{
            val currencyManager = CurrencyManager(Currency("USD", ""), rates)
            val inferenceRates = ExchangeRateInferencer.invoke(Currency(base, ""), currencyManager).first()
            flowOf(Result.Success(ExchangeRateData(1L, inferenceRates.rates)))

        }
    }
}