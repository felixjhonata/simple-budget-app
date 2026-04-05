package com.felixjhonata.simplebudgetapp.util

import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale

fun Double.toLocalizedString(
    locale: Locale = Locale.getDefault(),
    maxFractionDigits: Int = 0
): String = NumberFormat.getNumberInstance(locale).apply {
    maximumFractionDigits = maxFractionDigits
}.format(this)

fun Long.convertEpochSecondToLocalDateTime(
    zoneId: ZoneId = ZoneId.systemDefault()
): LocalDateTime = Instant.ofEpochSecond(this).atZone(zoneId).toLocalDateTime()

fun Long.convertEpochMillisToLocalDateTime(
    zoneId: ZoneId = ZoneId.systemDefault()
): LocalDateTime = Instant.ofEpochMilli(this).atZone(zoneId).toLocalDateTime()