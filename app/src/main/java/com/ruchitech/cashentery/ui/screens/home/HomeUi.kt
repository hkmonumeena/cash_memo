package com.ruchitech.cashentery.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ruchitech.cashentery.ui.screens.add_transactions.AddTransaction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeUi(viewModel: HomeViewModel = viewModel(), navigateToAddTransaction: () -> Unit) {
    val transactions by viewModel.transactionsFlow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transactions") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = navigateToAddTransaction) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Transaction")
            }
        },
        content = { padding ->
            Column(modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEEECEC))
                .padding(padding)) {
              Row(modifier = Modifier.fillMaxWidth()) {
                  SumCountCard(Modifier.weight(1F),"Received",Color(0xFFDBF5DB),Color(0xFF228B22 ),130F)
                  SumCountCard(Modifier.weight(1F),"Paid",Color(0xFFFEECEC ),Color(0xFFB22222),320F)
              }
                TransactionList(transactions = transactions)
            }
        }
    )
}

@Composable
fun TransactionList(transactions: List<AddTransaction>) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(transactions) { transaction ->
            TransactionItem(transaction = transaction)
        }
    }
}

@Composable
fun TransactionItem(transaction: AddTransaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Transaction ID: ${transaction.id}")
            Text(text = "Date: ${transaction.date}")
            Text(text = "Type: ${transaction.type}")
            Text(text = "Account: ${transaction.account}")
            Text(text = "Amount: ${transaction.amount}")
            Text(text = "Remarks: ${transaction.remarks}")
            Text(text = "Main Category: ${transaction.mainCategory?.name}")
            Text(text = "Sub Category: ${transaction.subCategory?.name}")
        }
    }
}
