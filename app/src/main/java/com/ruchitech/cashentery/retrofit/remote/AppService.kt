package com.ruchitech.cashentery.retrofit.remote

import com.ruchitech.cashentery.retrofit.model.Tags
import com.ruchitech.cashentery.retrofit.model.TrnxSummary
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import com.ruchitech.cashentery.ui.screens.add_transactions.data.CreateTransaction
import com.ruchitech.cashentery.ui.screens.mobile_auth.data.CreateUser
import com.ruchitech.cashentery.ui.screens.mobile_auth.data.CreatedUserResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AppService {
    companion object {
        private const val CREATE_USER = "/api/createUser"
        private const val TRANSACTION_SUMMARY = "/api/transactions/summary/"
        private const val TRANSACTION_TAGS = "/api/transactions/tagSummary/"
        private const val CREATE_TRANSACTION= "/api/createTransaction"

    }

    @POST(CREATE_USER)
    fun createUser(@Body createUser: CreateUser): Flow<ApiResponse<CreatedUserResponse>>


    @POST(TRANSACTION_SUMMARY)
    fun transactionSummary(@Query("authId") authId: String): Flow<ApiResponse<TrnxSummary>>

    @POST(TRANSACTION_TAGS)
    fun transactionTagSummary(@Query("authId") authId: String): Flow<ApiResponse<Tags>>

    @POST(CREATE_TRANSACTION)
    fun createTransaction(@Body createTransaction: Transaction): Flow<ApiResponse<Transaction>>


    /*    @POST(SEND_OTP)
        fun sendOtp(@Body sendOtp: SendOtp): Flow<ApiResponse<SendOtpResult>>

        @POST(VERIFY_OTP)
        fun verifyOtp(@Body verifyOtp: VerifyOtp): Flow<ApiResponse<VerifyOtpResult>>*/


}


