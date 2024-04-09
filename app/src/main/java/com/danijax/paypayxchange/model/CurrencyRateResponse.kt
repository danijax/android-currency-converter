package com.danijax.paypayxchange.model

import kotlinx.serialization.Serializable

@Serializable
data class CurrencyRateResponse(
    val base: String,
    val disclaimer: String,
    val license: String,
    val rates: MutableMap<String, Float>,
    val timestamp: Long
)