package com.ruchitech.cashentery.ui.screens.transactions

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.ruchitech.cashentery.ui.theme.sfMediumFont

@Composable
fun TransactionsAppBar(
    onBack: () -> Unit,
    onTypeSelected: (Transaction.Type?) -> Unit,
    selectedType: Transaction.Type?
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(start = 10.dp, end = 16.dp),
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

        Box(modifier = Modifier.clickable { isDropdownExpanded = true }) {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = selectedType?.name ?: "All", // Display the selected filter
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
                // Menu item for each transaction type
                Transaction.Type.values().forEach { type ->
                    DropdownMenuItem(
                        onClick = {
                            onTypeSelected(type)
                            isDropdownExpanded = false
                        },
                        text = {
                            Text(text = type.name)
                        }
                    )
                }
                DropdownMenuItem(
                    onClick = {
                        onTypeSelected(null) // Reset filter to show all types
                        isDropdownExpanded = false
                    },
                    text = { Text(text = "All") }
                )
            }
        }
    }
}
