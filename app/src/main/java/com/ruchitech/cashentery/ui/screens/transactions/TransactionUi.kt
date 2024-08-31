package com.ruchitech.cashentery.ui.screens.transactions

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ruchitech.cashentery.helper.Result
import com.ruchitech.cashentery.ui.screens.add_transactions.AmountField
import com.ruchitech.cashentery.ui.screens.add_transactions.DateField
import com.ruchitech.cashentery.ui.screens.add_transactions.RemarksField
import com.ruchitech.cashentery.ui.screens.add_transactions.SaveButton
import com.ruchitech.cashentery.ui.screens.add_transactions.TagTextField
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import com.ruchitech.cashentery.ui.screens.common_ui.DeleteConfirmationDialog
import com.ruchitech.cashentery.ui.screens.common_ui.EmptyTransactionUi
import com.ruchitech.cashentery.ui.screens.common_ui.LoadingScreen
import com.ruchitech.cashentery.ui.screens.common_ui.MultiSelectStatus
import com.ruchitech.cashentery.ui.screens.common_ui.MultiSelectType
import com.ruchitech.cashentery.ui.screens.common_ui.PaymentTypeSelection
import com.ruchitech.cashentery.ui.screens.common_ui.PaymentTypeSwitch
import com.ruchitech.cashentery.ui.screens.common_ui.ReceiptUI
import com.ruchitech.cashentery.ui.screens.common_ui.SampleDatePickerView
import com.ruchitech.cashentery.ui.screens.common_ui.SpacerHeight
import com.ruchitech.cashentery.ui.screens.common_ui.SpacerWidth
import com.ruchitech.cashentery.ui.screens.common_ui.TagSelectionChips
import com.ruchitech.cashentery.ui.screens.common_ui.TransactionAccountSwitch
import com.ruchitech.cashentery.ui.screens.common_ui.TransactionStatusSwitch
import com.ruchitech.cashentery.ui.screens.common_ui.VerticalMoneyRangeSlider
import com.ruchitech.cashentery.ui.theme.MainBackgroundSurface
import com.ruchitech.cashentery.ui.theme.TempColor
import com.ruchitech.cashentery.ui.theme.montserrat_medium
import com.ruchitech.cashentery.ui.theme.nonScaledSp

fun filterTransactions(
    transactions: Map<String?, List<Transaction>>?,
    status: Transaction.Status? = null,
    type: Transaction.Type? = null,
): Map<String?, List<Transaction>>? {
    return transactions?.mapValues { entry ->
        entry.value.filter { transaction ->
            (status == null || transaction.status == status) && (type == null || transaction.type == type)
        }
    }?.filterValues { it.isNotEmpty() }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionUi(viewModel: TransactionsViewModel, onBack: () -> Unit) {
    val transactions by viewModel.groupByTag.collectAsState()
    val transactionsByDate by viewModel.transactionsByDate.collectAsState()
    val trnxFilteredData by viewModel.filterTrnx.collectAsState()
    val tagsData by viewModel.categories.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var isFilteredData by rememberSaveable {
        mutableStateOf(false)
    }
    var printAndShare by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var dataToEdit by remember { mutableStateOf<Transaction?>(null) }
    val viewPagerState = rememberPagerState(initialPage = 0) { 5 }
    val tabs = listOf("All", "Cleared", "Pending", "Overdue", "Void")
    val selectedTabIndex = viewPagerState.currentPage
    val scope = rememberCoroutineScope()
    val result by viewModel.result.collectAsState()
    var selectedType by rememberSaveable { mutableStateOf<Transaction.Type?>(null) }

    // Memoize filtered transactions
    val filteredTransactions by remember(transactions, selectedTabIndex, selectedType) {
        derivedStateOf {
            when (tabs[selectedTabIndex]) {
                "Cleared" -> filterTransactions(
                    transactions, status = Transaction.Status.CLEARED, type = selectedType
                )

                "Pending" -> filterTransactions(
                    transactions, status = Transaction.Status.PENDING, type = selectedType
                )

                "Overdue" -> filterTransactions(
                    transactions, status = Transaction.Status.OVERDUE, type = selectedType
                )

                "Void" -> filterTransactions(
                    transactions, status = Transaction.Status.VOID, type = selectedType
                )

                else -> filterTransactions(
                    transactions, type = selectedType
                ) // Show all if no specific status
            }
        }
    }
//filters
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

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = result) {
        when (result) {
            Result.Error -> {}
            Result.Success -> {
                showDialog = false
                viewModel.resetState()
                //onSuccess()
            }

            Result.ResetState -> {

            }

            null -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackgroundSurface)
    ) {
        TransactionsAppBar(
            onBack = onBack,
            onFliterClick = {
                showBottomSheet = !showBottomSheet
            },
            isFilteredData
            )/*
                TabRow(modifier = Modifier,
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MainBackgroundSurface,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[viewPagerState.currentPage]),
                            color = Color.Black
                        )
                    }) {
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
                    state = viewPagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalAlignment = Alignment.Top
                ) { page ->
                }
            }
        */

        if (transactionsByDate?.transactionsByDate.isNullOrEmpty()) {
            EmptyTransactionUi()
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                transactionsByDate?.transactionsByDate?.forEach { dateGroup ->
                    item {
                        TransactionHeader(dateGroup.date, dateGroup.netBalance)
                    }
                    itemsIndexed(dateGroup.transactions) { _, item ->
                        TransactionItem(item, onClick = {
                            showDialog = true
                            dataToEdit = item
                        }, onLongClick = {
                            dataToEdit = item
                            //  showDeleteDialog = true
                            printAndShare = true
                        })
                        HorizontalDivider(thickness = 1.dp, color = Color(0xFFBBA76D))
                    }
                }
            }
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
                        .background(MainBackgroundSurface), contentAlignment = Alignment.Center
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
                                listOf("Account", "Date", "Status", "Type", "Amount", "Tag")

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
                                            selectedTypes = if (selectedTypes.contains(type)) {
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
                                tempData.date = FilterTrnx.Date(start = startDate, end = endDate)
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


    if (showDialog) {
        Dialog(
            onDismissRequest = {
                showDialog = false
            }, DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(), color = Color.White
            ) {
                EditTransactionScreen(viewModel = viewModel, dataToEdit, onBack = {
                    showDialog = false
                })
            }
        }
    }

    if (printAndShare) {
        dataToEdit?.let {
            ReceiptUI(transaction = it, onDismiss = {
                printAndShare = false
            }, onShareClick = { s, uri ->
            })
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(onConfirm = {
            viewModel.deleteTransactionDb(dataToEdit?._id ?: "")
            showDeleteDialog = false
        }) {
            showDeleteDialog = false
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTransactionScreen(
    viewModel: TransactionsViewModel,
    dataToEdit: Transaction?,
    onBack: () -> Unit,
) {
    val tags by viewModel.categories.collectAsState()
    var addNewTransaction by remember {
        mutableStateOf(
            Transaction()
        )
    }
    LaunchedEffect(key1 = true) {
        if (dataToEdit != null) {
            addNewTransaction = dataToEdit
        }
    }
    var showDeleteDialog by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackgroundSurface),
        contentAlignment = Alignment.Center
    ) {

        if (showDeleteDialog) {
            DeleteConfirmationDialog(onConfirm = {
                onBack()
                viewModel.deleteTransactionDb(dataToEdit?._id ?: "")
                showDeleteDialog = false
            }) {
                showDeleteDialog = false
            }
        }

        IconButton(
            onClick = {
                onBack()
            },
            modifier = Modifier
                .padding(16.dp)
                .background(TempColor, shape = CircleShape)
                .align(Alignment.BottomStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Update Transaction",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 0.dp),
                    fontFamily = montserrat_medium
                )
                Icon(imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color(0xFFD50D50),
                    modifier = Modifier
                        .size(35.dp)
                        .clickable {
                            showDeleteDialog = true
                        })
            }


            var newTransaction by remember {
                mutableStateOf(dataToEdit)
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MainBackgroundSurface)
            ) {

                IconButton(
                    onClick = {
                        onBack()
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .background(TempColor, shape = CircleShape)
                        .align(Alignment.BottomStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 0.dp)
                ) {
                    SpacerHeight(10)
                    Row(modifier = Modifier.height(50.dp)) {
                        AmountField(
                            modifier = Modifier.weight(1F),
                            transactionType = newTransaction?.type,
                            newTransaction?.amount.toString()
                        ) { value ->
                            newTransaction = newTransaction?.copy(amount = value)
                        }

                        SpacerWidth(10)
                        DateField(
                            modifier = Modifier.weight(1f), initialValue = newTransaction?.date
                        ) { date: String?, timeInMiles: Long? ->
                            newTransaction = newTransaction?.copy(
                                date = date, timeInMiles = timeInMiles
                            )
                        }
                    }
                    SpacerHeight(10)
                    TagTextField(
                        modifier = Modifier, initialValue = newTransaction?.tag, tags = tags
                    ) { newTag ->
                        newTransaction = newTransaction?.copy(tag = newTag)
                    }

                    SpacerHeight(10)
                    RemarksField(initialValue = newTransaction?.remarks) { newRemarks ->
                        newTransaction = newTransaction?.copy(remarks = newRemarks)
                    }

                    SpacerHeight(20)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1F),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            TransactionAccountSwitch(
                                modifier = Modifier,
                                initialType = newTransaction?.account ?: Transaction.Account.ONLINE
                            ) { type: Transaction.Account, _: String ->
                                newTransaction = newTransaction?.copy(account = type)
                            }
                        }
                        SpacerWidth(10)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1F),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            PaymentTypeSwitch(
                                modifier = Modifier,
                                initialType = newTransaction?.type ?: Transaction.Type.DEBIT
                            ) { type, _ ->
                                newTransaction = newTransaction?.copy(type = type)
                            }
                        }
                    }
                    SpacerHeight(20)
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        TransactionStatusSwitch(
                            modifier = Modifier,
                            initialType = newTransaction?.status ?: Transaction.Status.CLEARED
                        ) { type: Transaction.Status, _: String ->
                            newTransaction = newTransaction?.copy(status = type)
                        }
                    }
                    SpacerHeight(20)
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        SaveButton(newTransaction?.type) {
                            if (newTransaction?.amount?.isNaN() == false && !newTransaction?.tag.isNullOrEmpty()) {
                                viewModel.updateTransaction(newTransaction!!)
                            }
                        }
                    }
                }
            }
        }
        LoadingScreen(showLoading = viewModel.showLoading.value, "Updating transaction...")
    }
}




