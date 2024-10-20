package com.ruchitech.cashentery.retrofit.model

data class UpdatedTagResponse(
    val matchedCount: Int, // 5
    val message: String, // Tag name updated successfully in 5 transactions.
    val modifiedCount: Int, // 5
    val success: Boolean // true
)