package com.ruchitech.cashentery.retrofit.repository


import com.ruchitech.cashentery.helper.sharedpreference.AppPreference
import com.ruchitech.cashentery.retrofit.RateLimiter
import com.ruchitech.cashentery.retrofit.model.Tags
import com.ruchitech.cashentery.retrofit.model.TransactionsByDate
import com.ruchitech.cashentery.retrofit.model.TransactionsBytag
import com.ruchitech.cashentery.retrofit.model.TrnxCrudResponse
import com.ruchitech.cashentery.retrofit.model.TrnxSummary
import com.ruchitech.cashentery.retrofit.remote.AppService
import com.ruchitech.cashentery.retrofit.remote.Resource
import com.ruchitech.cashentery.retrofit.repository.resource.networkOnlyResource
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import com.ruchitech.cashentery.ui.screens.chatview.TrxnByTagRes
import com.ruchitech.cashentery.ui.screens.mobile_auth.data.CreateUser
import com.ruchitech.cashentery.ui.screens.mobile_auth.data.CreatedUserResponse
import com.ruchitech.cashentery.ui.screens.transactions.FilterTrnx
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class AccountRepository
@Inject constructor(
    private val appService: AppService,
    private val appPreference: AppPreference,
) {
    private val repoListRateLimit = RateLimiter<String>(1, TimeUnit.SECONDS)

    fun createUserApi(createUser: CreateUser): Flow<Resource<CreatedUserResponse>> {
        return networkOnlyResource(fetchFromRemote = {
            appService.createUser(createUser)
        }, shouldFetchFromRemote = { repoListRateLimit.shouldFetch("createUserApi") })
    }


    fun transactionSummary(): Flow<Resource<TrnxSummary>> {
        return networkOnlyResource(fetchFromRemote = {
            appService.transactionSummary(appPreference.userId ?: "")
        }, shouldFetchFromRemote = { repoListRateLimit.shouldFetch("transactionSummary") })
    }

    fun transactionTags(filterTrnx: FilterTrnx): Flow<Resource<Tags>> {
        return networkOnlyResource(fetchFromRemote = {
            appService.transactionTagSummary(appPreference.userId ?: "",filterTrnx)
        }, shouldFetchFromRemote = { repoListRateLimit.shouldFetch("transactionTags${Date()}") })
    }

    fun createTransaction(createTransaction: Transaction): Flow<Resource<Transaction>> {
        return networkOnlyResource(fetchFromRemote = {
            appService.createTransaction(createTransaction)
        }, shouldFetchFromRemote = { repoListRateLimit.shouldFetch("createTransaction") })
    }

    fun updateTransaction(createTransaction: Transaction): Flow<Resource<TrnxCrudResponse>> {
        return networkOnlyResource(fetchFromRemote = {
            appService.updateTransaction(createTransaction)
        }, shouldFetchFromRemote = { repoListRateLimit.shouldFetch("${Date()}createTransaction") })
    }

    fun deleteTransaction(id: String): Flow<Resource<TrnxCrudResponse>> {
        return networkOnlyResource(fetchFromRemote = {
            appService.deleteTransaction(id)
        }, shouldFetchFromRemote = { repoListRateLimit.shouldFetch("${Date()}deleteTransaction") })
    }

    fun filteredTransaction(filterTrnx: FilterTrnx): Flow<Resource<TransactionsByDate>> {
        return networkOnlyResource(fetchFromRemote = {
            appService.filterTransaction(filterTrnx)
        }, shouldFetchFromRemote = { repoListRateLimit.shouldFetch("filteredTransaction") })
    }

    fun transactionsByTag(trxnByTagRes: TrxnByTagRes): Flow<Resource<TransactionsBytag>> {
        return networkOnlyResource(fetchFromRemote = {
            appService.transactionByTag(trxnByTagRes)
        }, shouldFetchFromRemote = { repoListRateLimit.shouldFetch("transactionsByTag") })
    }

}