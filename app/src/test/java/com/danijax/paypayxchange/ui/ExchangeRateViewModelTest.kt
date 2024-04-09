package com.danijax.paypayxchange.ui

import androidx.lifecycle.SavedStateHandle
import com.danijax.paypayxchange.data.repository.ExchangeRateRepository
import com.danijax.paypayxchange.data.repository.TestRepository
import com.danijax.paypayxchange.util.MainDispatcherRule
import com.danijax.paypayxchange.utils.AssetManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations


class ExchangeRateViewModelTest{
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ExchangeRateViewModel
    private lateinit var savedStateHandle : SavedStateHandle
    private lateinit var assetManager: AssetManager
    private lateinit var json: String
    private lateinit var currenciesJson: String

    @Before
    fun setup(){
        MockitoAnnotations.openMocks(this)
        savedStateHandle = SavedStateHandle().apply {
            set(SELECTED_CURRENCY, "USD")
            set(AMOUNT, "0")
        }

        assetManager = AssetManager()
        json = assetManager.loadString("samplecurrency.json") ?:""
        currenciesJson = assetManager.loadString("all_currencies.json") ?:""
        viewModel = ExchangeRateViewModel(TestRepository(assetManager), savedStateHandle)

    }
    @Test
    fun `initial view model state should return empty list of currencies`()= runTest{
        assertTrue(viewModel.currencyList.value.isEmpty())

    }

    @Test
    fun `initial view model selected currency state should return usd`()= runTest{
        assertEquals(viewModel.selectedCurrency.value, "USD")

    }

    @Test
    fun `initial view model amount on screen should 0`()= runTest{
        assertEquals(viewModel.amount.value, "0")

    }

    @Test
    fun `selecting base currency updates base currency in view model `()= runTest{
        viewModel.updateSelectedCurrency("NGN")
        assertEquals(viewModel.selectedCurrency.value ,"NGN")

    }

    @Test
    fun `get all currencies returns list of currencies `()= runTest{
        val collectJob2 = launch(UnconfinedTestDispatcher()) { viewModel.currencies.collect() }
        assertTrue(viewModel.currencyList.value.isNotEmpty())
        assertTrue(viewModel.currencyList.value.contains("JPY"))
        assertTrue(viewModel.currencyList.value.contains("EUR"))
        assertTrue(viewModel.currencyList.value.contains("NGN"))

        collectJob2.cancel()
    }

    @Test
    fun `given the app loads return rates for USD`()= runTest{
        val collectJob1 = launch(UnconfinedTestDispatcher()) { viewModel.currencies.collect() }
        val collectJob2 =
            launch(UnconfinedTestDispatcher()) { viewModel.conversionResult.collect() }
        viewModel.updateSelectedCurrency("USD")
        viewModel.updateAmount("1")

        val usd2NairaRate = viewModel.conversionResult.value.find{
            it.currencyRate.symbol == "NGN"
        }

        val usd2JpyRate = viewModel.conversionResult.value.find{
            it.currencyRate.symbol == "JPY"
        }
        assertEquals(usd2NairaRate?.value ,764.64F)
        assertEquals(usd2JpyRate?.value ,149.704F)
        collectJob1.cancel()
        collectJob2.cancel()
    }

    @Test
    fun `given user selects base currency for conversion return inferred rates`()= runTest{
        val collectJob1 = launch(UnconfinedTestDispatcher()) { viewModel.currencies.collect() }
        val collectJob2 =
            launch(UnconfinedTestDispatcher()) { viewModel.conversionResult.collect() }

        viewModel.updateSelectedCurrency("NGN")
        viewModel.updateAmount("1")

        val naira2UsdRate = viewModel.conversionResult.value.find{
            it.currencyRate.symbol == "USD"
        }

        val naira2JpyRate = viewModel.conversionResult.value.find{
            it.currencyRate.symbol == "JPY"
        }
        assertEquals(naira2UsdRate?.value ,0.001307805F)
        assertEquals(naira2JpyRate?.value ,0.19578363F)
        collectJob1.cancel()
        collectJob2.cancel()
    }

    @Test
    fun `given user selects base currency and updates amount for conversion return comverted values`()= runTest{
        val collectJob1 = launch(UnconfinedTestDispatcher()) { viewModel.currencies.collect() }
        val collectJob2 =
            launch(UnconfinedTestDispatcher()) { viewModel.conversionResult.collect() }

        viewModel.updateSelectedCurrency("NGN")
        viewModel.updateAmount("100")

        val naira2UsdRate = viewModel.conversionResult.value.find{
            it.currencyRate.symbol == "USD"
        }

        val naira2JpyRate = viewModel.conversionResult.value.find{
            it.currencyRate.symbol == "JPY"
        }
        //DecimalFormat
        assertEquals(naira2UsdRate?.value ,0.13078049F)
        assertEquals(naira2JpyRate?.value ,019.578363F)
        collectJob1.cancel()
        collectJob2.cancel()
    }


}
