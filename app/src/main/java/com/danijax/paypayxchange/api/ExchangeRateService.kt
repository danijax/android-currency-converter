package com.danijax.paypayxchange.api

import com.danijax.paypayxchange.BuildConfig
import com.danijax.paypayxchange.model.CurrencyRateResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateService {

    @GET("api/latest.json")
    fun getLatestRates(@Query("app_id") appId: String = BuildConfig.EXCHANGE_APP_ID, @Query("base") base: String? = null) : Call<CurrencyRateResponse>

    @GET("api/currencies.json")
    fun getCurrencies() : Call<Map<String, String>>
}