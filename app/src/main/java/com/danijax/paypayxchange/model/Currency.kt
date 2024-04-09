package com.danijax.paypayxchange.model

import kotlinx.serialization.Serializable

@Serializable
data class Currency(val symbol: String, val country: String)
