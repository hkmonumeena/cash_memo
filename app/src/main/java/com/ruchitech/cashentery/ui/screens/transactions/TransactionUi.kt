package com.ruchitech.cashentery.ui.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import com.ruchitech.cashentery.ui.screens.common_ui.EmptyTransactionUi
import com.ruchitech.cashentery.ui.theme.MainBackgroundSurface
import com.ruchitech.cashentery.ui.theme.nonScaledSp
import com.ruchitech.cashentery.ui.theme.sfMediumFont
import kotlinx.coroutines.launch

fun filterTransactionsByStatus(
    transactions: Map<String?, List<Transaction>>?,
    status: Transaction.Status
): Map<String?, List<Transaction>>? {
    return transactions?.mapValues { entry ->
        entry.value.filter { it.status == status }
    }?.filterValues { it.isNotEmpty() }
}

@Composable
fun TransactionUi(viewModel: TransactionsViewModel, onBack: () -> Unit) {
    val transactions by viewModel.groupByTag.collectAsState()
    val viewPagerState = rememberPagerState(initialPage = 0) { 5 }
    val tabs = listOf("All", "Cleared", "Pending", "Overdue", "Void")
    val selectedTabIndex = viewPagerState.currentPage
    val scope = rememberCoroutineScope()
    // Memoize filtered transactions
    val filteredTransactions by remember(transactions, selectedTabIndex) {
        derivedStateOf {
            when (tabs[selectedTabIndex]) {
                "Cleared" -> filterTransactionsByStatus(transactions, Transaction.Status.CLEARED)
                "Pending" -> filterTransactionsByStatus(transactions, Transaction.Status.PENDING)
                "Overdue" -> filterTransactionsByStatus(transactions, Transaction.Status.OVERDUE)
                "Void" -> filterTransactionsByStatus(transactions, Transaction.Status.VOID)
                else -> transactions // All transactions
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackgroundSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onBack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = null,
                        Modifier.size(35.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Transactions",
                    fontWeight = FontWeight.Bold,
                    fontFamily = sfMediumFont,
                    fontSize = 18.sp.nonScaledSp
                )
            }

            Icon(imageVector = Icons.Filled.Share, contentDescription = null)
        }

        TabRow(
            modifier = Modifier,
            selectedTabIndex = selectedTabIndex,
            containerColor = MainBackgroundSurface,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[viewPagerState.currentPage]),
                    color = Color.Black
                )
            }
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { scope.launch { viewPagerState.animateScrollToPage(index) } },
                    selectedContentColor = Color.Red,
                    unselectedContentColor = Color.Yellow
                ) {
                    Text(
                        text = tab,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(vertical = 14.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        HorizontalPager(
            state = viewPagerState, modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalAlignment = Alignment.Top
        ) { page ->
            if (filteredTransactions.isNullOrEmpty()) {
                EmptyTransactionUi()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    filteredTransactions?.forEach { (t, u) ->
                        item {
                            TransactionHeader(t, u[0].timeInMillis)
                        }
                        itemsIndexed(u) { _, item ->
                            TransactionItem(item)
                            Divider(thickness = 1.dp, color = Color(0xFFBBA76D))
                        }
                    }

                }

            }
        }
    }
}