package com.danijax.paypayxchange.data.repository

import com.danijax.paypayxchange.api.Result
import com.danijax.paypayxchange.data.datasource.CurrencyDataSource
import com.danijax.paypayxchange.data.datasource.RemoteDataSource
import com.danijax.paypayxchange.data.db.ExchangeRateDao
import com.danijax.paypayxchange.data.db.ExchangeRateEntity
import com.danijax.paypayxchange.domain.CurrencyManager
import com.danijax.paypayxchange.domain.ExchangeRateInferencer
import com.danijax.paypayxchange.model.Currency
import com.danijax.paypayxchange.model.CurrencyRate
import com.danijax.paypayxchange.model.ErrorResponse
import com.danijax.paypayxchange.model.ExchangeRateData
import com.danijax.paypayxchange.utils.RateParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ExchangeRateRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val dao: ExchangeRateDao,
    private val dataSource: CurrencyDataSource,
) : Repository {
    override fun getAllCurrencies(): Flow<List<Currency>> {
        return dataSource.getAllCurrencies().map { map ->
           map.entries.map { set ->
               Currency(set.key, set.value)
           }
        }
    }
    override fun getLastCurrencyUpdateTime() = dataSource.getLastUpdateTime()
    override fun fetchAllCurrencies(): Flow<Result<Boolean>> {
        return remoteDataSource.getCurrencies().map { result ->
            when(result){
                is Result.Error -> {
                    Result.Error(result.errorResponse)
                }
                is Result.Success -> {
                    dataSource.saveCurrencies(Json.encodeToString(result.data))
                    Result.Success(true)
                }
            }
        }.catch {
            Result.Error(ErrorResponse(description = "", error = true, message = "", status = 0))
        }.flowOn(Dispatchers.IO)
    }

    override fun fetchLatestRates(base: String): Flow<Result<Boolean>> {
        return remoteDataSource.getCurrentRates(base).map { result ->
        when(result){
            is Result.Error -> {
                Result.Error(result.errorResponse)
            }
            is Result.Success -> {
                //Data from API is cached once
                //Given we have exchange rates for USD as base currency. We are making inference of the other rates
                //For every currency except base USD we will generate rates and save them locally
                //worker service is responsible for performing this
                //db insert is an upsert action which is efficient
                dataSource.saveRates(currencies = Json.encodeToString(result.data.rates), lastUpdate = result.data.timestamp)
                dao.insert(ExchangeRateEntity(id = 0, base = result.data.base,  rates = Json.encodeToString(result.data.rates), lastUpdate = result.data.timestamp))
                val rates = RateParser.invoke(result.data.rates)
                result.data.rates.forEach {
                    if(it.key !== "USD"){
                        val entityMap = mutableMapOf<String, Float>()
                        val infRates = ExchangeRateInferencer.invoke(Currency(it.key, ""), CurrencyManager(Currency("USD",""), rates)).first()
                        infRates.rates.forEach {cr ->
                            entityMap[cr.name] = cr.rate
                        }
                        dao.insert(ExchangeRateEntity(0, it.key, result.data.timestamp, Json.encodeToString(entityMap)))
                    }
                }
                dataSource.updatePopulateJobStatus(completed = true)
                Result.Success(true)
            }
        }

        }.catch {
            Result.Error(ErrorResponse(description = "", error = true, message = "", status = 0))
        }.flowOn(Dispatchers.IO)
    }
    override suspend fun saveCurrencies(data: String)= dataSource.saveCurrencies(data)
    override suspend fun getBaseCurrency() = dataSource.getBaseCurrency()

    override suspend fun getExchangeRates() : Flow<List<CurrencyRate>>{
        return dataSource.getRates().map { map ->
            map.second.entries.map {
                CurrencyRate(it.key, it.value)
            }

        }

    }

    override suspend fun getRateLastUpdatetime() = dataSource.getRatesLastUpdateTime().first()
    override fun getUpdateStatus() = dataSource.getPopulateJobStatus()
    override suspend fun getRatesFor(base: String): Flow<Result<ExchangeRateData>> {
        return dao.get(base).mapLatest { entity ->
            val rates = Json.decodeFromString<Map<String, Float>>(entity.rates)

           val  result = rates.map {
               CurrencyRate(it.key, it.value)
           }
            Result.Success(ExchangeRateData(entity.lastUpdate, result))
        }
    }
}

