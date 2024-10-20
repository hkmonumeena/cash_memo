package com.ruchitech.cashentery.helper.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {

    @Serializable
    data object MobileAuth : Screen()

    @Serializable
    data object SignInGoogle : Screen()

    @Serializable
    data object SplashScreen : Screen()

    @Serializable
    data class VerifyOtp(val verificationId: String?, val mobileNumber: String?) : Screen()

    @Serializable
    data object Home : Screen()

    @Serializable
    data class AddTransaction(val type: Int = 2) : Screen() //   1 for income, 2 for expense

    @Serializable
    data object Transactions : Screen()

    @Serializable
    data object Settings : Screen()

    @Serializable
    data class TransactionDetails(val transactions: String) : Screen()

}