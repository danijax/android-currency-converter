package com.danijax.paypayxchange.domain

import com.danijax.paypayxchange.model.Currency
import com.danijax.paypayxchange.model.ExchangeResult
import kotlinx.coroutines.flow.channelFlow

object CurrencyConverter {
    /**
     * Converts a given amount in a given currency to another currency
     * @param amount value to be converted
     * @param objects tha holds base currency and known/estimated rates
     * @param to currency that should be produced
     */
   operator fun invoke(amount: Float, currencyManager: CurrencyManager, to: Currency) = channelFlow {
        val rate = currencyManager.rates.first {
            it.name == to.symbol
        }
        send(ExchangeResult(to, amount* rate.rate ))
    }

    fun convert(amount: Float, currencyManager: CurrencyManager, to: Currency) : ExchangeResult {
        val rate = currencyManager.rates.find {
            it.name == to.symbol
        }
        return ExchangeResult(to, amount* (rate?.rate ?:0F) )
    }
}