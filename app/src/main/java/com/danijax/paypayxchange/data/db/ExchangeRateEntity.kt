package com.danijax.paypayxchange.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "rates", indices = [Index(value = ["base_symbol"], unique = true)])
data class ExchangeRateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo("base_symbol") val base: String,
    @ColumnInfo("last_update_time") val lastUpdate: Long,
    @ColumnInfo("rates") val rates: String,

)
