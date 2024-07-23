package com.ruchitech.cashentery.ui.screens.chatview

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ruchitech.cashentery.MainActivity
import com.ruchitech.cashentery.helper.Result
import com.ruchitech.cashentery.ui.screens.add_transactions.Account
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import com.ruchitech.cashentery.ui.screens.add_transactions.AmountField
import com.ruchitech.cashentery.ui.screens.add_transactions.DateField
import com.ruchitech.cashentery.ui.screens.add_transactions.RemarksField
import com.ruchitech.cashentery.ui.screens.add_transactions.Status
import com.ruchitech.cashentery.ui.screens.add_transactions.SubmitButton
import com.ruchitech.cashentery.ui.screens.add_transactions.TagTextField
import com.ruchitech.cashentery.ui.screens.add_transactions.Type
import com.ruchitech.cashentery.ui.screens.common_ui.DeleteConfirmationDialog
import com.ruchitech.cashentery.ui.screens.common_ui.EmptyTransactionUi
import com.ruchitech.cashentery.ui.screens.common_ui.LoadingScreen
import com.ruchitech.cashentery.ui.screens.common_ui.PaymentTypeSwitch
import com.ruchitech.cashentery.ui.screens.common_ui.SpacerHeight
import com.ruchitech.cashentery.ui.screens.common_ui.SpacerWidth
import com.ruchitech.cashentery.ui.screens.common_ui.TransactionAccountSwitch
import com.ruchitech.cashentery.ui.screens.common_ui.TransactionStatusSwitch
import com.ruchitech.cashentery.ui.screens.home.formatMillisToDate
import com.ruchitech.cashentery.ui.screens.home.formatToINR
import com.ruchitech.cashentery.ui.theme.Expense
import com.ruchitech.cashentery.ui.theme.Income
import com.ruchitech.cashentery.ui.theme.MainBackgroundSurface
import com.ruchitech.cashentery.ui.theme.TempColor
import com.ruchitech.cashentery.ui.theme.montserrat_medium
import com.ruchitech.cashentery.ui.theme.nonScaledSp
import com.ruchitech.cashentery.ui.theme.sfMediumFont
import com.ruchitech.cashentery.ui.theme.sfSemibold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale


@Composable
fun TransactionDetailsUi(
    viewModel: TransactionDetailsViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
) {

    val data by viewModel.transactionsFlow.collectAsState()
    val context = LocalContext.current
    (context as MainActivity).lastTagUsed = data.firstOrNull()?.tag
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dataToEdit by remember { mutableStateOf<Transaction?>(null) }
    val result by viewModel.result.collectAsState()
    val viewPagerState = rememberPagerState(initialPage = 0) { 5 }
    val tabs = listOf("All", "Cleared", "Pending", "Overdue", "Void")
    val selectedTabIndex = viewPagerState.currentPage
    val scope = rememberCoroutineScope()
    // Memoize filtered transactions
    val filteredTransactions by remember(data, selectedTabIndex) {
        derivedStateOf {
            when (tabs[selectedTabIndex]) {
                "Cleared" -> data.filter { it.status == Status.CLEARED }
                "Pending" -> data.filter { it.status == Status.PENDING }
                "Overdue" -> data.filter { it.status == Status.OVERDUE }
                "Void" -> data.filter { it.status == Status.VOID }
                else -> data // All transactions
            }
        }
    }


    LaunchedEffect(key1 = result) {
        when (result) {
            Result.Error -> {}
            Result.Success -> {
                showDialog = false
                viewModel.resetState()
                if (data.isEmpty()) {
                    onBack()
                }
                //onSuccess()
            }

            Result.ResetState -> {

            }

            null -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackgroundSurface)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MainBackgroundSurface)
                    .size(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onBack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(10.dp))
                data.firstOrNull()?.tag?.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }?.let {
                    Text(
                        text = it,
                        fontFamily = sfMediumFont,
                        fontSize = 16.sp.nonScaledSp,
                        color = Color.Black
                    )
                }
            }
            Divider()
            HorizontalPager(
                state = viewPagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(bottom = 146.dp),
                verticalAlignment = Alignment.Top
            ) { page ->
                if (filteredTransactions.isEmpty()) {
                    EmptyTransactionUi()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(filteredTransactions) { index, item ->
                            ChatBox(transaction = item, onClick = {
                                showDialog = true
                                dataToEdit = item
                            }, onLongClick = {
                                dataToEdit = item
                                showDeleteDialog = true
                            })
                        }
                        item {
                            SpacerHeight(25)
                        }
                    }
                }
            }




            Divider()
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            shape = RectangleShape,
            colors = CardDefaults.cardColors(containerColor = Color(0xFFDACB9F))
        ) {
            Divider()
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

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                StatusSummary(
                    title = "",
                    data = data.filter { it.type == Type.CREDIT },
                    statuses = listOf(
                        Status.PENDING to "a",
                        Status.PENDING to "Pending",
                        Status.OVERDUE to "Overdue",
                        Status.CLEARED to "Cleared",
                        Status.VOID to "Void"
                    ),
                    type = 0
                )
                Spacer(modifier = Modifier.width(5.dp))
                StatusSummary(
                    title = "",
                    data = data.filter { it.type == Type.DEBIT },
                    statuses = listOf(
                        Status.PENDING to "a",
                        Status.PENDING to "Pending",
                        Status.OVERDUE to "Overdue",
                        Status.CLEARED to "Cleared",
                        Status.VOID to "Void",
                    ),
                    type = 1
                )
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

        if (showDeleteDialog) {
            DeleteConfirmationDialog(onConfirm = {
                viewModel.deleteTransactionDb(dataToEdit?.id ?: "", dataToEdit)
                showDeleteDialog = false
            }) {
                showDeleteDialog = false
            }
        }

    }
}

@Composable
fun StatusSummary(
    title: String,
    data: List<Transaction>,
    statuses: List<Pair<Status, String>>,
    type: Int,
) {
    Column {
        SpacerHeight(8)
        statuses.forEach { (status, label) ->
            val total = data.filter { it.status == status }.sumOf { it.amount ?: 0.0 }
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
            ) {
                if (type == 0) {
                    Text(
                        modifier = Modifier.width(75.dp),
                        text = if (label == "a") "STATUS" else label,
                        fontSize = 13.sp.nonScaledSp,
                        fontFamily = sfMediumFont,
                        fontWeight = if (label == "a") FontWeight.Bold else FontWeight.Medium
                    )
                }
                Text(
                    text = if (label == "a" && type == 0) "INCOME" else if (label == "a" && type == 1) "EXPENSE" else formatToINR(
                        total
                    ),
                    fontSize = if (label == "a" && type == 0) 14.sp.nonScaledSp else 13.sp.nonScaledSp,
                    fontFamily = sfMediumFont,
                    fontWeight = if (label == "a") FontWeight.Bold else FontWeight.Medium
                )
            }
        }
        SpacerHeight(10)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChatBox(transaction: Transaction, onClick: () -> Unit, onLongClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .defaultMinSize(minWidth = 200.dp)
                .fillMaxWidth(0.7F)
                .align(if (transaction.type == Type.CREDIT) Alignment.CenterStart else Alignment.CenterEnd)
                .combinedClickable(onLongClick = {
                    onLongClick()
                }, onClick = {
                    onClick()
                })
                .background(Color(0xFFDACB9F), shape = RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = (formatToINR(transaction.amount ?: 0.0)),
                fontFamily = sfSemibold,
                fontSize = 16.sp.nonScaledSp,
                color = if (transaction.type == Type.CREDIT) Income else Expense,
                modifier = Modifier.height(20.dp)
            )
            SpacerHeight(5)

            Text(
                text = transaction.remarks ?: "",
                fontFamily = sfMediumFont,
                fontSize = 12.sp.nonScaledSp.nonScaledSp,
                lineHeight = 14.sp.nonScaledSp,
                modifier = Modifier.padding(top = 0.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val paymentStr = when (transaction.type) {
                    Type.CREDIT -> {
                        when (transaction.status) {
                            Status.PENDING -> "Pending"
                            Status.CLEARED -> "Received on"
                            Status.OVERDUE -> "Overdue"
                            Status.VOID -> "Void"
                            null -> "Void"
                        }
                    }

                    Type.DEBIT -> {
                        when (transaction.status) {
                            Status.PENDING -> "Pending"
                            Status.CLEARED -> "Paid on"
                            Status.OVERDUE -> "Overdue"
                            Status.VOID -> "Void"
                            null -> "Void"
                        }
                    }

                    null -> "Void"
                }
                Text(
                    text = "$paymentStr: ${formatMillisToDate(transaction.timeInMiles ?: 0)}",
                    fontSize = 11.sp.nonScaledSp,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Gray
                )
                var statusColor = Color(0xFF4CAF50)
                val statusStr =
                    when (transaction.status) {
                        Status.PENDING -> {
                            statusColor = Color(0xFFFF9800)
                            "Pending"
                        }

                        Status.CLEARED -> {
                            statusColor = Color(0xFF4CAF50)
                            "Cleared"
                        }

                        Status.OVERDUE -> {
                            statusColor = Color(0xFFF44336)
                            "Overdue"
                        }

                        Status.VOID -> {
                            statusColor = Color(0xFF9E9E9E)
                            "Void"
                        }

                        null -> {
                            statusColor = Color(0xFF4CAF50)
                            "Cleared"
                        }
                    }

                Text(
                    text = statusStr,
                    fontSize = 11.sp.nonScaledSp,
                    fontFamily = sfSemibold,
                    color = statusColor,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTransactionScreen(
    viewModel: TransactionDetailsViewModel,
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

    LaunchedEffect(Unit) {
        // Delay added to ensure the Composable is fully composed before requesting focus
        delay(300)
        //   amountFocus.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackgroundSurface),
        contentAlignment = Alignment.Center
    ) {

        if (showDeleteDialog) {
            DeleteConfirmationDialog(onConfirm = {
                onBack()
                viewModel.deleteTransactionDb(dataToEdit?.id ?: "", dataToEdit)
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
            Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = null)
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
                    Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = null)
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
                        modifier = Modifier,
                        initialValue = newTransaction?.tag,
                        tags = tags
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
                                initialType = newTransaction?.account ?: Account.ONLINE
                            ) { type: Account, _: String ->
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
                                initialType = newTransaction?.type ?: Type.DEBIT
                            ) { type, _ ->
                                newTransaction = newTransaction?.copy(type = type)
                            }
                        }
                    }
                    SpacerHeight(20)
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        TransactionStatusSwitch(
                            modifier = Modifier,
                            initialType = newTransaction?.status ?: Status.CLEARED
                        ) { type: Status, _: String ->
                            newTransaction = newTransaction?.copy(status = type)
                        }
                    }
                    SpacerHeight(20)
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        SubmitButton(newTransaction?.type) {
                            if (newTransaction?.amount?.isNaN() == false && !newTransaction?.tag.isNullOrEmpty()) {
                                viewModel.updateTransaction(newTransaction!!)
                            }
                        }
                    }
                }
            }
        }

        LoadingScreen(showLoading = viewModel.showLoading.value,"Updating transaction...")

    }


}
