package com.ruchitech.cashentery.ui.screens.add_transactions

import kotlinx.serialization.Serializable

enum class Type {
    CREDIT, DEBIT
}

enum class Account {
    ONLINE, CASH
}

@Serializable
data class MainCategory(
    val id: Long,
    val name: String,
)

@Serializable
data class SubCategory(
    val id: Long,
    val name: String,
)

@Serializable
data class AddTransactionData(
    var id: String? = null,
    val date: String? = null,
    val timeInMiles: Long? = null,
    val type: Type? = null, //CREDIT, DEBIT
    val account: Account? = null,// ONLINE,CASH
    val transactionNumber: String? = null,
    val amount: Double? = null,
    val remarks: String? = null,
    val tag: String? = null,
    /*    val mainCategory: MainCategory? = null,
        val subCategory: SubCategory? = null,*/
)
