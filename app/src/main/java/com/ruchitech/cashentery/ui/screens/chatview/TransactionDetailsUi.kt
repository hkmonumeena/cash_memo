package com.ruchitech.cashentery.ui.screens.chatview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.ui.screens.add_transactions.AddTransactionData
import com.ruchitech.cashentery.ui.screens.add_transactions.Type
import com.ruchitech.cashentery.ui.screens.home.formatMillisToDate
import com.ruchitech.cashentery.ui.screens.home.formatToINR
import com.ruchitech.cashentery.ui.theme.Expense
import com.ruchitech.cashentery.ui.theme.Income
import com.ruchitech.cashentery.ui.theme.nonScaledSp
import com.ruchitech.cashentery.ui.theme.sfMediumFont
import java.util.Date
import java.util.Locale


@Composable
fun TransactionDetailsUi(viewModel: TransactionDetailsViewModel) {
    val data by viewModel.transactionsFlow.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFF0))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .size(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = data.first().tag!!.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    },
                    fontFamily = sfMediumFont,
                    fontSize = 16.sp.nonScaledSp,
                    color = Color.Black
                )
            }
            Divider()
            LazyColumn {
                items(data) {
                    ChatBox(transaction = it)
                }
            }
        }
    }
}

@Composable
private fun ChatBox(transaction: AddTransactionData) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .defaultMinSize(minWidth = 200.dp)
                .background(Color.White, shape = RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .align(if (transaction.type == Type.CREDIT) Alignment.CenterStart else Alignment.CenterEnd),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = formatToINR(transaction.amount!!.toDouble()),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp.nonScaledSp,
                color = if (transaction.type == Type.CREDIT) Income else Expense,
                modifier = Modifier
            )

            Text(
                text = transaction.remarks ?: "",
                fontFamily = sfMediumFont,
                fontSize = 11.sp.nonScaledSp.nonScaledSp,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                text = "${if (transaction.type == Type.CREDIT) "Received on" else "Paid on"}: ${
                    formatMillisToDate(
                        Date().time
                    )
                }",
                fontSize = 9.sp.nonScaledSp,
                fontFamily = FontFamily.SansSerif,
                color = Color.Gray
            )
        }
    }
}
