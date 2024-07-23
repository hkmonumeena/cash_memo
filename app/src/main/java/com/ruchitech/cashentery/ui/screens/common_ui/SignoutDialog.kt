package com.ruchitech.cashentery.ui.screens.common_ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
 fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {

    AlertDialog(icon = {
        Icon(Icons.Default.Delete, contentDescription = "Example Icon")
    }, title = {
        Text(text = "Confirm Deletion")
    }, text = {
        Text(text = "Are you sure you want to delete this transaction?")
    }, onDismissRequest = onCancel, confirmButton = {
        TextButton(
            onClick = onConfirm
        ) {
            Text("Confirm")
        }
    }, dismissButton = {
        TextButton(
            onClick = onCancel
        ) {
            Text("Dismiss")
        }
    })

}