package com.ruchitech.cashentery.helper.navigation

import com.ruchitech.cashentery.ui.screens.add_transactions.AddTransactionData
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {

    @Serializable
    data object MobileAuth : Screen()

    @Serializable
    data class VerifyOtp(val verificationId: String?) : Screen()

    @Serializable
    data object Home : Screen()

    @Serializable
    data object AddTransaction : Screen()

    @Serializable
    data object Transactions : Screen()

    @Serializable
    data class TransactionDetails(val transactions: String) : Screen()

}