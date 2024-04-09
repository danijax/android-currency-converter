package com.danijax.paypayxchange.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {
    @Upsert
    fun insert(exchangeRateEntity: ExchangeRateEntity)
    @Upsert
    fun insertAll(exchangeRateEntity: List<ExchangeRateEntity>)

    @Query("SELECT * FROM rates WHERE base_symbol = :base")
    fun get(base: String): Flow<ExchangeRateEntity>

    @Query("SELECT * FROM rates" )
    fun getAll(): Flow<List<ExchangeRateEntity>>
}