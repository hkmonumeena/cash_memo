package com.ruchitech.cashentery.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment

// Enum to represent the transaction view type
enum class TransactionViewType(val displayName: String) {
    CurrentMonth("Current Month Only"),
    Overall("Overall Data")
}

/**
 * Settings UI composable where the user can select the transaction view type.
 *
 * @param selectedViewType Current selected view type option.
 * @param onOptionSelected Callback to handle when the user selects a new option.
 */
@Composable
fun SettingsUi(
    selectedViewType: TransactionViewType,
    onOptionSelected: (TransactionViewType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Title of the settings screen
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Card for transaction view type selection
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Select Transaction View Type",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Loop through the view types and create radio buttons with icons
                TransactionViewType.values().forEach { viewType ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .selectable(
                                selected = (viewType == selectedViewType),
                                onClick = { onOptionSelected(viewType) }
                            )
                    ) {
                        Icon(
                            imageVector = if (viewType == TransactionViewType.CurrentMonth) Icons.Filled.DateRange else Icons.Filled.List,
                            contentDescription = null,
                            tint = if (viewType == selectedViewType) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        RadioButton(
                            selected = (viewType == selectedViewType),
                            onClick = { onOptionSelected(viewType) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = viewType.displayName,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                color = if (viewType == selectedViewType) MaterialTheme.colorScheme.primary else Color.Black
                            )
                        )
                    }
                    Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsUiPreview() {
    var selectedViewType by remember { mutableStateOf(TransactionViewType.CurrentMonth) }

    SettingsUi(
        selectedViewType = selectedViewType,
        onOptionSelected = { selectedViewType = it }
    )
}
