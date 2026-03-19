package com.felixjhonata.simplebudgetapp.util

import java.text.NumberFormat
import java.util.Locale

fun Int.toLocalizedString(
    locale: Locale = Locale.getDefault()
): String = NumberFormat.getNumberInstance(locale).format(this)

fun Double.toLocalizedString(
    locale: Locale = Locale.getDefault(),
    maxFractionDigits: Int = 0
): String = NumberFormat.getNumberInstance(locale).apply {
    maximumFractionDigits = maxFractionDigits
}.format(this)