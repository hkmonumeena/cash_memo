package com.ruchitech.cashentery.ui.screens.common_ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import com.ruchitech.cashentery.ui.theme.nonScaledSp

@Composable
fun MultiSelectType(
    selectedTypes: List<Transaction.Type>,
    onTypeSelected: (Transaction.Type) -> Unit,
) {
    val types = Transaction.Type.values().toList()

    Column(modifier = Modifier.padding(16.dp)) {
        types.forEach { type ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTypeSelected(type) }
                    .padding(vertical = 8.dp)
            ) {
                Checkbox(
                    checked = selectedTypes.contains(type),
                    onCheckedChange = { onTypeSelected(type) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = type.name, fontSize = 16.sp.nonScaledSp)
            }
        }
    }
}
