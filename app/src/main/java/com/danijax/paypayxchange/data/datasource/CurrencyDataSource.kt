package com.danijax.paypayxchange.data.datasource

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.danijax.paypayxchange.model.Currency
import com.danijax.paypayxchange.utils.dataStore
import com.danijax.paypayxchange.utils.format
import com.danijax.paypayxchange.utils.parseValidDate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import javax.inject.Inject

class CurrencyDataSource @Inject constructor(@ApplicationContext val context: Context) {

    suspend fun saveCurrencies(currencies: String) {
        context.dataStore.edit { preferences ->
            preferences[ALL_CURRENCIES] = currencies
            preferences[TIME_STAMP] = LocalDateTime.now().format()
        }
    }

    fun getAllCurrencies(): Flow<Map<String, String>> {
        return context.dataStore.data.map { pref ->
            val json = pref[ALL_CURRENCIES] ?: ""
            if (json.isEmpty()) {
                emptyMap()
            } else {
                val items = Json.decodeFromString<Map<String, String>>(json)
                items
            }
        }
    }

    suspend fun saveBaseCurrency(base: Currency) {
        context.dataStore.edit { preferences ->
            preferences[BASE_CURRENCY] = Json.encodeToString(base)
        }
    }

    fun getBaseCurrency() : Flow<Currency>{
        return context.dataStore.data.map { pref ->
            val json = pref[BASE_CURRENCY] ?: ""
            if (json.isEmpty()) {
                Currency(symbol = "USD", country = "Nigeria")
            } else {
                val items = Json.decodeFromString<Currency>(json)
                items
            }
        }
    }
    fun getLastUpdateTime() : Flow<LocalDateTime?> {
        return context.dataStore.data.map { pref->
            val date = pref[TIME_STAMP]
            date?.parseValidDate()
        }
    }

    suspend fun saveRates(currencies: String, lastUpdate: Long) {
        context.dataStore.edit { preferences ->
            preferences[BASE_CURRENCY_RATES] = currencies
            preferences[LAST_UPDATE] = lastUpdate.toString()
        }
    }

    fun getRates(): Flow<Pair<Long, Map<String, Float>>> {
        return context.dataStore.data.map { pref ->
            val json = pref[ALL_CURRENCIES] ?: ""
            val time = pref[LAST_UPDATE] ?: "0"
            if (json.isEmpty()) {
                time.toLong() to emptyMap<String, Float>()
            } else {
                val items = Json.decodeFromString<Map<String, Float>>(json)
                time.toLong() to items
            }
        }


    }

    fun getRatesLastUpdateTime(): Flow<Long> {
        return context.dataStore.data.map { pref ->
            val time = pref[LAST_UPDATE] ?: "0"
            time.toLong()
        }


    }


    suspend fun updatePopulateJobStatus(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[POPULATE_JOB_COMPETED] = completed.toString()
        }
    }

    fun getPopulateJobStatus() : Flow<Boolean>{
        return context.dataStore.data.map { preferences ->
            val status = preferences[POPULATE_JOB_COMPETED]
            status.toBoolean()
        }
    }

    companion object {
        val ALL_CURRENCIES = stringPreferencesKey("all_currencies")
        val TIME_STAMP = stringPreferencesKey("time_stamp")
        val LAST_UPDATE = stringPreferencesKey("last_update")
        val BASE_CURRENCY = stringPreferencesKey("base")
        val BASE_CURRENCY_RATES = stringPreferencesKey("rates")
        val POPULATE_JOB_COMPETED = stringPreferencesKey("populate_job_completed")
    }

}