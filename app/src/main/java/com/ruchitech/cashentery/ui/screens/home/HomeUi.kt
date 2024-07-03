package com.ruchitech.cashentery.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ruchitech.cashentery.ui.screens.add_transactions.AddTransactionData
import com.ruchitech.cashentery.ui.screens.add_transactions.Type
import com.ruchitech.cashentery.ui.theme.Expense
import com.ruchitech.cashentery.ui.theme.Income
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
fun HomeUi(
    viewModel: HomeViewModel = viewModel(),
    navigateToAddTransaction: () -> Unit,
    navigateToTransactions: () -> Unit,
    navigateToDetails: (transaction:List<AddTransactionData>) -> Unit,
) {
    val transactions by viewModel.groupByTag.collectAsState()
    val sumOfIncome by viewModel.sumOfIncome.collectAsState()
    val sumOfExpense by viewModel.sumOfExpense.collectAsState()

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
                IconButton(onClick = { viewModel.updateData() }) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
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
        floatingActionButtonPosition = FabPosition.Center,
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFEFEFF0))
                    .padding(padding)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    SumCountCard(
                        Modifier.weight(1F),
                        "Received",
                        Color(0xFFDBF5DB),
                        Color(0xFF228B22),
                        130F,
                        sumOfIncome ?: 0.0
                    ) {
                        navigateToTransactions()
                    }
                    SumCountCard(
                        Modifier.weight(1F),
                        "Paid",
                        Color(0xFFFEECEC),
                        Color(0xFFB22222),
                        320F,
                        sumOfExpense ?: 0.0
                    ) {
                        navigateToTransactions()
                    }
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
                TransactionList(transactions = transactions, onClick = {
                    navigateToDetails(it)
                })
            }
        }
    )
}

@Composable
fun TransactionList(transactions: Map<String?, List<AddTransactionData>>?,onClick: (transaction:List<AddTransactionData>) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {

        transactions?.forEach { (t, u) ->
            if (u.isNotEmpty()) {
                item {
                    TransactionItem(transaction = u.first(), u, onClick = {
                        onClick(u)
                    })
                }
            }

        }
        /*     items(transactions.) { transaction ->
                 TransactionItem(transaction = transaction)
             }*/
    }
}

@Composable
private fun TransactionItem(
    transaction: AddTransactionData,
    u: List<AddTransactionData>,
    onClick: () -> Unit,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 0.dp, vertical = 5.dp)
            .background(Color.White, shape = RoundedCornerShape(5.dp))
            .clickable {
                onClick()
            },
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
                    fontSize = 11.sp.nonScaledSp,
                    color = Color.White,
                    modifier = Modifier.padding(0.dp)
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Column(modifier = Modifier.padding(vertical = 5.dp)) {
                Text(
                    text = transaction.tag ?: "",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp.nonScaledSp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "last: ${formatMillisToDate(transaction.timeInMiles ?: 0)}",
                    fontSize = 9.sp.nonScaledSp,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.DarkGray
                )
            }

        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = formatToINR(u.sumOf { it.amount ?: 0.0 }),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp.nonScaledSp,
                modifier = Modifier.padding(end = 10.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "last was ${formatToINR(transaction.amount ?: 0.0)}",
                fontSize = 9.sp.nonScaledSp,
                fontFamily = FontFamily.SansSerif,
                color = if (transaction.type == Type.CREDIT) Income else Expense,
                modifier = Modifier.padding(end = 10.dp)
            )
        }


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
