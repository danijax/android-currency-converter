package com.danijax.paypayxchange

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.danijax.paypayxchange.api.ExchangeRateService
import com.danijax.paypayxchange.data.datasource.CurrencyDataSource
import com.danijax.paypayxchange.data.repository.ExchangeRateRepository
import com.danijax.paypayxchange.worker.FetchAllCurrencyWorker
import com.danijax.paypayxchange.worker.FetchBaseRatesWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: PaypayExchangeWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()
}

class PaypayExchangeWorkerFactory @Inject constructor(
    val api: ExchangeRateService,
    private val dataSource: CurrencyDataSource,
    private val repository: ExchangeRateRepository,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ) =
        when (workerClassName) {
            FetchAllCurrencyWorker::class.java.name -> FetchAllCurrencyWorker(
                appContext,
                workerParameters,
                repository = repository
            )

            else -> FetchBaseRatesWorker(appContext, workerParameters, repository)
        }

}
