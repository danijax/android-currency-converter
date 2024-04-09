package com.danijax.paypayxchange.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danijax.paypayxchange.api.Result
import com.danijax.paypayxchange.data.repository.Repository
import com.danijax.paypayxchange.domain.CurrencyConverter
import com.danijax.paypayxchange.domain.CurrencyManager
import com.danijax.paypayxchange.model.Currency
import com.danijax.paypayxchange.model.ExchangeResult
import com.danijax.paypayxchange.utils.format
import com.danijax.paypayxchange.utils.fromTimeStamp
import com.danijax.paypayxchange.utils.getTimeElapsedInMinutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ExchangeRateViewModel @Inject constructor(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val populateJobStatus: StateFlow<BackgroundSetUpState> =
        getStatus().distinctUntilChanged().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BackgroundSetUpState.Loading
        )
    val baseCurrency = savedStateHandle.getStateFlow(BASE_CURRENCY, "USD")
    val selectedCurrency = savedStateHandle.getStateFlow(SELECTED_CURRENCY, "USD")
    val selectedRate = savedStateHandle.getStateFlow(SELECTED_CURRENCY, 0.00F)
    val amount = savedStateHandle.getStateFlow(AMOUNT, "0")
    val currencies: StateFlow<CurrenciesState<List<Currency>>> = getAllCurrencies() .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CurrenciesState.Loading
    )

    val currencyList = savedStateHandle.getStateFlow(CURRENCIES, emptyList<String>())

    val lastUpdate = savedStateHandle.getStateFlow( LAST_UPDATE,"Last Updated: unknown")

    private val _conversionResult = MutableStateFlow(emptyList<ExchangeResult>())
    val conversionResult : StateFlow<List<ExchangeResult>> = _conversionResult
    @VisibleForTesting
    fun getAllCurrencies() : Flow<CurrenciesState<List<Currency>>> {
            return repository.getAllCurrencies().map { it ->
                if (it.isEmpty()) {
                    savedStateHandle[CURRENCIES] = emptyList<String>()
                    CurrenciesState.Error("")
                } else {
                    var res = it.map {
                        it.symbol
                    }
                    savedStateHandle[CURRENCIES] = res
                    CurrenciesState.Success(it)
                }

            }.catch {
                //Log error
            }

    }

    private fun getExchangeRates() {
        viewModelScope.launch {
            val base = selectedCurrency.value
            repository.getRatesFor(base).map { result ->
                when (result) {
                    is Result.Error -> Unit
                    is Result.Success -> {

                        val data = result.data.rates
                        val conversions = currencyList.value.map {
                            val conv = CurrencyConverter.convert(
                                amount.value.ifEmpty { "0" }.toFloat(),
                                CurrencyManager(Currency(base, ""), rates = data),
                                Currency(it, "")
                            )
                            conv
                        }
                        _conversionResult.value = conversions
                    }
                }

            }
                .catch {
                    it.message
                    //Log.d("Rates", it.message?:"")
                }
                .collect()
        }
    }

    private fun getStatus() = channelFlow {
        repository.getUpdateStatus().map { status ->
            send(if (status) BackgroundSetUpState.Complete else BackgroundSetUpState.Loading)
        }
            .catch { }
            .collect()
    }

    fun updateSelectedCurrency(symbol: String) {
        savedStateHandle[SELECTED_CURRENCY] = symbol
        getExchangeRates()
    }

    fun updateAmount(amount: String) {
        savedStateHandle[AMOUNT] = amount
        getExchangeRates()
    }

    fun getLastUpdate() {
        viewModelScope.launch {
            val time = repository.getRateLastUpdatetime()
            if (time <=0L){
                savedStateHandle[LAST_UPDATE] = "Last Updated: Unknown"
            }else {
                Log.d("UpdateTime", time.toString())
                val dateTime = time.fromTimeStamp()
                Log.d("UpdateTime", dateTime.format())
                val lapse = dateTime.getTimeElapsedInMinutes(LocalDateTime.now())
                if (lapse < 1) {
                    savedStateHandle[LAST_UPDATE] = "Last Updated: $lapse Seconds ago"
                } else savedStateHandle[LAST_UPDATE] = "Last Updated: $lapse Minutes ago"
            }
        }
    }
}

sealed interface CurrenciesState<out T> {
    data class Success<out T>(val data: T) : CurrenciesState<T>
    data class Error(val message: String) : CurrenciesState<Nothing>
    data object Loading : CurrenciesState<Nothing>
}

sealed interface BackgroundSetUpState {
    data object Loading : BackgroundSetUpState
    data object Complete : BackgroundSetUpState
}

const val BASE_CURRENCY = "base"
const val SELECTED_CURRENCY = "selected_currency"
const val SELECTED_RATE = "selected_rate"
const val CURRENCIES = "cached_list"
const val AMOUNT = "amount"
const val LAST_UPDATE = "update_time"
