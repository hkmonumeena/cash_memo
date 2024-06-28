package com.ruchitech.cashentery.ui.screens.transactions

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.ui.theme.nonScaledSp

@Composable
fun TransactionUi(viewModel: TransactionsViewModel) {
    val transactions by viewModel.groupByTag.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Transactions",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp.nonScaledSp
            )

            Icon(imageVector = Icons.Filled.Share, contentDescription = null)
        }
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp)
        ) {

            transactions?.forEach { (t, u) ->
                item {
                    TransactionHeader(t, u[0].timeInMiles)
                    Log.e("gfdgfdg", "TransactionUi: $t")
                }
                itemsIndexed(u) { index, item ->
                    TransactionItem(item)
                    Divider(thickness = 1.dp, color = Color(0xFFEFEFF0))
                }
            }
        }

    }
}