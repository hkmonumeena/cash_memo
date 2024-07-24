package com.ruchitech.cashentery.ui.screens.add_transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.ruchitech.cashentery.MainActivity
import com.ruchitech.cashentery.helper.Result
import com.ruchitech.cashentery.helper.getInitialTransaction
import com.ruchitech.cashentery.ui.screens.common_ui.LoadingScreen
import com.ruchitech.cashentery.ui.screens.common_ui.PaymentTypeSwitch
import com.ruchitech.cashentery.ui.screens.common_ui.ReceiptUI
import com.ruchitech.cashentery.ui.screens.common_ui.SpacerHeight
import com.ruchitech.cashentery.ui.screens.common_ui.SpacerWidth
import com.ruchitech.cashentery.ui.screens.common_ui.TransactionAccountSwitch
import com.ruchitech.cashentery.ui.screens.common_ui.TransactionStatusSwitch
import com.ruchitech.cashentery.ui.theme.MainBackgroundSurface
import com.ruchitech.cashentery.ui.theme.TempColor
import com.ruchitech.cashentery.ui.theme.montserrat_medium


@Composable
fun AddTransactionUi(
    viewModel: AddTransactionViewModel,
    onSuccess: () -> Unit,
    onBack: () -> Unit,
) {
    val result by viewModel.result.collectAsState()
    LaunchedEffect(key1 = result) {
        when (result) {
            Result.Error -> {}
            Result.Success -> {
                onSuccess()
            }

            null -> {}
            Result.ResetState -> {}
        }
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AddTransactionScreen(viewModel, onBack)
        LoadingScreen(showLoading = viewModel.showLoading.value)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTransactionScreen(viewModel: AddTransactionViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    var newTransaction by remember {
        mutableStateOf(getInitialTransaction().copy(tag = (context as MainActivity).lastTagUsed))
    }
    val tags by viewModel.categories.collectAsState()
    var printAndShare by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackgroundSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Add Transaction",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 0.dp),
                    fontFamily = montserrat_medium
                )

                IconButton(onClick = {
                    printAndShare = true
                }) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null)
                }

            }

            SpacerHeight(16)
            Row(modifier = Modifier.height(50.dp)) {
                AmountField(
                    modifier = Modifier.weight(1F),
                    transactionType = newTransaction.type,
                    ""
                ) { value ->
                    newTransaction =
                        newTransaction.copy(amount = value)
                }

                SpacerWidth(10)
                DateField(
                    modifier = Modifier.weight(1f),
                    initialValue = newTransaction.date
                ) { date: String?, timeInMillis: Long? ->
                    newTransaction = newTransaction.copy(
                        date = date,
                        timeInMiles = timeInMillis
                    )
                }
            }
            SpacerHeight(10)
            TagTextField(modifier = Modifier, initialValue = newTransaction.tag, tags) { newTag ->
                newTransaction = newTransaction.copy(tag = newTag)
            }

            SpacerHeight(10)
            RemarksField(initialValue = newTransaction.remarks) { newRemarks ->
                newTransaction = newTransaction.copy(remarks = newRemarks)
            }

            SpacerHeight(20)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F), contentAlignment = Alignment.CenterStart
                ) {
                    TransactionAccountSwitch(
                        modifier = Modifier,
                        initialType = Transaction.Account.ONLINE
                    ) { type: Transaction.Account, _: String ->
                        newTransaction = newTransaction.copy(account = type)
                    }
                }
                SpacerWidth(10)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F), contentAlignment = Alignment.CenterEnd
                ) {
                    PaymentTypeSwitch(
                        modifier = Modifier,
                        initialType = Transaction.Type.DEBIT
                    ) { type, _ ->
                        newTransaction = newTransaction.copy(type = type)
                    }
                }
            }
            SpacerHeight(20)
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TransactionStatusSwitch(
                    modifier = Modifier,
                    initialType = Transaction.Status.CLEARED
                ) { type: Transaction.Status, _: String ->
                    newTransaction = newTransaction.copy(status = type)
                }
            }
            SpacerHeight(20)
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SubmitButton(newTransaction.type) {
                        if (newTransaction.amount?.isNaN() == false && !newTransaction.tag.isNullOrEmpty()) {
                            viewModel.addTrans(newTransaction)
                        }
                    }
                    SpacerHeight(16)
                    IconButton(
                        onClick = {
                            onBack()
                        }, modifier = Modifier
                            .padding(16.dp)
                            .background(TempColor, shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = null
                        )
                    }
                }
            }
        }
        if (printAndShare) {
            BasicAlertDialog(onDismissRequest = {
            }, modifier = Modifier.fillMaxSize(), properties = DialogProperties(usePlatformDefaultWidth = false)) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(MainBackgroundSurface)){
                    newTransaction?.let {
                        ReceiptUI(transaction = it, onDismiss = {
                            printAndShare = false
                        }, onShareClick = { s, uri ->
                        }
                        )
                    }

                }

            }
        }
    }
}