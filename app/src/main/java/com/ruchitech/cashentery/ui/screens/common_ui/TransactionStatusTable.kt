package com.ruchitech.cashentery.ui.screens.common_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.ui.screens.home.formatToINR
import com.ruchitech.cashentery.ui.theme.nonScaledSp
import com.ruchitech.cashentery.ui.theme.sfSemibold

@Composable
fun TransactionStatusTable(
    creditPending: Double?,
    creditCleared: Double?,
    creditOverdue: Double?,
    creditVoid: Double?,
    debtPending: Double?,
    debtCleared: Double?,
    debtOverdue: Double?,
    debtVoid: Double?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, color = Color(0xFFBBA76D), shape = RoundedCornerShape(8.dp))
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .shadow(1.dp, shape = RoundedCornerShape(8.dp))
    ) {
        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFBBA76D))
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Type",
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.weight(0.6F),
                textAlign = TextAlign.Center,
                fontSize = 12.sp.nonScaledSp,
                color = Color.White
            )
         /*   Text(
                text = "PENDING",
                fontSize = 12.sp.nonScaledSp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = Color.White
            )*/

            Text(
                text = "CLEARED",
                fontSize = 12.sp.nonScaledSp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Text(
                text = "SWAP",
                fontSize = 12.sp.nonScaledSp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Text(
                text = "DUE",
                fontSize = 12.sp.nonScaledSp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Text(
                fontSize = 12.sp.nonScaledSp,
                text = "VOID",
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }

        Divider(color = Color(0xFFBBA76D), thickness = 1.dp)

        // Income Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Credit",
                fontSize = 12.sp.nonScaledSp,
                fontFamily = sfSemibold,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                modifier = Modifier
                    .weight(0.6F)
                    .padding(start = 5.dp),
                textAlign = TextAlign.Start,
                color = Color(0xFF4A4A4A)
            )
            Text(
                text = formatToINR(creditCleared ?: 0.0),
                fontSize = 12.sp.nonScaledSp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontFamily = sfSemibold,
                color = Color(0xFF4A4A4A)
            )
            Text(
                text = formatToINR(creditPending ?: 0.0),
                fontSize = 12.sp.nonScaledSp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontFamily = sfSemibold,
                color = Color(0xFF4A4A4A)
            )

            Text(
                text = formatToINR(creditOverdue ?: 0.0),
                fontSize = 12.sp.nonScaledSp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontFamily = sfSemibold,
                color = Color(0xFF4A4A4A)
            )
            Text(
                text = formatToINR(creditVoid?: 0.0),
                fontSize = 12.sp.nonScaledSp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontFamily = sfSemibold,
                color = Color(0xFF4A4A4A)
            )
        }

        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

        // Expense Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Debit",
                fontSize = 12.sp.nonScaledSp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                fontFamily = sfSemibold,
                modifier = Modifier
                    .weight(0.6F)
                    .padding(start = 5.dp),
                textAlign = TextAlign.Start,
                color = Color(0xFF4A4A4A)
            )
            Text(
                text = formatToINR(debtCleared ?: 0.0),
                fontSize = 12.sp.nonScaledSp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontFamily = sfSemibold,
                color = Color(0xFF4A4A4A)
            )
            Text(
                text = formatToINR(debtPending ?: 0.0),
                fontSize = 12.sp.nonScaledSp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontFamily = sfSemibold,
                color = Color(0xFF4A4A4A)
            )

            Text(
                text = formatToINR(debtOverdue ?: 0.0),
                fontSize = 12.sp.nonScaledSp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontFamily = sfSemibold,
                color = Color(0xFF4A4A4A)
            )
            Text(
                text = formatToINR(debtVoid ?: 0.0),
                fontSize = 12.sp.nonScaledSp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontFamily = sfSemibold,
                color = Color(0xFF4A4A4A)
            )
        }
    }
}


