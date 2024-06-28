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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.ui.screens.add_transactions.AddTransaction
import com.ruchitech.cashentery.ui.screens.add_transactions.Type
import com.ruchitech.cashentery.ui.screens.home.formatMillisToDate
import com.ruchitech.cashentery.ui.screens.home.formatToINR
import com.ruchitech.cashentery.ui.theme.Expense
import com.ruchitech.cashentery.ui.theme.Income
import com.ruchitech.cashentery.ui.theme.nonScaledSp

@Composable
fun TransactionItem(item: AddTransaction) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
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
                fontSize = 12.sp.nonScaledSp,
                modifier = Modifier
            )
            Text(
                text = formatToINR(item.amount!!.toDouble()),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp.nonScaledSp,
                color = if (item.type == Type.CREDIT) Income else Expense,
                modifier = Modifier
            )
        }

        Text(
            text = item.remarks ?: "",
            fontSize = 9.sp.nonScaledSp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 10.dp)

        )
        Spacer(modifier = Modifier.height(10.dp))

    }
}

@Composable
fun TransactionHeader(u: String?, timeInMiles: Long?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color(0xFFF6F7F9))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Outlined.DateRange, contentDescription = null, tint = Color.Gray)
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = formatMillisToDate(timeInMiles?: 0),
            color = Color.DarkGray,
            fontSize = 11.sp.nonScaledSp
        )
    }
}