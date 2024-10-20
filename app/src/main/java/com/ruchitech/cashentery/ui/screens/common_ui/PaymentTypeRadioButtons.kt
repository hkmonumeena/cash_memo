package com.ruchitech.cashentery.ui.screens.common_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import com.ruchitech.cashentery.ui.theme.MainBackgroundSurface
import com.ruchitech.cashentery.ui.theme.nonScaledSp

@Composable
fun PaymentTypeSelection(
    options: List<Transaction.Account> = listOf(
        Transaction.Account.ONLINE,
        Transaction.Account.CASH
    ),
    selectedOptions: List<Transaction.Account> = listOf(),
    onSelectionChange: (List<Transaction.Account>) -> Unit,
) {
    val selectedItems = remember { mutableStateOf(selectedOptions.toMutableSet()) }

    Column {
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MainBackgroundSurface)
                    .clickable {
                        if (selectedItems.value.contains(option)) {
                            selectedItems.value.remove(option)
                        } else {
                            selectedItems.value.add(option)
                        }
                        onSelectionChange(selectedItems.value.toList())
                    }
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selectedItems.value.contains(option),
                    onCheckedChange = {
                        if (it) {
                            selectedItems.value.add(option)
                        } else {
                            selectedItems.value.remove(option)
                        }
                        onSelectionChange(selectedItems.value.toList())
                    }
                )
                Text(
                    text = option.name,
                    fontSize = 14.sp.nonScaledSp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

