package com.danijax.paypayxchange.data.datasource

import com.danijax.paypayxchange.api.ExchangeRateService
import com.danijax.paypayxchange.api.Result
import com.danijax.paypayxchange.api.apiRequestFlow
import com.danijax.paypayxchange.model.CurrencyRateResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val apiService: ExchangeRateService) {

    fun getCurrencies(): Flow<Result<Map<String, String>>> {
        return apiRequestFlow { apiService.getCurrencies() }
    }

    fun getCurrentRates(base: String): Flow<Result<CurrencyRateResponse>> {
        return apiRequestFlow { apiService.getLatestRates(base = base) }
    }
}