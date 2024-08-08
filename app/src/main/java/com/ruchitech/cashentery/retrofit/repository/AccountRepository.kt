package com.ruchitech.cashentery.retrofit.repository


import com.ruchitech.cashentery.helper.sharedpreference.AppPreference
import com.ruchitech.cashentery.retrofit.RateLimiter
import com.ruchitech.cashentery.retrofit.model.Tags
import com.ruchitech.cashentery.retrofit.model.TrnxSummary
import com.ruchitech.cashentery.retrofit.remote.AppService
import com.ruchitech.cashentery.retrofit.remote.Resource
import com.ruchitech.cashentery.retrofit.repository.resource.networkOnlyResource
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import com.ruchitech.cashentery.ui.screens.add_transactions.data.CreateTransaction
import com.ruchitech.cashentery.ui.screens.mobile_auth.data.CreateUser
import com.ruchitech.cashentery.ui.screens.mobile_auth.data.CreatedUserResponse
import kotlinx.coroutines.flow.Flow
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
            appService.transactionSummary(appPreference.userId?:"")
        }, shouldFetchFromRemote = { repoListRateLimit.shouldFetch("transactionSummary") })
    }

    fun transactionTags(): Flow<Resource<Tags>> {
        return networkOnlyResource(fetchFromRemote = {
            appService.transactionTagSummary(appPreference.userId?:"")
        }, shouldFetchFromRemote = { repoListRateLimit.shouldFetch("transactionTags") })
    }

    fun createTransaction(createTransaction: Transaction): Flow<Resource<Transaction>> {
        return networkOnlyResource(fetchFromRemote = {
            appService.createTransaction(createTransaction)
        }, shouldFetchFromRemote = { repoListRateLimit.shouldFetch("createTransaction") })
    }


}