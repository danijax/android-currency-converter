package com.danijax.paypayxchange.utils

import com.danijax.paypayxchange.model.CurrencyRate

/**
 * Utility to parse a map to list of objects
 */
object RateParser {

    operator fun invoke(data: Map<String, Float>): List<CurrencyRate>{
        return data.entries.map {
            CurrencyRate(it.key, it.value)
        }
    }
}