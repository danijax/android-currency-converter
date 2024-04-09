package com.danijax.paypayxchange.model

data class ExchangeRateData(val timeStamp: Long, val rates: List<CurrencyRate>) {
}