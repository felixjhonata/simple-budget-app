package com.felixjhonata.simplebudgetapp.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InputField(
    numDisplay: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "IDR",
            style = TextStyle(
                fontSize = 24.sp
            )
        )

        Spacer(Modifier.width(4.dp))

        Text(
            numDisplay,
            style = TextStyle(
                fontSize = 48.sp,
                textAlign = TextAlign.End
            ),
            overflow = TextOverflow.StartEllipsis
        )
    }
}