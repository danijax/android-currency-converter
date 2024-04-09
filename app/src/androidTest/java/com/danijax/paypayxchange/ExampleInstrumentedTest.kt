package com.danijax.paypayxchange

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.danijax.paypayxchange.domain.CurrencyConverter
import com.danijax.paypayxchange.domain.CurrencyManager
import com.danijax.paypayxchange.domain.ExchangeRateInferencer
import com.danijax.paypayxchange.model.Currency
import com.danijax.paypayxchange.model.CurrencyRate
import com.danijax.paypayxchange.model.CurrencyRateResponse
import com.danijax.paypayxchange.utils.AssetManager
import com.danijax.paypayxchange.utils.RateParser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var assetManager : AssetManager

    @Before
    fun setup(){
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assetManager = AssetManager(appContext)
    }
    @Test
    fun  testRateParserReturnsListOfCurrencies() {
        val json = assetManager.getJsonFromAssets("samplecurrency.json")
        val items = Json.decodeFromString<CurrencyRateResponse>(json?: "")
        val rates = RateParser.invoke(items.rates)
        assertTrue(rates.isNotEmpty())
    }

    @Test
    fun  testListOfCurrenciesContainsBaseCurrency() {
        val base = "USD"
        val json = assetManager.getJsonFromAssets("samplecurrency.json")
        val items = Json.decodeFromString<CurrencyRateResponse>(json?: "")
        val rates = RateParser.invoke(items.rates)
        val baseCurrency = rates.first{
            it.name.contains(base, true)
        }
        assertNotNull(baseCurrency)
        assertTrue(baseCurrency.rate == 1.0F)
    }

    @Test
    fun  testListOfCurrenciesContainsSpecificRate() {
        val currency = "NGN"
        val json = assetManager.getJsonFromAssets("samplecurrency.json")
        val items = Json.decodeFromString<CurrencyRateResponse>(json?: "")
        val rates = RateParser.invoke(items.rates)
        val baseCurrency = rates.first{
            it.name.contains(currency, true)
        }
        assertNotNull(baseCurrency)
        assertTrue(baseCurrency.rate == 764.64F)
    }

    @Test
    fun  testRateConversion() =runTest {
        val baseSymbol = "USD"
        val json = assetManager.getJsonFromAssets("samplecurrency.json")
        val items = Json.decodeFromString<CurrencyRateResponse>(json?: "")
        val rates = RateParser.invoke(items.rates)
        val baseCurrency = rates.first{
            it.name.contains(baseSymbol, true)
        }

        val manager = CurrencyManager(Currency(baseSymbol, ""), rates)
        val result = CurrencyConverter.invoke(100.0F, manager, Currency("NGN", "Nigeria")).first()
        println(result)

        assertTrue(result.value == 76464F)
    }

    @Test
    fun  testRateConversion2() =runTest {
        val baseSymbol = "USD"
        val json = assetManager.getJsonFromAssets("samplecurrency.json")
        val items = Json.decodeFromString<CurrencyRateResponse>(json?: "")
        val rates = RateParser.invoke(items.rates)
        val baseCurrency = rates.first{
            it.name.contains(baseSymbol, true)
        }

        val manager = CurrencyManager(Currency(baseSymbol, ""), rates)
        val result = ExchangeRateInferencer.invoke (Currency(symbol = "NGN", "Nigeria"), manager,).first()
        println(result.rates.first {
            it.name == "USD"
        })

       // assertTrue(result.value == 76464F)
    }

}