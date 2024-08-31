package com.ruchitech.cashentery.ui.screens.transactions

import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction

data class FilterTrnx(
    var account: List<Transaction.Account>,
    var amount: Amount,
    var authId: String, // W5mzbR4YFSTClH6Tsf28LilEH9d2
    var date: Date,
    val limit: Int, // 100
    val page: Int, // 1
    var status: List<Transaction.Status>,
    var tag: List<String>,
    var type: List<Transaction.Type>
) {
    data class Amount(
        val max: Double, // 10000.0
        val min: Double // 0.0
    )

    data class Date(
        val end: String, // 2024-07-31
        val start: String // 2024-07-31
    )
}