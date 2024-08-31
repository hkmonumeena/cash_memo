package com.ruchitech.cashentery.retrofit.model

import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction

data class TransactionsByDate(
    val dateRangeSummary: DateRangeSummary,
    val filterCriteria: FilterCriteria,
    val pagination: Pagination,
    val transactionsByDate: List<TransactionsByDate>
) {
    data class DateRangeSummary(
        val netBalance: Double?, // -8000
        val totalCredits: Double?, // 0
        val totalDebits: Double? // 8000
    )

    data class FilterCriteria(
        val account: List<Any>,
        val amount: Amount,
        val date: Date,
        val status: List<Any>,
        val tag: List<Any>,
        val type: List<Any>
    ) {
        data class Amount(
            val max: Double?, // 10000
            val min: Double? // 0
        )

        data class Date(
            val end: String, // 2024-07-31
            val start: String // 2024-07-31
        )
    }

    data class Pagination(
        val currentPage: Int, // 1
        val hasMorePages: Boolean, // false
        val totalPages: Int, // 1
        val totalTransactions: Int // 4
    )

    data class TransactionsByDate(
        val date: String, // 31 Jul 2024 23:24
        val netBalance: Double?, // -6500
        val totalAmount: Double?, // 6500
        val totalCredits: Double?, // 0
        val totalDebits: Double?, // 6500
        val transactionCount: Double?, // 1
        val transactions: List<Transaction>
    ) {
        /*data class Transaction(
            val _id: String, // 66b51c842c07c6514faab171
            val account: String, // ONLINE
            val amount: Int, // 6500
            val authId: String, // W5mzbR4YFSTClH6Tsf28LilEH9d2
            val createdAt: String, // 2024-08-09T18:11:30.684Z
            val date: String, // 31 Jul 2024 23:24
            val id: String, // 1qrQW1krHHX2tj7wzenQ
            val remarks: String, // Spray pump purchase kiya crown company ka
            val status: String, // VOID
            val tag: String, // Kheti
            val timeInMiles: Long, // 1722448459031
            val transactionNumber: String, // ccadf698-af64-4
            val type: String, // DEBIT
            val updatedAt: String // 2024-08-09T18:11:30.684Z
        )*/
    }
}