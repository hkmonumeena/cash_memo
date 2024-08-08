package com.ruchitech.cashentery.retrofit.model

class Tags : ArrayList<Tags.TagsItem>(){
    data class TagsItem(
        val balance: Double, // 10
        val lastTransactionAmount: Int, // 10
        val lastTransactionStatus: String, // CLEARED
        val lastUsedDate: String, // 2024-07-30
        val settlementMessage: String, // You have to pay 10
        val lastTransactionType: String, // You have to pay 10
        val tag: String, // Joota
        val totalCreditAmount: Int, // 160
        val totalCreditSum: Int, // 160
        val totalDebitAmount: Int, // 150
        val totalDebitSum: Int // 150
    )
}