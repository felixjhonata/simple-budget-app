package com.felixjhonata.simplebudgetapp.add_transaction.model.uistate

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.format.DateTimeFormatter

class AddTransactionUiStateTest {

    @Test
    fun createUiState_currentDateIsOneAprilTwoThousandTwentySix_returnsCorrectUiState() {
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        val millis = 1775001600000 // 1 April 2026 00:00:00 UTC in millis

        val expectedUiState = AddTransactionUiState("01 April 2026", millis)
        val uiState = AddTransactionUiState.create(millis, formatter)

        assertEquals(expectedUiState, uiState)
    }

}