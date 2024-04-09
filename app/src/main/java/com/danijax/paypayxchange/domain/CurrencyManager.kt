package com.danijax.paypayxchange.domain

import com.danijax.paypayxchange.model.Currency
import com.danijax.paypayxchange.model.CurrencyRate

data class CurrencyManager(val baseCurrencyRate: Currency, val rates: List<CurrencyRate>)