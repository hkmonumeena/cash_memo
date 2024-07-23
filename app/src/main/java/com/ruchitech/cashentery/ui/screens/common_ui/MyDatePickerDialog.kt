package com.ruchitech.cashentery.ui.screens.common_ui

import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import com.ruchitech.cashentery.helper.combineDateWithCurrentTime
import com.ruchitech.cashentery.helper.formatMilliSecondsToDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(onDismiss: () -> Unit, selectedDate:(date:String?, utcTimeMillis:Long?)->Unit) {
    val dateState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            Button(
                onClick = {
                     dateState.selectedDateMillis?.let { utcTimeMillis ->
                       val date = formatMilliSecondsToDateTime(combineDateWithCurrentTime(utcTimeMillis).time)
                         selectedDate(date,utcTimeMillis)
                    }
                }
            ) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() }
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