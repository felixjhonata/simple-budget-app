package com.felixjhonata.simplebudgetapp.shared.util

import android.content.ContentResolver
import android.net.Uri
import kotlinx.serialization.json.Json
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

inline fun <reified T> ContentResolver.writeToUri(uri: Uri, data: T) {
    this.openOutputStream(uri)?.use { outputStream ->
        val jsonString = Json.encodeToString(data)
        outputStream.write(jsonString.toByteArray())
    }
}

inline fun <reified T> ContentResolver.readFromUri(uri: Uri): T? {
    return this.openInputStream(uri)?.use { inputStream ->
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        Json.decodeFromString<T>(jsonString)
    }
}