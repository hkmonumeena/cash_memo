package com.ruchitech.cashentery.helper

import com.ruchitech.cashentery.helper.navigation.Screen
import com.ruchitech.cashentery.ui.screens.add_transactions.Account
import com.ruchitech.cashentery.ui.screens.add_transactions.Status
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import com.ruchitech.cashentery.ui.screens.add_transactions.Type
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID


const val DEBIT = "DEBIT"
const val credit = "CREDIT"
const val ONLINE = "ONLINE"
const val cash = "CASH"
const val pending = "PENDING"
const val PAID = "PAID"
const val overdue = "OVERDUE"


fun formatMilliSecondsToDateTime(milliSeconds: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = milliSeconds
    return formatter.format(calendar.time)
}

fun getCurrentDateWithFixedTime(): Date {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 5)
    calendar.set(Calendar.MINUTE, 30)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

/**
 * SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(currentDateTime)
 */
fun getCurrentDateWithTime(): String? {
    val currentDateTime = Date().time
    return SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(currentDateTime)
}

fun getCurrentMillies(): Long {
    return Date().time
}

fun combineDateWithCurrentTime(dateInLong: Long): Date {
    val calendar = Calendar.getInstance()
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val currentMinute = calendar.get(Calendar.MINUTE)
    val currentSecond = calendar.get(Calendar.SECOND)
    val currentMillisecond = calendar.get(Calendar.MILLISECOND)
    calendar.timeInMillis = dateInLong
    calendar.set(Calendar.HOUR_OF_DAY, currentHour)
    calendar.set(Calendar.MINUTE, currentMinute)
    calendar.set(Calendar.SECOND, currentSecond)
    calendar.set(Calendar.MILLISECOND, currentMillisecond)

    return calendar.time
}

fun getInitialTransaction(): Transaction {
    val transactionId = UUID.randomUUID().toString()
    val transactionNumber = transactionId.removeRange(15, transactionId.length)
    val newTransaction = Transaction(
        date = getCurrentDateWithTime(),
        type = Type.DEBIT,
        account = Account.ONLINE,
        transactionNumber = transactionNumber,
        timeInMiles = getCurrentMillies(),
        status = Status.CLEARED
    )
    return newTransaction
}

/**
 * this will hide the add transaction button on selected  screen
 */
fun hideTransactionButton(destinationId: String?): Boolean {
    val currentRoute = destinationId?.split("/")?.get(0)
    return when (currentRoute) {
        Screen.MobileAuth::class.qualifiedName -> true
        Screen.VerifyOtp::class.qualifiedName -> true
        Screen.AddTransaction::class.qualifiedName -> true
        else -> false
    }
}