package com.ruchitech.cashentery.ui.screens.chatview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ruchitech.cashentery.R
import com.ruchitech.cashentery.helper.Result
import com.ruchitech.cashentery.ui.screens.add_transactions.Account
import com.ruchitech.cashentery.ui.screens.add_transactions.AddTransactionData
import com.ruchitech.cashentery.ui.screens.add_transactions.Type
import com.ruchitech.cashentery.ui.screens.add_transactions.combineDateWithCurrentTime
import com.ruchitech.cashentery.ui.screens.add_transactions.formatMilliSecondsToDateTime
import com.ruchitech.cashentery.ui.screens.home.formatMillisToDate
import com.ruchitech.cashentery.ui.screens.home.formatToINR
import com.ruchitech.cashentery.ui.theme.Expense
import com.ruchitech.cashentery.ui.theme.Income
import com.ruchitech.cashentery.ui.theme.TempColor
import com.ruchitech.cashentery.ui.theme.TempColor2
import com.ruchitech.cashentery.ui.theme.TempColor3
import com.ruchitech.cashentery.ui.theme.montserrat_medium
import com.ruchitech.cashentery.ui.theme.montserrat_semibold
import com.ruchitech.cashentery.ui.theme.nonScaledSp
import com.ruchitech.cashentery.ui.theme.sfMediumFont
import com.ruchitech.cashentery.ui.theme.sfSemibold
import kotlinx.coroutines.delay
import java.util.Date
import java.util.Locale


@Composable
fun TransactionDetailsUi(
    viewModel: TransactionDetailsViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
) {
    val data by viewModel.transactionsFlow.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var dataToEdit by remember { mutableIntStateOf(0) }
    val result by viewModel.result.collectAsState()
    LaunchedEffect(key1 = result) {
        when (result) {
            Result.Error -> {}
            Result.Success -> {
                showDialog = false
                //onSuccess()
            }

            null -> {}
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFF0))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .size(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onBack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(10.dp))
                data.first().tag?.replaceFirstChar {
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
            LazyColumn {
                itemsIndexed(data) { index, item ->
                    ChatBox(transaction = item, onClick = {
                        showDialog = true
                        dataToEdit = index
                    })
                }
            }
        }

        if (showDialog) {
            Dialog(
                onDismissRequest = {
                    showDialog = false
                },
                DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    color = Color.White
                ) {
                    AddTransactionScreen(viewModel = viewModel, dataToEdit, onBack = {
                        showDialog = false
                    })
                }
            }
        }

    }
}

@Composable
private fun ChatBox(transaction: AddTransactionData, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .defaultMinSize(minWidth = 200.dp)
                .background(Color.White, shape = RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .align(if (transaction.type == Type.CREDIT) Alignment.CenterStart else Alignment.CenterEnd),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = formatToINR(transaction.amount?:0.0),
                fontFamily = sfSemibold,
                fontSize = 14.sp.nonScaledSp,
                color = if (transaction.type == Type.CREDIT) Income else Expense,
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = transaction.remarks ?: "",
                fontFamily = sfMediumFont,
                fontSize = 9.sp.nonScaledSp.nonScaledSp,
                modifier = Modifier.padding(top = 0.dp)
            )
            Text(
                text = "${if (transaction.type == Type.CREDIT) "Received on" else "Paid on"}: ${
                    formatMillisToDate(
                        transaction.timeInMiles ?: 0
                    )
                }",
                fontSize = 9.sp.nonScaledSp,
                fontFamily = FontFamily.SansSerif,
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTransactionScreen(
    viewModel: TransactionDetailsViewModel,
    dataToEdit: Int,
    onBack: () -> Unit,
) {
    val allTags by viewModel.categories.collectAsState()
    val data by viewModel.transactionsFlow.collectAsState()
    val currentDateTime by remember {
        mutableStateOf(Date().time)
    }
    var date by remember {
        mutableStateOf(data.get(dataToEdit).date)
    }
    var paymentType by remember { mutableStateOf(if (data.get(dataToEdit).type == Type.CREDIT) "Credit" else "Debit") }
    var amount by remember { mutableStateOf(data.get(dataToEdit).amount.toString()) }
    val transactionId = data.get(dataToEdit).transactionNumber
    var transactionNumber by remember {
        mutableStateOf(transactionId?.removeRange(15, transactionId.length))
    }
    var remarks by remember { mutableStateOf(data.get(dataToEdit).remarks) }
    var fromAccount by remember { mutableStateOf(if (data.get(dataToEdit).account == Account.ONLINE) "Online" else "Cash") }
    var category by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf(TextFieldValue("")) }
    LaunchedEffect(key1 = true) {
        tag = TextFieldValue(data[dataToEdit].tag ?: "")
    }
    val dateState = rememberDatePickerState()
    val amountFocus = remember {
        FocusRequester()
    }
    val remarksFocus = remember {
        FocusRequester()
    }

    val tagsFocusRequester = remember {
        FocusRequester()
    }

    var expanded by remember { mutableStateOf(false) }

    var isDatePickerVisible by remember { mutableStateOf(false) }
    var paymentType2 by remember { mutableStateOf(false) }

    var addNewTransaction by remember {
        mutableStateOf(
            AddTransactionData(
                id = data.get(dataToEdit).id,
                date = data.get(dataToEdit).date,
                type = data.get(dataToEdit).type,
                account = data.get(dataToEdit).account,
                transactionNumber = data.get(dataToEdit).transactionNumber,
                timeInMiles = data.get(dataToEdit).timeInMiles,
            )
        )
    }
    val suggestions = remember(tag) {
        if (tag.text.isEmpty()) {
            emptyList()
        } else {
            allTags?.filter { it?.contains(tag.text, ignoreCase = true) == true }
        }
    }
    LaunchedEffect(Unit) {
        // Delay added to ensure the Composable is fully composed before requesting focus
        delay(300)
        //   amountFocus.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFF0))
    ) {

        IconButton(
            onClick = {
                onBack()
            }, modifier = Modifier
                .padding(16.dp)
                .background(TempColor, shape = CircleShape)
                .align(Alignment.BottomStart)
        ) {
            Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = null)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text(
                "Update Transaction",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp),
                fontFamily = montserrat_medium
            )

            Row(modifier = Modifier.height(45.dp)) {
                Column(modifier = Modifier.weight(1F)) {
                    Row(
                        modifier = Modifier
                            .background(Color.White, shape = RoundedCornerShape(5.dp))
                            .height(45.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_rupay),
                            contentDescription = null,
                            modifier = Modifier
                                .size(25.dp)
                                .padding(2.dp),
                            tint = if (paymentType2) Income else Expense
                        )
                        BasicTextField(
                            value = amount,
                            onValueChange = {
                                if (it.toDoubleOrNull() != null) {
                                    amount = it
                                    addNewTransaction =
                                        addNewTransaction.copy(amount = it.toDoubleOrNull())
                                } else {
                                    amount = ""
                                }

                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                tagsFocusRequester.requestFocus()
                            }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp)
                                .focusRequester(amountFocus)
                                .onFocusChanged {
                                },
                            textStyle = TextStyle(
                                color = if (paymentType2) Income else Expense,
                                fontFamily = montserrat_semibold,
                                fontSize = 16.sp.nonScaledSp
                            )
                        )

                    }

                }
                Spacer(modifier = Modifier.width(10.dp))


                // Date
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(Color.White, shape = RoundedCornerShape(5.dp))
                        .clickable {
                            isDatePickerVisible = true
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = null,
                        modifier = Modifier
                            .size(25.dp)
                            .padding(2.dp),
                    )
                    BasicTextField(
                        value = date ?: "",
                        onValueChange = {
                            date = it
                            addNewTransaction = addNewTransaction.copy(date = it)
                        },
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth(),
                        textStyle = TextStyle(
                            color = Color(0xFF323232),
                            fontFamily = montserrat_semibold,
                            fontSize = 12.sp.nonScaledSp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                value = tag,
                onValueChange = {
                    //  tag = it
                    // addNewTransaction = addNewTransaction.copy(tag = it)

                    val newText = it.text.trim()
                    if (newText.isEmpty()) {
                        tag = it
                        addNewTransaction = addNewTransaction.copy(tag = it.text)
                    } else {
                        // Split the text by spaces
                        val words = newText.split("\\s+".toRegex())

                        // Ensure max two words and each word max length <= 20
                        if (words.size <= 2 && words.all { it.length <= 20 }) {
                            val capitalizedText = words.joinToString(" ") { word ->
                                word.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() }
                            }

                            tag = it
                            addNewTransaction = addNewTransaction.copy(tag = capitalizedText)
                        }
                        // If invalid, do nothing (keep the previous value)
                    }
                },
                enabled = true,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(2.dp))
                    .focusRequester(tagsFocusRequester)
                    .onFocusChanged {
                    },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    remarksFocus.requestFocus()
                }),
                textStyle = TextStyle(
                    color = Color(0xFF323232),
                    fontFamily = sfMediumFont,
                    fontSize = 14.sp.nonScaledSp
                ),
                colors = TextFieldDefaults.colors(
                    disabledContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                placeholder = {
                    Text(
                        "Add Tag",
                        fontFamily = montserrat_semibold,
                        fontSize = 10.sp.nonScaledSp,
                        color = Color(0xFF858585)
                    )
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                suggestions?.forEach { suggestion ->
                    DropdownMenuItem(onClick = {
                        tag = TextFieldValue(
                            text = suggestion ?: "",
                            selection = TextRange(suggestion?.length ?: 0)
                        )
                        expanded = false
                    }, text = {
                        Text(text = suggestion ?: "")
                    })
                }
            }

            LazyColumn {
                items(suggestions ?: listOf()) { suggestion ->
                    Text(
                        text = suggestion ?: "",
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                tag = TextFieldValue(
                                    text = suggestion ?: "",
                                    selection = TextRange(suggestion?.length ?: 0)
                                )
                            },
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    )
                }
            }


            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.White)
                    .padding(horizontal = 0.dp)
            ) {
                TextField(
                    value = remarks ?: "",
                    onValueChange = {
                        remarks = it
                        addNewTransaction = addNewTransaction.copy(remarks = it)
                    },
                    enabled = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        //.focusRequester(remarksFocus)
                        .onFocusChanged {
                        },
                    textStyle = TextStyle(
                        color = Color(0xFF323232),
                        fontFamily = sfMediumFont,
                        fontSize = 14.sp.nonScaledSp,
                    ),
                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    placeholder = {
                        Text(
                            "Type your description here",
                            fontFamily = montserrat_semibold,
                            fontSize = 10.sp.nonScaledSp,
                            color = Color(0xFF858585)
                        )
                    }
                )

            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sub Category
            var isBSelected by remember {
                mutableStateOf(true)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F), contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.background(
                            Color.LightGray,
                            shape = RoundedCornerShape(10.dp)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isBSelected) TempColor else Color.LightGray,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    isBSelected = true
                                    fromAccount = Account.ONLINE.toString()
                                    addNewTransaction =
                                        addNewTransaction.copy(account = Account.ONLINE)
                                }) {
                            Text(
                                text = "ONLINE",
                                fontSize = 14.sp.nonScaledSp,
                                fontFamily = montserrat_semibold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    if (!isBSelected) TempColor else Color.LightGray,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    isBSelected = false
                                    fromAccount = Account.CASH.toString()
                                    addNewTransaction =
                                        addNewTransaction.copy(account = Account.CASH)
                                }) {
                            Text(
                                text = " CASH ",
                                fontSize = 14.sp.nonScaledSp,
                                modifier = Modifier.padding(10.dp),
                                fontFamily = montserrat_semibold,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F), contentAlignment = Alignment.CenterEnd
                ) {
                    Row(
                        modifier = Modifier.background(
                            Color.LightGray,
                            shape = RoundedCornerShape(10.dp)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    if (paymentType2) TempColor3 else Color.LightGray,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    paymentType2 = true
                                    paymentType = "Credit"
                                    addNewTransaction = addNewTransaction.copy(type = Type.CREDIT)
                                }) {
                            Text(
                                text = " Credit ".uppercase(Locale.getDefault()),
                                fontSize = 14.sp.nonScaledSp,
                                modifier = Modifier.padding(10.dp),
                                fontFamily = montserrat_semibold,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    if (!paymentType2) TempColor2 else Color.LightGray,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    paymentType2 = false
                                    paymentType = "Debit"
                                    addNewTransaction = addNewTransaction.copy(type = Type.DEBIT)
                                }) {
                            Text(
                                text = "  Debit ".uppercase(Locale.ROOT),
                                fontSize = 14.sp.nonScaledSp,
                                fontFamily = montserrat_semibold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }

            // Submit Button
            Button(
                onClick = {
                    if (amount.isNotEmpty()) {
                        viewModel.updateTransaction(addNewTransaction)
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text("Update")
            }
        }
        if (isDatePickerVisible) {
            DatePickerDialog(
                onDismissRequest = {
                    isDatePickerVisible = false
                },
                confirmButton = {
                    Button(
                        onClick = {
                            isDatePickerVisible = false
                            date = dateState.selectedDateMillis?.let {
                                formatMilliSecondsToDateTime(combineDateWithCurrentTime(it).time)
                            }
                            addNewTransaction = addNewTransaction.copy(
                                date = date,
                                timeInMiles = combineDateWithCurrentTime(
                                    dateState.selectedDateMillis ?: 0L
                                ).time
                            )
                        }
                    ) {
                        Text(text = "OK")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { isDatePickerVisible = false }
                    ) {
                        Text(text = "Cancel")
                    }
                }
            ) {
                DatePicker(
                    state = dateState,
                    showModeToggle = true
                )
            }
        }
    }

}
