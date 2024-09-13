package com.ruchitech.cashentery.ui.screens.home

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ruchitech.cashentery.MainActivity
import com.ruchitech.cashentery.R
import com.ruchitech.cashentery.WebViewActivity
import com.ruchitech.cashentery.helper.RequestState
import com.ruchitech.cashentery.helper.privacyPolicy
import com.ruchitech.cashentery.helper.termsAndCond
import com.ruchitech.cashentery.retrofit.model.Tags
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import com.ruchitech.cashentery.ui.screens.common_ui.BottomMenu
import com.ruchitech.cashentery.ui.screens.common_ui.DeleteAccountConfirmationDialog
import com.ruchitech.cashentery.ui.screens.common_ui.EmptyTransactionUi
import com.ruchitech.cashentery.ui.screens.common_ui.MultiSelectStatus
import com.ruchitech.cashentery.ui.screens.common_ui.MultiSelectType
import com.ruchitech.cashentery.ui.screens.common_ui.PaymentTypeSelection
import com.ruchitech.cashentery.ui.screens.common_ui.SampleDatePickerView
import com.ruchitech.cashentery.ui.screens.common_ui.SignOutConfirmationDialog
import com.ruchitech.cashentery.ui.screens.common_ui.SpacerHeight
import com.ruchitech.cashentery.ui.screens.common_ui.SpacerWidth
import com.ruchitech.cashentery.ui.screens.common_ui.TagSelectionChips
import com.ruchitech.cashentery.ui.screens.common_ui.TransactionStatusTable
import com.ruchitech.cashentery.ui.screens.common_ui.VerticalMoneyRangeSlider
import com.ruchitech.cashentery.ui.screens.transactions.FilterTrnx
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
    navigateToDetails: (tagName: String) -> Unit,
    onSignOut: () -> Unit,
) {
    val data by viewModel.data.collectAsState(initial = RequestState.Idle)
    val transactions by viewModel.groupByTag.collectAsState()
    val isLoading by viewModel.circularLoadingIndicator.collectAsState()
    val tags by viewModel.trxnTags.collectAsState()
    val trxnSummary by viewModel.trnxSummary.collectAsState()
    val context = LocalContext.current
    (context as MainActivity).lastTagUsed = ""
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    var selectedStatus by rememberSaveable { mutableStateOf<Transaction.Status?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    val trnxFilteredData by viewModel.filterTrnx.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sheetStateBottomMenu = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showBottomSheet by remember { mutableStateOf(false) }
    var showBottomMenuSheet by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Account") }
    var dateRangePickerShow by remember { mutableStateOf(true) }
    var startDate by rememberSaveable { mutableStateOf("") }
    var endDate by rememberSaveable { mutableStateOf("") }
    var startDateTemp by rememberSaveable { mutableStateOf("") }
    var endDateTemp by rememberSaveable { mutableStateOf("") }
    var selectedStatuses by rememberSaveable { mutableStateOf(listOf<Transaction.Status>()) }
    var selectedTypes by rememberSaveable { mutableStateOf(listOf<Transaction.Type>()) }
    var selectedAccounts by rememberSaveable { mutableStateOf(listOf<Transaction.Account>()) }
    var date1Temp by rememberSaveable { mutableStateOf("") }
    var date2Temp by rememberSaveable { mutableStateOf("") }
    var selectedRange by remember { mutableStateOf(0f..100000f) }
    var selectedTags by rememberSaveable { mutableStateOf(listOf<String>()) }
    var isFilteredData by rememberSaveable {
        mutableStateOf(false)
    }
    val tagsData by viewModel.categories.collectAsState()
    /*
        // Display results based on the state
        data.DisplayResult(
            onIdle = {
                Log.e("gkfhdfg", "HomeUi: IDle")
            },
            onLoading = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            },
            onSuccess = { transaction2s ->

                //    isRefreshing = false

                //writeTransactionsToJsonFile(context,transaction2s,"trnx.json")

                viewModel.fetchTransactions(transaction2s)
            },
            onError = {
                Log.e("gkfhdfg", "HomeUi: $it")
            }
        )
    */

// Filter transactions based on the selected status
    // Use remember to cache the filtered transactions
    val transactionFilters = remember(transactions, selectedStatus) {
        transactions?.mapValues { (_, transactionList) ->
            transactionList.filter { transaction ->
                selectedStatus == null || transaction.status == selectedStatus
            }
        }?.filterValues { filteredList ->
            filteredList.isNotEmpty()
        }
    }


    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        showBottomMenuSheet = true
                    }) {
                        Icon(imageVector = Icons.Filled.Menu, contentDescription = null)
                    }

                    Text(
                        text = "Cash Entry",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp.nonScaledSp
                    )
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

            if (showDeleteAccountDialog) {
                DeleteAccountConfirmationDialog(
                    onConfirm = {
                        showDeleteAccountDialog = false
                        viewModel.deleteAccount {
                            onSignOut()
                        }
                    },
                    onCancel = {
                        showDeleteAccountDialog = false
                    }
                )
            }

            // Use SwipeRefresh to enable pull-to-refresh
            SwipeRefresh(
                modifier = Modifier.background(MainBackgroundSurface),
                state = swipeRefreshState,
                onRefresh = {
                    isRefreshing = true
                    viewModel.refreshData() // Trigger your data refresh logic here
                    scope.launch {
                        delay(2500)
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
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
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
                                trxnSummary?.totalCredits ?: 0.0
                            ) {
                                navigateToTransactions()
                            }
                            SumCountCard(
                                Modifier.weight(1f),
                                "Paid",
                                Color(0xFFFEECEC),
                                Color(0xFFB22222),
                                320f,
                                trxnSummary?.totalDebits ?: 0.0
                            ) {
                                navigateToTransactions()
                            }
                        }
                        TransactionStatusTable(
                            creditPending = trxnSummary?.creditsByStatus?.PENDING ?: 0.0,
                            creditCleared = trxnSummary?.creditsByStatus?.CLEARED ?: 0.0,
                            creditOverdue = trxnSummary?.creditsByStatus?.OVERDUE ?: 0.0,
                            creditVoid = trxnSummary?.creditsByStatus?.VOID ?: 0.0,
                            debtPending = trxnSummary?.debitsByStatus?.PENDING ?: 0.0,
                            debtCleared = trxnSummary?.debitsByStatus?.CLEARED ?: 0.0,
                            debtOverdue = trxnSummary?.debitsByStatus?.OVERDUE ?: 0.0,
                            debtVoid = trxnSummary?.debitsByStatus?.VOID ?: 0.0,
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tags (${tags?.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp.nonScaledSp
                            )

                            Box(modifier = Modifier.clickable { showBottomSheet = true }) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(
                                        text = if (isFilteredData) "Filtered" else "", // Display the selected filter
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp.nonScaledSp,
                                        color = Color.Gray,
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                    SpacerWidth(8)
                                    Image(
                                        painterResource(id = R.drawable.ic_filter),
                                        contentDescription = "Date Range",
                                        modifier = Modifier
                                            .size(25.dp)
                                    )
                                }
                            }
                        }
                        /*
                                                FilterRow(
                                                    transactionFilters = transactionFilters,
                                                    selectedStatus = selectedStatus,
                                                    onStatusSelected = { newStatus ->
                                                        selectedStatus = newStatus
                                                    }
                                                )
                        */
                        Spacer(modifier = Modifier.height(8.dp))
                        TransactionList(transactions = tags, onClick = {
                            navigateToDetails(it)
                        })
                    }
                }
                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            showBottomSheet = false
                        }, sheetState = sheetState,
                        dragHandle = {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .background(MainBackgroundSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                BottomSheetDefaults.DragHandle()
                                HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter))
                            }
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxSize(0.8F)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MainBackgroundSurface)

                            ) {
                                Text(
                                    text = "Sort",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp.nonScaledSp,
                                    modifier = Modifier.padding(start = 16.dp, top = 5.dp)
                                )
                                SpacerHeight(8)
                                HorizontalDivider()
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MainBackgroundSurface)
                                        .padding(bottom = 58.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(0.35F)
                                            .padding(top = 0.dp)
                                    ) {
                                        // List of sort options
                                        val options =
                                            listOf(
                                                "Account",
                                                "Date",
                                                "Status",
                                                "Type",
                                                "Amount",
                                                "Tag"
                                            )

                                        // Iterate through the options and create a Box for each one
                                        options.forEach { option ->
                                            val isSelected = selectedOption == option
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(45.dp)
                                                    .background(
                                                        if (isSelected) Color(0xFFDACB9F) else MainBackgroundSurface
                                                    )
                                                    .clickable { selectedOption = option },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = option,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp.nonScaledSp,
                                                    modifier = Modifier
                                                )
                                                HorizontalDivider(
                                                    modifier = Modifier
                                                        .align(Alignment.BottomCenter)
                                                        .fillMaxWidth()
                                                )
                                            }
                                        }
                                    }
                                    VerticalDivider()
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1F)
                                            .background(MainBackgroundSurface)

                                    ) {
                                        when (selectedOption) {
                                            "Account" -> {
                                                PaymentTypeSelection(selectedOptions = selectedAccounts,
                                                    onSelectionChange = { newSelection ->
                                                        selectedAccounts = newSelection
                                                        // Handle the new selection
                                                    })
                                            }

                                            "Date" -> {
                                                if (dateRangePickerShow) {
                                                    Log.e(
                                                        "yrdhgftghr",
                                                        "TransactionUi: $startDate -> $date1Temp"
                                                    )
                                                    SampleDatePickerView(
                                                        dates = { date1, date2, date1L, date2L ->
                                                            dateRangePickerShow = true
                                                            Log.e(
                                                                "hfhgfhgfhfg",
                                                                "TransactionUi: $date1 -> $date1L"
                                                            )
                                                            startDate = date1
                                                            endDate = date2
                                                            date1Temp = date1L.toString()
                                                            date2Temp = date2L.toString()
                                                        },
                                                        onDismissRequest = {
                                                            dateRangePickerShow = true
                                                        },
                                                        initialSelectedStartDateMillis = if (date1Temp.isNotEmpty()) date1Temp.toLong() else null,
                                                        initialSelectedEndDateMillis = if (date2Temp.isNotEmpty()) date2Temp.toLong() else null
                                                    )
                                                }
                                            }

                                            "Status" -> {
                                                MultiSelectStatus(selectedStatuses = selectedStatuses,
                                                    onStatusSelected = { status ->
                                                        selectedStatuses =
                                                            if (selectedStatuses.contains(status)) {
                                                                selectedStatuses - status
                                                            } else {
                                                                selectedStatuses + status
                                                            }
                                                    })

                                            }

                                            "Type" -> {
                                                MultiSelectType(selectedTypes = selectedTypes,
                                                    onTypeSelected = { type ->
                                                        selectedTypes =
                                                            if (selectedTypes.contains(type)) {
                                                                selectedTypes - type
                                                            } else {
                                                                selectedTypes + type
                                                            }
                                                    })
                                            }

                                            "Amount" -> {
                                                VerticalMoneyRangeSlider(range = selectedRange,
                                                    onRangeChange = { newRange ->
                                                        selectedRange = newRange
                                                    })
                                            }

                                            "Tag" -> {
                                                TagSelectionChips(availableTags = tagsData,
                                                    selectedTags = selectedTags,
                                                    onTagSelectionChange = { newSelection ->
                                                        selectedTags = newSelection
                                                        // Handle the updated selection
                                                    })
                                            }
                                        }
                                    }
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter),
                            ) {
                                HorizontalDivider()
                                SpacerHeight(10)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Absolute.Center
                                ) {
                                    TextButton(onClick = {
                                        showBottomSheet = false
                                        val data = FilterTrnx(
                                            account = listOf(),
                                            amount = FilterTrnx.Amount(
                                                max = 100000.0, min = 0.0
                                            ),
                                            authId = "",
                                            date = FilterTrnx.Date(
                                                end = "", start = ""
                                            ),
                                            limit = 100,
                                            page = 1,
                                            status = listOf(),
                                            tag = listOf(),
                                            type = listOf()
                                        )

                                        startDate = ""
                                        endDate = ""
                                        selectedStatuses = listOf()
                                        selectedTypes = listOf()
                                        selectedAccounts = listOf()
                                        selectedRange = 0f..100000f
                                        selectedTags = listOf()
                                        date1Temp = ""
                                        date2Temp = ""
                                        viewModel.getFilteredTransactions(data)
                                        isFilteredData = false

                                    }, modifier = Modifier.weight(0.4F)) {
                                        Text(
                                            text = "Clear All",
                                            color = Color.Red,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 16.sp.nonScaledSp
                                        )
                                    }

                                    SpacerWidth(16)

                                    Button(
                                        onClick = {
                                            val tempData = trnxFilteredData
                                            tempData.date =
                                                FilterTrnx.Date(start = startDate, end = endDate)
                                            tempData.status = selectedStatuses
                                            tempData.type = selectedTypes
                                            tempData.account = selectedAccounts
                                            tempData.tag = selectedTags
                                            tempData.amount = FilterTrnx.Amount(
                                                min = selectedRange.start.toDouble(),
                                                max = selectedRange.endInclusive.toDouble()
                                            )
                                            viewModel.getFilteredTransactions(tempData)
                                            showBottomSheet = false
                                            isFilteredData = true
                                        },
                                        modifier = Modifier
                                            .weight(1F)
                                            .padding(horizontal = 10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                        shape = RoundedCornerShape(5.dp)
                                    ) {
                                        Text(
                                            text = "Apply",
                                            color = Color.White,
                                            fontSize = 16.sp.nonScaledSp,
                                            modifier = Modifier.padding(vertical = 5.dp)
                                        )
                                    }
                                }
                                SpacerHeight(10)
                            }

                        }
                    }
                }

                if (showBottomMenuSheet) {
                    BottomMenu(sheetState = sheetStateBottomMenu, onDismissRequest = {
                        showBottomMenuSheet = false
                    }) {
                        when (it) {
                            "Profile" -> {}
                            "Terms and Conditions" -> {
                                val intent = Intent(context, WebViewActivity::class.java)
                                intent.putExtra("url", termsAndCond)
                                intent.putExtra("type", "Terms & Conditions")
                                context.startActivity(intent)
                            }

                            "Privacy Policy" -> {
                                val intent = Intent(context, WebViewActivity::class.java)
                                intent.putExtra("url", privacyPolicy)
                                intent.putExtra("type", "Privacy Policy")
                                context.startActivity(intent)
                            }

                            "Sign Out" -> {
                                showDeleteDialog = true
                            }

                            "Delete Account" -> {
                                showDeleteAccountDialog = true
                            }
                        }
                    }
                }
            }
        })
}

@Composable
fun TransactionList(
    transactions: Tags?,
    onClick: (tagName: String) -> Unit,
) {
    if (transactions.isNullOrEmpty()) {
        EmptyTransactionUi()
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {

            itemsIndexed(transactions) { index: Int, item: Tags.TagsItem ->
                HorizontalDivider(color = Color((0xFFBBA76D)))
                TransactionItem(transaction = item, onClick = {
                    onClick(item.tag)
                })
            }

        }
    }
}

@Composable
private fun TransactionItem(
    transaction: Tags.TagsItem,
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
                    text = transaction.tag.firstOrNull().toString(),
                    fontSize = 14.sp.nonScaledSp,
                    color = Color.White,
                    modifier = Modifier.padding(0.dp)
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Column(modifier = Modifier.padding(vertical = 5.dp)) {
                Text(
                    text = transaction.tag,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp.nonScaledSp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "last: ${transaction.lastUsedDate}",
                    fontSize = 12.sp.nonScaledSp,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.DarkGray
                )
            }

        }

        Column(horizontalAlignment = Alignment.End) {
            //val netBalance = calculateNetBalance(u)
            Text(
                text = formatToINR(transaction.balance),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp.nonScaledSp,
                modifier = Modifier.padding(end = 10.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            /*          Text(
                          text = formatNetBalanceMessage(netBalance),
                          modifier = Modifier
                              .fillMaxWidth()
                              .padding(top = 10.dp),
                          textAlign = TextAlign.Center
                      )*/
            Text(
                text = transaction.settlementMessage,
                fontSize = 12.sp.nonScaledSp,
                fontFamily = FontFamily.SansSerif,
                color = if (transaction.lastTransactionType == Transaction.Type.CREDIT.toString()) Income else Expense,
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
