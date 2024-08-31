package com.ruchitech.cashentery.ui.screens.common_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.ui.theme.Expense
import com.ruchitech.cashentery.ui.theme.MainBackgroundSurface
import com.ruchitech.cashentery.ui.theme.nonScaledSp
import java.text.SimpleDateFormat
import java.util.Calendar


fun getFormattedDate(timeInMillis: Long): String {
    val calender = Calendar.getInstance()
    calender.timeInMillis = timeInMillis
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    return dateFormat.format(calender.timeInMillis)
}

fun dateValidator(): (Long) -> Boolean {
    return { timeInMillis ->
        // Get the selected date
        val selectedDate = Calendar.getInstance()
        selectedDate.timeInMillis = timeInMillis

        // Ensure that the selected date is not in the future
        !selectedDate.after(Calendar.getInstance())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleDatePickerView(
    onDismissRequest: () -> Unit,
    dates: (date1: String, date2: String, date1L: Long?, date2L: Long?) -> Unit,
    initialSelectedStartDateMillis: Long?, initialSelectedEndDateMillis: Long?,
) {

    val state =
        rememberDateRangePickerState(
            initialSelectedStartDateMillis = initialSelectedStartDateMillis,
            initialSelectedEndDateMillis = initialSelectedEndDateMillis
        )

    DateRangePickerSample(state = state, dates = { date1, date2, date1L, date2L ->
        dates(date1, date2, date1L, date2L)
    })
    /*
        ModalBottomSheet(onDismissRequest = { onDismissRequest() },
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle() }) {
            DateRangePickerSample(state = state, dates = { date1, date2 ->
                dates(date1, date2)
            })
        }
    */
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerSample(
    state: DateRangePickerState,
    dates: (date1: String, date2: String, date1L: Long?, date2L: Long?) -> Unit,
) {
    val context = LocalContext.current
    val datePickerFormatter = remember {
        DatePickerDefaults.dateFormatter("yyyy-MM-dd", "yyyy-MM-dd", "yyyy-MM-dd")
    }
    LaunchedEffect(state.selectedStartDateMillis, state.selectedEndDateMillis) {
        if (state.selectedStartDateMillis != null && state.selectedEndDateMillis != null) {
            dates(
                getFormattedDate(state.selectedStartDateMillis!!),
                getFormattedDate(state.selectedEndDateMillis!!),
                state.selectedStartDateMillis,
                state.selectedEndDateMillis
            )
        }
    }

    DateRangePicker(
        state = state,
        modifier = Modifier.background(MainBackgroundSurface),
        dateFormatter = datePickerFormatter,
        //dateValidator = dateValidator(),
        title = {
            Text(
                text = "Select date",
                modifier = Modifier
                    .padding(16.dp)
            )
        },
        headline = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
            ) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    (if (state.selectedStartDateMillis != null) state.selectedStartDateMillis?.let {
                        getFormattedDate(
                            it
                        )
                    } else "Start Date")?.let { Text(text = it, fontSize = 16.sp.nonScaledSp) }
                }
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    (if (state.selectedEndDateMillis != null) state.selectedEndDateMillis?.let {
                        getFormattedDate(it)
                    } else "End Date")?.let { Text(text = it, fontSize = 16.sp.nonScaledSp) }
                }
                /*
                                Box(Modifier.weight(0.2f)) {
                                    IconButton(onClick = {
                                        */
                /*       if (state.selectedStartDateMillis != null && state.selectedEndDateMillis != null) {
                                                   dates(
                                                       setStartTimeOfDay( state.selectedStartDateMillis!!).toString(),
                                                       setEndTimeOfDay(state.selectedEndDateMillis!!).toString()
                                                   )
                                               }*//*


                        dates(
                            getFormattedDate(state.selectedStartDateMillis!!),
                            getFormattedDate(state.selectedEndDateMillis!!)
                        )

                    }) {
                        Icon(imageVector = Icons.Default.Done, contentDescription = "Okk")
                    }
                }
*/

            }
        },
        showModeToggle = false,
        colors = DatePickerDefaults.colors(
            containerColor = Color.Blue,
            titleContentColor = Color.Black,
            headlineContentColor = Color.Black,
            weekdayContentColor = Color.Black,
            subheadContentColor = Color.Black,
            yearContentColor = Color.Green,
            currentYearContentColor = Color.Red,
            selectedYearContainerColor = Color.Red,
            disabledDayContentColor = Color.Gray,
            todayDateBorderColor = Color(0xFFFF6E40),
            dayInSelectionRangeContainerColor = Expense,
            dayInSelectionRangeContentColor = Color.White,
            selectedDayContainerColor = Color.DarkGray,
            todayContentColor = Color.Black
        )
    )
}
