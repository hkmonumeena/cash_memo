package com.ruchitech.cashentery.ui.screens.common_ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@Composable
fun DropdownSearch() {
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var expanded by remember { mutableStateOf(false) }
    val allItems = listOf("Apple", "Banana", "Orange", "Grapes", "Mango")
    val filteredItems = allItems.filter { it.contains(textFieldValue.text, ignoreCase = true) }

    val keyboardController = LocalSoftwareKeyboardController.current
    if (filteredItems.isNotEmpty()){
        keyboardController?.show()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = textFieldValue,
            onValueChange = { value ->
                textFieldValue = value
                expanded = value.text.isNotEmpty() && !filteredItems.contains(value.text)
            },
            label = { Text("Search") },
            modifier = Modifier.fillMaxWidth()
                .onFocusChanged { focusState ->
                    keyboardController?.show()
                }
        )


        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            properties = PopupProperties(focusable = false)
        ) {
            filteredItems.forEach { item ->
                DropdownMenuItem(onClick = {
                    textFieldValue = TextFieldValue(
                        text = item,
                        selection = TextRange(item.length)
                    )
                    expanded = false
                    keyboardController?.hide()
                }, text = {
                    Text(text = item)
                }, enabled = true)
                keyboardController?.show()
            }
        }
    }
}
