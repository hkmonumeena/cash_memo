package com.ruchitech.cashentery.helper

import com.ruchitech.cashentery.helper.navigation.Screen
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
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
const val termsAndCond = "https://btgondia.com/tnc"
const val privacyPolicy = "https://btgondia.com/Privacy_Policy"

/**
 * Formats the given milliseconds since epoch into a human-readable date and time string.
 *
 * This function converts a timestamp in milliseconds to a formatted date and time string.
 * The timestamp represents milliseconds since the Unix epoch (January 1, 1970, 00:00:00 GMT).
 * The output format is "dd MMM yyyy HH:mm", where:
 * - "dd" represents the day of the month (01-31),
 * - "MMM" represents the abbreviated month name (e.g., "Jan", "Feb"),
 * - "yyyy" represents the year in four digits,
 * - "HH" represents the hour of the day in 24-hour format (00-23),
 * - "mm" represents the minutes (00-59).
 *
 * @param milliSeconds The timestamp in milliseconds since epoch to be formatted.
 * @return A `String` representing the formatted date and time.
 */
fun formatMilliSecondsToDateTime(milliSeconds: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()) // Create a SimpleDateFormat instance
    val calendar = Calendar.getInstance() // Create a Calendar instance
    calendar.timeInMillis = milliSeconds // Set the time in milliseconds
    return formatter.format(calendar.time) // Format the Calendar's time and return the formatted string
}

/**
 * Returns the current date with a fixed time set to 05:30.
 *
 * This function creates a `Calendar` instance for the current date and time, then sets the time
 * components (hours, minutes, seconds, milliseconds) to a fixed value of 05:30:00.000.
 * The resulting `Date` object represents the current date with the specified fixed time.
 *
 * @return A `Date` object representing the current date with the time set to 05:30:00.
 */
fun getCurrentDateWithFixedTime(): Date {
    val calendar = Calendar.getInstance() // Get the current date and time
    calendar.set(Calendar.HOUR_OF_DAY, 5) // Set the hour to 05
    calendar.set(Calendar.MINUTE, 30) // Set the minute to 30
    calendar.set(Calendar.SECOND, 0) // Set seconds to 00
    calendar.set(Calendar.MILLISECOND, 0) // Set milliseconds to 000
    return calendar.time // Return the Date object with the fixed time
}

/**
 * Returns the current date and time formatted as a string.
 *
 * This function retrieves the current date and time, then formats it using the pattern
 * "dd MMM yyyy HH:mm". The pattern represents the date and time as day-month-year hours:minutes.
 * The formatting is done based on the default locale of the device.
 *
 * @return A string representing the current date and time in the format "dd MMM yyyy HH:mm",
 *         or null if the formatting operation fails.
 */
fun getCurrentDateWithTime(): String? {
    val currentDateTime = Date() // Get the current date and time
    return try {
        // Format the date and time using the specified pattern and default locale
        SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(currentDateTime)
    } catch (e: Exception) {
        // Handle any potential exceptions that may occur during formatting
        null
    }
}

/**
 * Retrieves the current timestamp in milliseconds since the Unix epoch.
 *
 * This function returns the current time as a number of milliseconds that have elapsed
 * since the Unix epoch (January 1, 1970, 00:00:00 GMT). This timestamp is useful for
 * measuring elapsed time, scheduling events, or recording the time of specific actions.
 *
 * @return A `Long` value representing the current time in milliseconds since the Unix epoch.
 */
fun getCurrentMillis(): Long {
    return Date().time // Returns the current time in milliseconds
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

/**
 * Creates and returns a new `Transaction` instance with initial default values.
 *
 * This function generates a new `Transaction` with the following default properties:
 * - A unique identifier for the transaction.
 * - A truncated transaction number derived from the unique identifier.
 * - The current date and time formatted as a string.
 * - The current timestamp in milliseconds.
 * - Default values for transaction type, account type, and status.
 *
 * The `Transaction` object is initialized with:
 * - `date`: The current date and time formatted as "dd MMM yyyy HH:mm".
 * - `type`: Defaulted to [Transaction.Type.DEBIT].
 * - `account`: Defaulted to [Transaction.Account.ONLINE].
 * - `transactionNumber`: A truncated version of a randomly generated UUID.
 * - `timeInMillis`: The current time in milliseconds since the Unix epoch.
 * - `status`: Defaulted to [Transaction.Status.CLEARED].
 *
 * @return A `Transaction` instance with pre-defined default values.
 */
fun getInitialTransaction(): Transaction {
    val transactionId = UUID.randomUUID().toString()
    val transactionNumber = transactionId.removeRange(15, transactionId.length)

    return Transaction(
        date = getCurrentDateWithTime(),
        type = Transaction.Type.DEBIT,
        account = Transaction.Account.ONLINE,
        transactionNumber = transactionNumber,
        timeInMiles = getCurrentMillis(),
        status = Transaction.Status.CLEARED
    )
}

/**
 * Calculates the sum of transaction amounts based on specified type and status.
 *
 * This function filters a list of transactions by the given transaction type and status,
 * and then sums the amounts of the filtered transactions. If any transaction amount is null,
 * it is treated as zero in the summation.
 *
 * @param transactions The list of transactions to be filtered and summed.
 * @param type The type of transactions to be included in the sum. This is an enum of [Transaction.Type].
 * @param status The status of transactions to be included in the sum. This is an enum of [Transaction.Status].
 *
 * @return The total sum of amounts for transactions that match the specified type and status.
 *         If no transactions match, the function returns 0.0.
 *
 * @sample
 * val creditPendingSum = calculateSum(transactions, Transaction.Type.CREDIT, Transaction.Status.PENDING)
 * val debitClearedSum = calculateSum(transactions, Transaction.Type.DEBIT, Transaction.Status.CLEARED)
 *
 * @see Transaction
 */
fun calculateSum(
    transactions: List<Transaction>,
    type: Transaction.Type,
    status: Transaction.Status
): Double {
    return transactions
        .filter { it.type == type && it.status == status }
        .sumOf { it.amount ?: 0.0 }
}



/**
 * Determines whether the "Add Transaction" button should be hidden based on the current screen.
 *
 * This function evaluates the provided `destinationId` to determine if the button should be hidden
 * on specific screens. It uses the fully qualified names of the screens to check against known screens
 * where the button should be hidden.
 *
 * The button will be hidden on the following screens:
 * - [Screen.MobileAuth]
 * - [Screen.VerifyOtp]
 * - [Screen.AddTransaction]
 *
 * For any other screens, the button will be visible.
 *
 * @param destinationId The ID of the current destination screen. This should be a string representing
 *                      the route or path of the current screen.
 * @return `true` if the "Add Transaction" button should be hidden, `false` otherwise.
 */
fun hideTransactionButton(destinationId: String?): Boolean {
    val hiddenScreens = setOf(
        Screen.MobileAuth::class.qualifiedName,
        Screen.VerifyOtp::class.qualifiedName,
        Screen.AddTransaction::class.qualifiedName
    )
    // Extract the current route from the destination ID and check if it is in the hidden screens set
    return destinationId?.split("/")?.firstOrNull()?.let { it in hiddenScreens } ?: false
}

