package com.felixjhonata.simplebudgetapp.shared.model

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText {
    data class StringResource(@get:StringRes val id: Int): UiText()

    fun asString(context: Context): String {
        return when(this) {
            is StringResource -> context.getString(id)
        }
    }
}