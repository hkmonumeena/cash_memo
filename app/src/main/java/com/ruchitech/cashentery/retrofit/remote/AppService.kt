package com.ruchitech.cashentery.retrofit.remote

import com.ruchitech.cashentery.retrofit.model.BaseResponse
import com.ruchitech.cashentery.retrofit.model.Tags
import com.ruchitech.cashentery.retrofit.model.TransactionsByDate
import com.ruchitech.cashentery.retrofit.model.TransactionsBytag
import com.ruchitech.cashentery.retrofit.model.TrnxCrudResponse
import com.ruchitech.cashentery.retrofit.model.TrnxSummary
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import com.ruchitech.cashentery.ui.screens.chatview.TrxnByTagRes
import com.ruchitech.cashentery.ui.screens.mobile_auth.data.CreateUser
import com.ruchitech.cashentery.ui.screens.mobile_auth.data.CreatedUserResponse
import com.ruchitech.cashentery.ui.screens.transactions.FilterTrnx
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AppService {
    companion object {
        private const val CREATE_USER = "/api/createUser"
        private const val DELETE_USER = "/api/users/"
        private const val TRANSACTION_SUMMARY = "/api/transactions/summary/"
        private const val TRANSACTION_TAGS = "/api/transactions/tagSummary/"
        private const val CREATE_TRANSACTION= "/api/createTransaction"
        private const val UPDATE_TRANSACTION= "/api/transactions/update"
        private const val DELETE_TRANSACTION= "/api/transactions/delete/"
        private const val FILTER_TRANSACTION= "/api/transactions/filterTransactions"
        private const val TRANSACTIONS_BY_TAG= "/api/transactions/byTag"
    }

    @POST(CREATE_USER)
    fun createUser(@Body createUser: CreateUser): Flow<ApiResponse<CreatedUserResponse>>

    @POST(DELETE_USER)
    fun deleteUser(@Query("authId") authId: String): Flow<ApiResponse<BaseResponse>>

    @POST(TRANSACTION_SUMMARY)
    fun transactionSummary(@Query("authId") authId: String): Flow<ApiResponse<TrnxSummary>>

    @POST(TRANSACTION_TAGS)
    fun transactionTagSummary(@Query("authId") authId: String,@Body filterTrnx: FilterTrnx): Flow<ApiResponse<Tags>>

    @POST(CREATE_TRANSACTION)
    fun createTransaction(@Body createTransaction: Transaction): Flow<ApiResponse<Transaction>>

    @POST(UPDATE_TRANSACTION)
    fun updateTransaction(@Body createTransaction: Transaction): Flow<ApiResponse<TrnxCrudResponse>>

    @POST(FILTER_TRANSACTION)
    fun filterTransaction(@Body filterTrnx: FilterTrnx): Flow<ApiResponse<TransactionsByDate>>

    @POST(TRANSACTIONS_BY_TAG)
    fun transactionByTag(@Body trxnByTagRes: TrxnByTagRes): Flow<ApiResponse<TransactionsBytag>>

    @POST(DELETE_TRANSACTION)
    fun deleteTransaction(@Query("id") id: String): Flow<ApiResponse<TrnxCrudResponse>>



    /*
     @POST(SEND_OTP)
        fun sendOtp(@Body sendOtp: SendOtp): Flow<ApiResponse<SendOtpResult>>

        @POST(VERIFY_OTP)
        fun verifyOtp(@Body verifyOtp: VerifyOtp): Flow<ApiResponse<VerifyOtpResult>>

        */


}


