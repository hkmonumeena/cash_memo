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
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ruchitech.cashentery.MainActivity
import com.ruchitech.cashentery.helper.calculateSum
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import com.ruchitech.cashentery.ui.screens.common_ui.EmptyTransactionUi
import com.ruchitech.cashentery.ui.screens.common_ui.SignOutConfirmationDialog
import com.ruchitech.cashentery.ui.screens.common_ui.TransactionStatusTable
import com.ruchitech.cashentery.ui.theme.Expense
import com.ruchitech.cashentery.ui.theme.Income
import com.ruchitech.cashentery.ui.theme.MainBackgroundSurface
import com.ruchitech.cashentery.ui.theme.nonScaledSp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    navigateToDetails: (transaction: List<Transaction>) -> Unit,
    onSignOut: () -> Unit,
) {
    val transactions by viewModel.groupByTag.collectAsState()
    val singleTrnx by viewModel.transactionsFlow.collectAsState()
    val sumOfIncome by viewModel.sumOfIncome.collectAsState()
    val sumOfExpense by viewModel.sumOfExpense.collectAsState()
    val context = LocalContext.current
    (context as MainActivity).lastTagUsed = ""
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    // SwipeRefreshState keeps track of the refresh state
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
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
                    fontSize = 15.sp
                )
                Row(horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = { viewModel.updateData() }) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                    IconButton(onClick = {
                        showDeleteDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.ExitToApp,
                            contentDescription = "Sign out"
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = { padding ->
            if (showDeleteDialog) {
                SignOutConfirmationDialog(
                    onConfirm = {
                        showDeleteDialog = false
                        viewModel.signout()
                        onSignOut()
                    },
                    onCancel = {
                        showDeleteDialog = false
                    }
                )
            }

            // Use SwipeRefresh to enable pull-to-refresh
            SwipeRefresh(
                modifier = Modifier.background(MainBackgroundSurface),
                state = swipeRefreshState,
                onRefresh = {
                    isRefreshing = true
                    viewModel.updateData() // Trigger your data refresh logic here
                    scope.launch {
                        delay(1500)
                        isRefreshing = false
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MainBackgroundSurface)
                        .padding(padding)

                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                    ) {
                        SumCountCard(
                            Modifier.weight(1f),
                            "Received",
                            Color(0xFFDBF5DB),
                            Color(0xFF228B22),
                            130f,
                            sumOfIncome ?: 0.0
                        ) {
                            navigateToTransactions()
                        }
                        SumCountCard(
                            Modifier.weight(1f),
                            "Paid",
                            Color(0xFFFEECEC),
                            Color(0xFFB22222),
                            320f,
                            sumOfExpense ?: 0.0
                        ) {
                            navigateToTransactions()
                        }
                    }
                    TransactionStatusTable(
                        creditPending = calculateSum(singleTrnx, Transaction.Type.CREDIT, Transaction.Status.PENDING),
                        creditCleared = calculateSum(singleTrnx, Transaction.Type.CREDIT, Transaction.Status.CLEARED),
                        creditOverdue = calculateSum(singleTrnx, Transaction.Type.CREDIT, Transaction.Status.OVERDUE),
                        creditVoid = calculateSum(singleTrnx, Transaction.Type.CREDIT, Transaction.Status.VOID),
                        debtPending = calculateSum(singleTrnx, Transaction.Type.DEBIT, Transaction.Status.PENDING),
                        debtCleared = calculateSum(singleTrnx, Transaction.Type.DEBIT, Transaction.Status.CLEARED),
                        debtOverdue = calculateSum(singleTrnx, Transaction.Type.DEBIT, Transaction.Status.OVERDUE),
                        debtVoid = calculateSum(singleTrnx, Transaction.Type.DEBIT, Transaction.Status.VOID)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Most used Tags(${transactions?.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )

                        Row(horizontalArrangement = Arrangement.SpaceBetween) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search"
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = Icons.Outlined.DateRange,
                                contentDescription = "Date Range"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(0.dp))
                    TransactionList(transactions = transactions, onClick = {
                        navigateToDetails(it)
                    })
                }
            }
        }
    )
}

@Composable
fun TransactionList(
    transactions: Map<String?, List<Transaction>>?,
    onClick: (transaction: List<Transaction>) -> Unit,
) {
    if (transactions.isNullOrEmpty()) {
        EmptyTransactionUi()
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            transactions.forEach { (t, u) ->
                if (u.isNotEmpty()) {
                    item {
                        Divider(color = Color((0xFFBBA76D)))
                        TransactionItem(transaction = u.first(), u, onClick = {
                            onClick(u)
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: Transaction,
    u: List<Transaction>,
    onClick: () -> Unit,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .padding(horizontal = 0.dp, vertical = 0.dp)
            .background(MainBackgroundSurface, shape = RoundedCornerShape(5.dp))
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
                    .size(25.dp)
                    .background(
                        color = Color(0xFFBBA76D),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = transaction.tag?.firstOrNull().toString(),
                    fontSize = 14.sp.nonScaledSp,
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
                    text = "last: ${formatMillisToDate(transaction.timeInMiles ?: 0)}",
                    fontSize = 12.sp.nonScaledSp,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.DarkGray
                )
            }

        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = formatToINR(u.sumOf { it.amount ?: 0.0 }),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp.nonScaledSp,
                modifier = Modifier.padding(end = 10.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "last was ${formatToINR(transaction.amount ?: 0.0)}",
                fontSize = 12.sp.nonScaledSp,
                fontFamily = FontFamily.SansSerif,
                color = if (transaction.type == Transaction.Type.CREDIT) Income else Expense,
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
