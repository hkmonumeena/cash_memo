package com.ruchitech.cashentery.ui.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import com.ruchitech.cashentery.ui.screens.home.formatMillisToDate
import com.ruchitech.cashentery.ui.screens.home.formatToINR
import com.ruchitech.cashentery.ui.theme.Expense
import com.ruchitech.cashentery.ui.theme.Income
import com.ruchitech.cashentery.ui.theme.MainBackgroundSurface
import com.ruchitech.cashentery.ui.theme.nonScaledSp
import com.ruchitech.cashentery.ui.theme.sfMediumFont
import com.ruchitech.cashentery.ui.theme.sfSemibold

@Composable
fun TransactionItem(item: Transaction) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MainBackgroundSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.tag ?: "",
                fontSize = 14.sp.nonScaledSp,
                modifier = Modifier
            )
            Text(
                text = formatToINR(item.amount!!.toDouble()),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp.nonScaledSp,
                color = if (item.type == Transaction.Type.CREDIT) Income else Expense,
                modifier = Modifier
            )
        }

        Row(modifier = Modifier.fillMaxWidth().padding(end = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            var statusColor = Color(0xFF4CAF50)
            val statusStr =
                when (item.status) {
                    Transaction.Status.PENDING -> {
                        statusColor = Color(0xFFFF9800)
                        "Pending"
                    }

                    Transaction.Status.CLEARED -> {
                        statusColor = Color(0xFF4CAF50)
                        "Cleared"
                    }

                    Transaction.Status.OVERDUE -> {
                        statusColor = Color(0xFFF44336)
                        "Overdue"
                    }

                    Transaction.Status.VOID -> {
                        statusColor = Color(0xFFE040FB)
                        "Void"
                    }

                    null -> {
                        statusColor = Color(0xFF4CAF50)
                        "Cleared"
                    }
                }
            Text(
                text = item.remarks ?: "",
                fontSize = 12.sp.nonScaledSp,
                color = Color.DarkGray,
                lineHeight = 14.sp.nonScaledSp,
                modifier = Modifier.weight(1f).padding(start = 10.dp)

            )

            Text(
                text = statusStr,
                fontSize = 10.sp.nonScaledSp,
                fontFamily = sfSemibold,
                color = statusColor,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.2F)
            )
        }


        Spacer(modifier = Modifier.height(10.dp))

    }
}

@Composable
fun TransactionHeader(u: String?, timeInMiles: Long?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color(0xFFDACB9F))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Outlined.DateRange, contentDescription = null, tint = Color.Gray)
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = formatMillisToDate(timeInMiles?: 0),
            color = Color.DarkGray,
            fontSize = 12.sp.nonScaledSp,
            fontFamily = sfMediumFont
        )
    }
}