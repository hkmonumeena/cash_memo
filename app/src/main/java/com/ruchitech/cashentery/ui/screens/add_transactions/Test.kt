package com.ruchitech.cashentery.ui.screens.add_transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.ui.theme.TempColor
import com.ruchitech.cashentery.ui.theme.TempColor2
import com.ruchitech.cashentery.ui.theme.TempColor3
import com.ruchitech.cashentery.ui.theme.montserrat_medium
import com.ruchitech.cashentery.ui.theme.montserrat_semibold
import com.ruchitech.cashentery.ui.theme.nonScaledSp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun AddTransactionUi(viewModel: AddTransactionViewModel) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AddTransactionScreen(viewModel)
    }
}

@Composable
private fun AddTransactionScreen(viewModel: AddTransactionViewModel) {
    var date by remember {
        mutableStateOf(
            SimpleDateFormat(
                "yyyy-MM-dd, HH:mm:ss", Locale.US
            ).format(Date())
        )
    }
    var paymentType by remember { mutableStateOf("Debit") }
    var amount by remember { mutableStateOf("") }
    val transactionId = UUID.randomUUID().toString()
    var transactionNumber by remember {
        mutableStateOf(transactionId.removeRange(15, transactionId.length))
    }
    var remarks by remember { mutableStateOf("") }
    var fromAccount by remember { mutableStateOf("Online") }
    var category by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    val amountFocus = remember {
        FocusRequester()
    }
    val remarksFocus = remember {
        FocusRequester()
    }

    var addNewTransaction by remember {
        mutableStateOf(
            AddTransaction(
                date = date,
                type = Type.DEBIT,
                account = Account.ONLINE,
                transactionNumber = transactionNumber
            )
        )
    }

    LaunchedEffect(Unit) {
        // Delay added to ensure the Composable is fully composed before requesting focus
        delay(300)
        amountFocus.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Add Transaction",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp),
            fontFamily = montserrat_medium
        )

        Row {
            // Date
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Date",
                    fontFamily = montserrat_semibold,
                    fontSize = 10.sp.nonScaledSp,
                    color = Color(0xFF858585)
                )
                BasicTextField(
                    value = date,
                    onValueChange = {
                        date = it
                        addNewTransaction = addNewTransaction.copy(date = it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, top = 5.dp)
                        .background(Color.Gray.copy(alpha = 0.1f), shape = RoundedCornerShape(5.dp))
                        .padding(8.dp),
                    textStyle = TextStyle(
                        color = Color(0xFF858585),
                        fontFamily = montserrat_semibold,
                        fontSize = 12.sp.nonScaledSp
                    )
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            var expanded by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    "Payment Type",
                    fontFamily = montserrat_semibold,
                    fontSize = 10.sp.nonScaledSp,
                    color = Color(0xFF858585)
                )
                BasicTextField(
                    value = paymentType,
                    enabled = false,
                    onValueChange = { paymentType = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, top = 5.dp)
                        .background(Color.Gray.copy(alpha = 0.1f), shape = RoundedCornerShape(5.dp))
                        .padding(8.dp),
                    textStyle = TextStyle(
                        color = Color(0xFF858585),
                        fontFamily = montserrat_semibold,
                        fontSize = 12.sp.nonScaledSp
                    )
                )
                DropdownMenu(
                    expanded = expanded, onDismissRequest = { expanded = false }, offset = DpOffset(
                        0.dp, (-12).dp
                    )
                ) {
                    DropdownMenuItem(text = {
                        Text(
                            text = "Credit",
                            fontFamily = montserrat_semibold,
                            fontSize = 11.sp.nonScaledSp
                        )
                    }, onClick = {
                        paymentType = "Credit"
                        expanded = false
                    })
                    DropdownMenuItem(text = {
                        Text(
                            text = "Debit",
                            fontFamily = montserrat_semibold,
                            fontSize = 11.sp.nonScaledSp
                        )
                    }, onClick = {
                        paymentType = "Debit"
                        expanded = false
                    })
                }
            }
        }
        Row {
            var expanded by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier
                    .weight(1f)
                /* .clickable(
                     interactionSource = remember { MutableInteractionSource() },
                     indication = null
                 ) {
                     expanded = true
                 }*/
            ) {
                Text(
                    "Account",
                    fontFamily = montserrat_semibold,
                    fontSize = 10.sp.nonScaledSp,
                    color = Color(0xFF858585)
                )
                BasicTextField(
                    value = fromAccount,
                    onValueChange = { fromAccount = it },
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, top = 5.dp)
                        .background(Color.Gray.copy(alpha = 0.1f), shape = RoundedCornerShape(5.dp))
                        .padding(8.dp),
                    textStyle = TextStyle(
                        color = Color(0xFF858585),
                        fontFamily = montserrat_semibold,
                        fontSize = 12.sp.nonScaledSp
                    )
                )
                DropdownMenu(
                    expanded = expanded, onDismissRequest = { expanded = false }, offset = DpOffset(
                        0.dp, (-12).dp
                    )
                ) {
                    DropdownMenuItem(text = {
                        Text(
                            text = "HDFC BANK",
                            fontFamily = montserrat_semibold,
                            fontSize = 11.sp.nonScaledSp
                        )
                    }, onClick = {
                        fromAccount = "HDFC BANK"
                        expanded = false
                    })
                    DropdownMenuItem(text = {
                        Text(
                            text = "CASH",
                            fontFamily = montserrat_semibold,
                            fontSize = 11.sp.nonScaledSp
                        )
                    }, onClick = {
                        fromAccount = "CASH"
                        expanded = false
                    })
                }


            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Transaction Number",
                    fontFamily = montserrat_semibold,
                    fontSize = 10.sp.nonScaledSp,
                    color = Color(0xFF858585)
                )
                BasicTextField(
                    value = transactionNumber,
                    onValueChange = {
                        transactionNumber = it
                        addNewTransaction = addNewTransaction.copy(transactionNumber = it)
                    },
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, top = 5.dp)
                        .background(Color.Gray.copy(alpha = 0.1f), shape = RoundedCornerShape(5.dp))
                        .padding(8.dp),
                    textStyle = TextStyle(
                        color = Color(0xFF858585),
                        fontFamily = montserrat_semibold,
                        fontSize = 12.sp.nonScaledSp
                    )
                )
            }


        }

        // Amount
        Text(
            "Amount",
            fontFamily = montserrat_semibold,
            fontSize = 10.sp.nonScaledSp,
            color = Color(0xFF858585)
        )
        BasicTextField(
            value = amount,
            onValueChange = {
                amount = it
                addNewTransaction = addNewTransaction.copy(amount = it.toDoubleOrNull())
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                remarksFocus.requestFocus()
            }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 5.dp)
                .background(Color.Gray.copy(alpha = 0.1f), shape = RoundedCornerShape(5.dp))
                .padding(8.dp)
                .focusRequester(amountFocus)
                .onFocusChanged { },
            textStyle = TextStyle(
                color = Color(0xFF323232),
                fontFamily = montserrat_semibold,
                fontSize = 12.sp.nonScaledSp
            )
        )

        // Remarks
        Text(
            "Remarks",
            fontFamily = montserrat_semibold,
            fontSize = 10.sp.nonScaledSp,
            color = Color(0xFF858585)
        )
        BasicTextField(
            value = remarks,
            onValueChange = {
                remarks = it
                addNewTransaction = addNewTransaction.copy(remarks = it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 5.dp)
                .background(Color.Gray.copy(alpha = 0.1f), shape = RoundedCornerShape(5.dp))
                .padding(8.dp)
                .focusRequester(remarksFocus)
                .onFocusChanged {

                },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {

            }),
            textStyle = TextStyle(
                color = Color(0xFF323232),
                fontFamily = montserrat_semibold,
                fontSize = 12.sp.nonScaledSp
            )
        )

        // From Account


        var mainCategory by remember { mutableStateOf(false) }
        /*       Text(
                   "Category",
                   fontFamily = montserrat_semibold,
                   fontSize = 10.sp.nonScaledSp,
                   color = Color(0xFF858585)
               )

               Column {
                   BasicTextField(
                       value = category,
                       onValueChange = {
                           category = it
                           //addNewTransaction = addNewTransaction.copy(mainCategory = it)
                       },
                       enabled = false,
                       modifier = Modifier
                           .fillMaxWidth()
                           .padding(bottom = 16.dp, top = 5.dp)
                           .background(Color.Gray.copy(alpha = 0.1f), shape = RoundedCornerShape(5.dp))
                           .padding(8.dp)
                           .clickable(
                               interactionSource = remember { MutableInteractionSource() },
                               indication = null
                           ) {
                               mainCategory = true

                           },
                       textStyle = TextStyle(
                           color = Color(0xFF323232),
                           fontFamily = montserrat_semibold,
                           fontSize = 12.sp.nonScaledSp
                       )
                   )

                   DropdownMenu(
                       expanded = mainCategory,
                       onDismissRequest = { mainCategory = false },
                       offset = DpOffset(
                           0.dp, (-12).dp
                       )
                   ) {
                       DropdownMenuItem(text = {
                           Text(
                               text = "Groceries",
                               fontFamily = montserrat_semibold,
                               fontSize = 11.sp.nonScaledSp
                           )
                       }, onClick = {
                           category = "Groceries"
                           mainCategory = false
                       })
                       DropdownMenuItem(text = {
                           Text(
                               text = "Fuel",
                               fontFamily = montserrat_semibold,
                               fontSize = 11.sp.nonScaledSp
                           )
                       }, onClick = {
                           category = "Fuel"
                           mainCategory = false
                       })
                       DropdownMenuItem(text = {
                           Text(
                               text = "Bills",
                               fontFamily = montserrat_semibold,
                               fontSize = 11.sp.nonScaledSp
                           )
                       }, onClick = {
                           category = "Bills"
                           mainCategory = false
                       })
                   }
               }*/


        // Sub Category
        Text(
            "Tag",
            fontFamily = montserrat_semibold,
            fontSize = 10.sp.nonScaledSp,
            color = Color(0xFF858585)
        )
        BasicTextField(
            value = tag,
            onValueChange = {
                tag = it
                addNewTransaction = addNewTransaction.copy(tag = it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 5.dp)
                .background(Color.Gray.copy(alpha = 0.1f), shape = RoundedCornerShape(5.dp))
                .padding(8.dp),
            textStyle = TextStyle(
                color = Color(0xFF323232),
                fontFamily = montserrat_semibold,
                fontSize = 12.sp.nonScaledSp
            )
        )
        var isBSelected by remember {
            mutableStateOf(true)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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
                                fromAccount = "ONLINE"
                                addNewTransaction = addNewTransaction.copy(account = Account.ONLINE)
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
                                fromAccount = "CASH"
                                addNewTransaction = addNewTransaction.copy(account = Account.CASH)
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
            var isASelcted by remember { mutableStateOf(false) }
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
                                if (isASelcted) TempColor3 else Color.LightGray,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                isASelcted = true
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
                                if (!isASelcted) TempColor2 else Color.LightGray,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                isASelcted = false
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
                    viewModel.addTrans(addNewTransaction)
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = RoundedCornerShape(5.dp)
        ) {
            Text("Submit")
        }
    }
}