package com.danijax.paypayxchange.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * Utility to handle work manager scheduling
 */
object WorkScheduler {

    operator fun invoke(context: Context) {
        val getCurrenciesRequest =
            OneTimeWorkRequestBuilder<FetchAllCurrencyWorker>()
                .setInitialDelay(Duration.ofSeconds(10))
                .setBackoffCriteria(
                    backoffPolicy = BackoffPolicy.LINEAR,
                    duration = Duration.ofSeconds(20)
                )
                .addTag("All Currencies Job")
                .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
            .build()

        val getBaseRatesWorkerRequest =
            OneTimeWorkRequestBuilder<FetchBaseRatesWorker>()
                .setBackoffCriteria(
                    backoffPolicy = BackoffPolicy.LINEAR,
                    duration = Duration.ofSeconds(20)
                )
                .addTag("Latest rates Job")
                .build()

        val updateRatesPeriodicRequest =
            PeriodicWorkRequestBuilder<FetchBaseRatesWorker>(
                30, TimeUnit.MINUTES, // repeatInterval (the period cycle)
                15, TimeUnit.MINUTES
            )
                .setInitialDelay(Duration.ofSeconds(30))
                .setBackoffCriteria(
                    backoffPolicy = BackoffPolicy.LINEAR,
                    duration = Duration.ofSeconds(20)
                )
                .addTag("Update rates Job")
                .setConstraints(constraints)
                .build()
        scheduleOneTimeWork(
            context,
            getCurrenciesRequest,
            ExistingWorkPolicy.KEEP,
            "GetCurrenciesWork"
        )
        scheduleOneTimeWork(
            context,
            getBaseRatesWorkerRequest,
            ExistingWorkPolicy.APPEND,
            "GetBaseRatesWork"
        )

        schedulePeriodicWork(context, updateRatesPeriodicRequest, "UpdateWork")
    }

    private fun schedulePeriodicWork(
        context: Context, periodicWorkRequest: PeriodicWorkRequest, wrkName: String
    ) {
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                wrkName,  // Unique name for the work
                ExistingPeriodicWorkPolicy.UPDATE,  // How to handle existing work with the same name
                periodicWorkRequest
            )
    }

    private fun scheduleOneTimeWork(
        context: Context,
        oneTimeWorkRequest: OneTimeWorkRequest,
        existingWorkPolicy: ExistingWorkPolicy,
        workName: String
    ) {
        WorkManager
            .getInstance(context).enqueueUniqueWork(
                workName,  // Unique name for the chain
                existingWorkPolicy,  // Use KEEP to ensure the chain isn't replaced
                oneTimeWorkRequest
            )
    }
}