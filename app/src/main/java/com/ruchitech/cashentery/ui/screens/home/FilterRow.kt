package com.ruchitech.cashentery.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.R
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import com.ruchitech.cashentery.ui.screens.common_ui.SpacerWidth
import com.ruchitech.cashentery.ui.theme.nonScaledSp


@Composable
fun FilterRow(
    transactionFilters: Map<String?, List<Transaction>>?,
    selectedStatus: Transaction.Status?,
    onStatusSelected: (Transaction.Status?) -> Unit,
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Tags (${transactionFilters?.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp.nonScaledSp
        )

        Box(modifier = Modifier.clickable { isDropdownExpanded = true }) {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = selectedStatus?.name ?: "All", // Display the selected filter
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
            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                // Menu item for each transaction status
                Transaction.Status.values().forEach { status ->
                    DropdownMenuItem(
                        onClick = {
                            onStatusSelected(status)
                            isDropdownExpanded = false
                        },
                        text = {
                            Text(text = status.name)
                        }
                    )
                }
                DropdownMenuItem(
                    onClick = {
                        onStatusSelected(null) // Reset filter to show all
                        isDropdownExpanded = false
                    },
                    text = { Text(text = "All") }
                )
            }
        }
    }
}
