package com.ruchitech.cashentery.ui.screens.mobile_auth.data

import com.ruchitech.cashentery.retrofit.model.BaseResponse

data class CreatedUserResponse(
    val user: User,
) : BaseResponse() {
    data class User(
        val __v: Int, // 0
        val _id: String, // 66b197fdb249082f82b41528
        val authId: String, // oyugjhfg6fh
        val createdAt: String, // 2024-08-06T03:26:53.496Z
        val email: String,
        val name: String, // Monu Meena
        val phoneNumber: String, // 9131414134
        val updatedAt: String, // 2024-08-06T03:26:53.500Z
    )
}