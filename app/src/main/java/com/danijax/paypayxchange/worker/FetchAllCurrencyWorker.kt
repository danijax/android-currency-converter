package com.danijax.paypayxchange.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.danijax.paypayxchange.api.Result
import com.danijax.paypayxchange.data.repository.ExchangeRateRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.LocalDateTime

/**
 * Background Job to fetch and cache all available currencies
 */
@HiltWorker
class FetchAllCurrencyWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @Assisted private val repository: ExchangeRateRepository ,
) : CoroutineWorker(appContext, workerParams) {


    private suspend fun fetchAndSaveCurrencies() : Result {
        return try {
            when(repository.fetchAllCurrencies().first()){
                is com.danijax.paypayxchange.api.Result.Error -> Result.retry()
                is com.danijax.paypayxchange.api.Result.Success -> Result.success()
            }
        }
        catch (ex: Exception){
            Result.retry()
        }
    }

    override suspend fun doWork(): Result {
        val lastDate = repository.getLastCurrencyUpdateTime().first()
        if (lastDate == null) {
            return fetchAndSaveCurrencies()
        } else {
            lastDate.let {
                val duration = Duration.between(it, LocalDateTime.now())
                if (duration.toHours() < 23) {
                   return fetchAndSaveCurrencies()
                }
                return Result.success()
            }
        }
    }
}