package com.ruchitech.cashentery.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ruchitech.cashentery.ui.screens.add_transactions.AddTransaction
import com.ruchitech.cashentery.ui.theme.nonScaledSp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDateTime(originalDateString: String): String {
    // Define the input and output date formats
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd, HH:mm:ss", Locale.getDefault())
    val outputDateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())

    return try {
        // Step 1: Parse the original date string into a Date object
        val date: Date = inputDateFormat.parse(originalDateString) ?: return ""

        // Step 2: Format the Date object into the desired format
        outputDateFormat.format(date)
    } catch (e: Exception) {
        // Handle the case where parsing fails
        e.printStackTrace()
        ""
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeUi(viewModel: HomeViewModel = viewModel(), navigateToAddTransaction: () -> Unit) {
    val transactions by viewModel.transactionsFlow.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Monu Meena",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp.nonScaledSp
                )
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Add Transaction"
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = navigateToAddTransaction) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Transaction")
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF6F7F9))
                    .padding(padding)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    SumCountCard(
                        Modifier.weight(1F),
                        "Received",
                        Color(0xFFDBF5DB),
                        Color(0xFF228B22),
                        130F
                    )
                    SumCountCard(
                        Modifier.weight(1F),
                        "Paid",
                        Color(0xFFFEECEC),
                        Color(0xFFB22222),
                        320F
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Most used Tags(15)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp.nonScaledSp
                    )

                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Add Transaction"
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = "Add Transaction"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .background(Color.White, shape = RoundedCornerShape(5.dp)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row {
            Spacer(modifier = Modifier.width(5.dp))
            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .size(22.dp)
                    .background(
                        color = Color.LightGray,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = transaction.tag?.firstOrNull().toString(),
                    fontSize = 12.sp.nonScaledSp,
                    color = Color.White,
                    modifier = Modifier.padding(0.dp)
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Column(modifier = Modifier.padding(vertical = 5.dp)) {
                Text(
                    text = transaction.tag ?: "",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp.nonScaledSp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Last: ${formatDateTime(transaction.date ?: "2024-06-26, 16:32:54")}",
                    fontSize = 10.sp.nonScaledSp,
                    color = Color.DarkGray
                )
            }

        }
        Text(
            text = formatToINR(1325.0),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp.nonScaledSp,
            modifier = Modifier.padding(end = 10.dp)
        )
        /*
                            Column {

                                Text(
                                    text = "Last: 25 June 2025",
                                    fontSize = 11.sp.nonScaledSp,
                                    color = Color.LightGray
                                )
                            }
        */


    }


    /*
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
                Text(text = "Tag: ${transaction.tag}")
            }
        }
    */
}
