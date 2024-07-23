package com.ruchitech.cashentery.ui.screens.add_transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.ruchitech.cashentery.R
import com.ruchitech.cashentery.helper.combineDateWithCurrentTime
import com.ruchitech.cashentery.ui.screens.common_ui.MyDatePickerDialog
import com.ruchitech.cashentery.ui.theme.Expense
import com.ruchitech.cashentery.ui.theme.Income
import com.ruchitech.cashentery.ui.theme.montserrat_semibold
import com.ruchitech.cashentery.ui.theme.nonScaledSp
import com.ruchitech.cashentery.ui.theme.sfMediumFont

@Composable
fun AmountField(
    modifier: Modifier,
    transactionType: Transaction.Type?,
    initialAmount: String = "",
    onValueChange: (value: Double?) -> Unit,
) {
    var amount by remember { mutableStateOf(initialAmount) }
    Row(
        modifier = modifier
            .background(Color.White, shape = RoundedCornerShape(5.dp))
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_rupay),
            contentDescription = null,
            modifier = Modifier
                .size(25.dp)
                .padding(2.dp),
            tint = if (transactionType == Transaction.Type.CREDIT) Income else Expense
        )
        TextField(
            value = amount,
            onValueChange = {
                if (it.toDoubleOrNull() != null) {
                    amount = it
                    onValueChange(it.toDoubleOrNull())
                } else {
                    amount = ""
                    onValueChange(null)
                }

            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {

            }),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .onFocusChanged {
                },
            textStyle = TextStyle(
                color = if (transactionType == Transaction.Type.CREDIT) Income else Expense,
                fontFamily = montserrat_semibold,
                fontSize = 16.sp.nonScaledSp
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
                    "00.0",
                    color = Color.LightGray,
                    fontFamily = montserrat_semibold,
                    fontSize = 16.sp.nonScaledSp
                )
            }
        )

    }
}


@Composable
fun DateField(
    modifier: Modifier,
    initialValue: String?,
    onValueChange: (date: String?, timeInMillis: Long?) -> Unit,
) {
    var isDatePickerVisible by remember { mutableStateOf(false) }
    var selectedDate by remember {
        mutableStateOf(initialValue)
    }
    Row(
        modifier = modifier
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
            value = selectedDate ?: "",
            onValueChange = {},
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
    if (isDatePickerVisible) {
        MyDatePickerDialog(onDismiss = {
            isDatePickerVisible = false
        }) { date: String?, utcTimeMillis: Long? ->

            onValueChange(
                date, combineDateWithCurrentTime(
                    utcTimeMillis ?: 0L
                ).time
            )
            selectedDate = date
            isDatePickerVisible = false
        }
    }
}


@Composable
fun TagTextField(
    modifier: Modifier,
    initialValue: String?,
    tags:List<String> = listOf(),
    onValueChange: (value: String?) -> Unit,
) {

    var tag by remember { mutableStateOf(TextFieldValue(text = initialValue?:"", selection = TextRange(initialValue?.length?:0))) }
    var expanded by remember { mutableStateOf(false) }
    val filteredItems = tags.filter { it.contains(tag.text, ignoreCase = true) }
    Column {
    TextField(
        value = tag,
        onValueChange = { textFieldValue ->
            //  tag = it
            // addNewTransaction = addNewTransaction.copy(tag = it)
            val newText = textFieldValue.text.trim()
            if (newText.isEmpty()) {
                tag = textFieldValue
                onValueChange(textFieldValue.text)
            } else {
                // Split the text by spaces
                val words = newText.split("\\s+".toRegex())

                // Ensure max two words and each word max length <= 20
                if (words.size <= 3 && words.all { it.length <= 20 }) {
                    val capitalizedText = words.joinToString(" ") { word ->
                        word.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() }
                    }
                    tag = textFieldValue
                    expanded = textFieldValue.text.isNotEmpty() && !filteredItems.contains(textFieldValue.text)
                    onValueChange(capitalizedText)
                }
                // If invalid, do nothing (keep the previous value)
            }
        },
        enabled = true,
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(2.dp))
            .onFocusChanged {
            },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
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
            properties = PopupProperties(focusable = false, clippingEnabled = true),
            offset = DpOffset(0. dp,5.dp),
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                .padding(vertical = 4.dp)
        ) {
            filteredItems.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        tag = TextFieldValue(
                            text = item,
                            selection = TextRange(item.length)
                        )
                        expanded = false
                    },
                    text = {
                        Text(
                            text = item,
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
}
}


@Composable
fun RemarksField(initialValue: String?, onValueChange: (value: String?) -> Unit) {
    var remarks by remember { mutableStateOf(initialValue) }
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
                onValueChange(it)
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

}

@Composable
fun SubmitButton(transactionType: Transaction.Type?, onClick: () -> Unit) {
    Button(
        onClick = {
            onClick()
        }, modifier = Modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (transactionType == Transaction.Type.CREDIT) Color(
                0xFF4CAF50
            ).copy(alpha = 0.8F) else Color(0xFFF44336).copy(alpha = 0.8F)
        )
    ) {
        Text(
            "SUBMIT",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontSize = 16.sp.nonScaledSp
        )
    }
}