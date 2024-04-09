package com.danijax.paypayxchange.utils

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun LocalDateTime.format(): String {
    return DateTimeFormatter.ISO_DATE_TIME.format(this)

}

fun String.parseValidDate(): LocalDateTime {
    return LocalDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
}

fun Long.fromTimeStamp(): LocalDateTime {
    val instant = Instant.ofEpochMilli(this * 1000)
    val zoneId = ZoneId.systemDefault()
    return instant.atZone(zoneId).toLocalDateTime()
}

fun LocalDateTime.getTimeElapsedInMinutes(another: LocalDateTime): Long {
    return Duration.between(this, another).toMinutes()
}