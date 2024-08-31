package com.ruchitech.cashentery.retrofit.model

import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction

data class TrnxCrudResponse(
    val message: String, // Transaction deleted successfully
    val transaction: Transaction,
) {
}