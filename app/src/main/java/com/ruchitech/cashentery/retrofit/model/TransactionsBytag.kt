package com.ruchitech.cashentery.retrofit.model

import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction

data class TransactionsBytag(
    val summary: Summary,
    val transactions: List<Transaction>
) {
    data class Summary(
        val creditCleared: Double?, // 0
        val creditOverdue: Double?, // 3100
        val creditPending: Double?, // 0
        val creditSwap: Double?, // 0
        val debitSwap: Double?, // 0
        val creditVoid: Double?, // 0
        val debitCleared: Double?, // 0
        val debitOverdue: Double?, // 0
        val debitPending: Double?, // 0
        val debitVoid: Double?, // 3093
        val message: String, // You'll receive 3100 amount
        val netBalance: Double?, // 3100
        val totalCredits: Double?, // 3100
        val totalDebits: Double? // 3093
    )

}