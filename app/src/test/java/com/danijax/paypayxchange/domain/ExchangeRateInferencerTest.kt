package com.danijax.paypayxchange.domain

import com.danijax.paypayxchange.model.Currency
import com.danijax.paypayxchange.model.CurrencyRate
import com.danijax.paypayxchange.model.CurrencyRateResponse
import com.danijax.paypayxchange.utils.AssetManager
import com.danijax.paypayxchange.utils.RateParser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class ExchangeRateInferencerTest{

    private lateinit var baseRates: List<CurrencyRate>
    private lateinit var assetManager: AssetManager
    private lateinit var json: String

    @Before
    fun setup(){
        baseRates = listOf(CurrencyRate("NGN", 764.64F))
        assetManager = AssetManager()
        json = assetManager.loadString("samplecurrency.json") ?:""
    }

    @Test
    fun  testRateParserReturnsListOfCurrencies()  {
        val items = Json.decodeFromString<CurrencyRateResponse>(json?: "")
        val rates = RateParser.invoke(items.rates)
        Assert.assertTrue(rates.isNotEmpty())
    }

    @Test
    fun `given a base currency and rates we should generate a new list of rates for selected currency `() = runTest{
        val baseCurrencyRate = Currency("USD", "")
        val items = Json.decodeFromString<CurrencyRateResponse>(json?: "")
        val rates = RateParser.invoke(items.rates)
        val currencyManager = CurrencyManager(baseCurrencyRate, rates)
        val inferenceRates = ExchangeRateInferencer.invoke(Currency("NGN", "Nigeria"), currencyManager).first()
        assertTrue(inferenceRates.rates.isNotEmpty())
        assertTrue(inferenceRates.rates.size == rates.size)
        assertTrue(inferenceRates.rates.first{
            it.name == "NGN"
        }.rate == 1.00F)
    }

    @Test
    fun `given a generated rates test inference values are correct `() = runTest{
        val baseCurrencyRate = Currency("USD", "")
        val items = Json.decodeFromString<CurrencyRateResponse>(json?: "")
        val rates = RateParser.invoke(items.rates)
        val currencyManager = CurrencyManager(baseCurrencyRate, rates)
        val inferenceRates = ExchangeRateInferencer.invoke(Currency("JPY", "Japan"), currencyManager).first()
        assertTrue(inferenceRates.rates.isNotEmpty())
        assertTrue(inferenceRates.rates.size == rates.size)
        assertTrue(inferenceRates.rates.first{
            it.name == "JPY"
        }.rate == 1.00F)
    }
}