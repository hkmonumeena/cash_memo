package com.ruchitech.cashentery.ui.screens.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ruchitech.cashentery.helper.Event
import com.ruchitech.cashentery.helper.SharedViewModel
import com.ruchitech.cashentery.helper.getCurrentMonthStartAndEndDate
import com.ruchitech.cashentery.helper.sharedpreference.AppPreference
import com.ruchitech.cashentery.retrofit.model.Tags
import com.ruchitech.cashentery.retrofit.model.TrnxSummary
import com.ruchitech.cashentery.retrofit.remote.Status
import com.ruchitech.cashentery.retrofit.repository.AccountRepository
import com.ruchitech.cashentery.ui.screens.Repository
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import com.ruchitech.cashentery.ui.screens.mobile_auth.data.CreateUser
import com.ruchitech.cashentery.ui.screens.transactions.FilterTrnx
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

fun formatMillisToDate(millis: Long): String {
    val date = Date(millis)
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return formatter.format(date)
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appPreference: AppPreference,
    private val repository: Repository,
    private val accountRepository: AccountRepository,
) : SharedViewModel() {
    private val _transactionsFlow = MutableStateFlow<List<Transaction>>(emptyList())
    val transactionsFlow: StateFlow<List<Transaction>> = _transactionsFlow

    private val _groupByTag = MutableStateFlow<Map<String?, List<Transaction>>?>(null)
    val groupByTag: StateFlow<Map<String?, List<Transaction>>?> = _groupByTag

    private val _trxnSummary = MutableStateFlow<TrnxSummary?>(null)
    val trnxSummary: StateFlow<TrnxSummary?> = _trxnSummary

    private val _trxnTags = MutableStateFlow<Tags?>(null)
    val trxnTags: StateFlow<Tags?> = _trxnTags

    private val _userData = MutableStateFlow<CreateUser?>(null)
    val userData: StateFlow<CreateUser?> = _userData

    private val _sumOfExpense = MutableStateFlow<Double?>(0.0)
    val sumOfExpense: StateFlow<Double?> = _sumOfExpense

    private val _sumOfIncome = MutableStateFlow<Double?>(0.0)
    val sumOfIncome: StateFlow<Double?> = _sumOfIncome

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    var transactions = ArrayList<Transaction>()
    var data = repository.fetchAllTransactions()
    private val _categories =
        MutableStateFlow(appPreference.categoriesList.ifEmpty { arrayListOf() })
    val categories: StateFlow<List<String>> = _categories


    private val _filterTrnx = MutableStateFlow<FilterTrnx>(
        FilterTrnx(
            account = listOf(), amount = FilterTrnx.Amount(
                max = 100000.0, min = 0.0
            ), authId = appPreference.userId ?: "", date = FilterTrnx.Date(
                end = "", start = ""
            ), limit = 100, page = 1, status = listOf(), tag = listOf(), type = listOf()
        )
    )
    val filterTrnx: StateFlow<FilterTrnx> = _filterTrnx


    init {
        val (start, end) = getCurrentMonthStartAndEndDate()
        if (appPreference.currentMonthOnly) {
            /*            _filterTrnx.value.date.start = start
                        _filterTrnx.value.date.end = end*/
            _filterTrnx.value.date.start = ""
            _filterTrnx.value.date.end = ""
        } else {
            _filterTrnx.value.date.start = ""
            _filterTrnx.value.date.end = ""
        }


        _userData.value = appPreference.userData

        fetchMongoDbSummaryTrnx()
    }

    // Define the page size
    private val pageSize = 10
    fun refreshData() {
        fetchMongoDbSummaryTrnx()
    }


    private fun fetchMongoDbSummaryTrnx() {
        viewModelScope.launch {
            accountRepository.transactionSummary().distinctUntilChanged()
                .collectLatest { resources ->
                    when (resources.status) {
                        Status.INITIAL -> Unit
                        Status.EMPTY -> Unit
                        Status.SUCCESS -> {
                            hideLoading()
                            val tags = resources.data
                            _trxnSummary.value = tags
                            fetchMongoDbTags()
                        }

                        Status.ERROR -> {
                            hideLoading()
                        }

                        Status.LOADING -> {
                            showLoading()
                        }
                    }
                }
        }
    }

    private fun fetchMongoDbTags() {
        viewModelScope.launch {
            accountRepository.transactionTags(filterTrnx.value).distinctUntilChanged()
                .collectLatest { resources ->
                    when (resources.status) {
                        Status.INITIAL -> Unit
                        Status.EMPTY -> Unit
                        Status.SUCCESS -> {
                            hideLoading()
                            val tags = resources.data
                            tags?.sortByTagName()
                            appPreference.categoriesList = tags?.map { it.tag } ?: emptyList()
                            _categories.value = appPreference.categoriesList
                            _trxnTags.value = tags
                        }

                        Status.ERROR -> {
                            hideLoading()
                            Log.e("Gdfgfdgf", "fetchMongoDbTags: ${resources.message}")
                        }

                        Status.LOADING -> {
                            showLoading()
                        }
                    }
                }
        }
    }


    fun deleteAccount(onDelete: () -> Unit) {
        viewModelScope.launch {
            accountRepository.deleteAccount().distinctUntilChanged().collectLatest { resources ->
                when (resources.status) {
                    Status.INITIAL -> Unit
                    Status.EMPTY -> Unit
                    Status.SUCCESS -> {
                        hideLoading()
                        signout()
                    }

                    Status.ERROR -> {
                        hideLoading()
                        signout()
                        onDelete()
                        Log.e("Gdfgfdgf", "fetchMongoDbTags: ${resources.message}")
                    }

                    Status.LOADING -> {
                        showLoading()
                    }
                }
            }
        }
    }


    fun getFilteredTransactions(trnx: FilterTrnx) {
        trnx.authId = appPreference.userId ?: ""
        _filterTrnx.value = trnx
        fetchMongoDbTags()
    }

    fun signout() {
        auth.signOut()
        appPreference.clearData()
        appPreference.isUserLoggedIn = false
        appPreference.userId = null
        appPreference.categoriesList = emptyList()
    }


    override fun handleInternalEvent(event: Event) {
        super.handleInternalEvent(event)
        when (event) {
            is Event.HomeViewModel -> {
                if (event.refreshPage) {
                    Log.e("gjfmfgg", "handleInternalEvent: refreshed")
                    refreshData()
                }
            }

            is Event.TransactionDetailsViewModel -> Unit
            is Event.TransactionsViewModel -> Unit
        }
    }

}