package com.danijax.paypayxchange.domain

import com.danijax.paypayxchange.model.Currency
import com.danijax.paypayxchange.model.CurrencyRate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

object ExchangeRateInferencer {
    /**
     * using mathematical relationships to inference/estimate new rates
     * @param currency the currency that the new rates should be inference for
     * @param currencyManager an object that holds base currency with known exchange rates
     */
    operator fun invoke(currency: Currency, currencyManager: CurrencyManager) : Flow<CurrencyManager> {
        val baseUsdRate = currencyManager.rates.first {
            it.name == currency.symbol
        }
        val result = currencyManager.rates.map {
            val xd =1F.div(baseUsdRate.rate) * it.rate
            CurrencyRate(it.name,xd)
        }
        return flowOf( CurrencyManager(currency, result))
    }
}