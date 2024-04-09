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

/**
 * Background work to fetch and save available rates
 * Job will also infer unavailable rates and insert/update in the db
 * Should be constrained to run once for initial app setup and another
 * Job to run every 30 minutes to keep the rates updated
 */
@HiltWorker
class FetchBaseRatesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @Assisted private val repository: ExchangeRateRepository,
) : CoroutineWorker(appContext, workerParams) {


    private suspend fun fetchAndSaveBaseRates(): Result {
        return try {
            when(repository.fetchLatestRates("USD").first()){
                is com.danijax.paypayxchange.api.Result.Error -> Result.retry()
                is com.danijax.paypayxchange.api.Result.Success -> Result.success()
            }


        } catch (ex: Exception) {
            Result.retry()
        }
    }

    override suspend fun doWork(): Result {
        return fetchAndSaveBaseRates()
    }
}