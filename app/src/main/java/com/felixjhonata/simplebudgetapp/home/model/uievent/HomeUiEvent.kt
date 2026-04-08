package com.felixjhonata.simplebudgetapp.home.model.uievent

import com.felixjhonata.simplebudgetapp.shared.model.UiText

sealed interface HomeUiEvent {
    data class ShowSnackbar(val message: UiText): HomeUiEvent
}