package com.ruchitech.cashentery.retrofit.model

class Tags : ArrayList<Tags.TagsItem>() {
    data class TagsItem(
        val balance: Double, // 10
        val lastTransactionAmount: Double, // 10
        val lastTransactionStatus: String, // CLEARED
        val lastUsedDate: String, // 2024-07-30
        val settlementMessage: String, // You have to pay 10
        val lastTransactionType: String, // You have to pay 10
        val tag: String, // Joota
        val totalCreditAmount: Double, // 160
        val totalCreditSum: Double, // 160
        val totalDebitAmount: Double, // 150
        val totalDebitSum: Double, // 150
    )

    fun sortByTagName() {
        this.sortBy { it.tag }
    }
}