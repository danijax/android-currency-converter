package com.danijax.paypayxchange.data.db

import androidx.room.TypeConverter
import com.danijax.paypayxchange.model.CurrencyRate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.TimeZone

class Converters {

    @TypeConverter
    fun fromTimeStamp(timeStamp: Long) =
        LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timeStamp),
            TimeZone.getDefault().toZoneId())

    @TypeConverter
    fun fromLocalDateTime(date: LocalDateTime) = date.atZone(ZoneId.systemDefault()).toEpochSecond()

    @TypeConverter
    fun ratesToJson(rates: List<CurrencyRate>) = Json.encodeToString(rates)

    @TypeConverter
    fun jsonToRates(rates: String) = Json.decodeFromString<List<CurrencyRate>>(rates)
}