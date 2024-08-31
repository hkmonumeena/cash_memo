package com.ruchitech.cashentery.ui.screens.transactions

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.ruchitech.cashentery.helper.Event
import com.ruchitech.cashentery.helper.EventEmitter
import com.ruchitech.cashentery.helper.Result
import com.ruchitech.cashentery.helper.SharedViewModel
import com.ruchitech.cashentery.helper.sharedpreference.AppPreference
import com.ruchitech.cashentery.helper.toast.MyToast
import com.ruchitech.cashentery.retrofit.model.TransactionsByDate
import com.ruchitech.cashentery.retrofit.remote.Status
import com.ruchitech.cashentery.retrofit.repository.AccountRepository
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val appPreference: AppPreference,
    private val myToast: MyToast,
    private val accountRepository: AccountRepository,
) : SharedViewModel() {
    private val _transactionsFlow = MutableStateFlow<List<Transaction>>(emptyList())
    val transactionsFlow: StateFlow<List<Transaction>> = _transactionsFlow
    private val _groupByTag = MutableStateFlow<Map<String?, List<Transaction>>?>(null)
    val groupByTag: StateFlow<Map<String?, List<Transaction>>?> = _groupByTag

    private val _transactionsByDate = MutableStateFlow<TransactionsByDate?>(null)
    val transactionsByDate: StateFlow<TransactionsByDate?> = _transactionsByDate

    private val db = FirebaseFirestore.getInstance()
    private val _categories =
        MutableStateFlow(appPreference.categoriesList.ifEmpty { arrayListOf() })
    val categories: StateFlow<List<String>> = _categories
    val showLoading = mutableStateOf(false)

    private val _result = MutableStateFlow<Result?>(null)
    val result: StateFlow<Result?> = _result

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
        fetchTransactionsByDate()
    }

    fun resetState() {
        _result.value = Result.ResetState
    }

    private fun fetchTransactionsByDate() {
        viewModelScope.launch {
            accountRepository.filteredTransaction(filterTrnx.value).distinctUntilChanged()
                .collectLatest { resources ->
                    when (resources.status) {
                        Status.INITIAL -> Unit
                        Status.EMPTY -> Unit
                        Status.SUCCESS -> {
                            _transactionsByDate.value = resources.data
                        }

                        Status.ERROR -> {
                            Log.e(
                                "fetchTransactionsByDate",
                                "fetchTransactionsByDate: ${resources.message}"
                            )
                        }

                        Status.LOADING -> {}
                    }
                }
        }
    }

    fun updateTransaction(updatedTransaction: Transaction) {
        viewModelScope.launch {
            accountRepository.updateTransaction(updatedTransaction).distinctUntilChanged()
                .collectLatest { resources ->
                    when (resources.status) {
                        Status.INITIAL -> Unit
                        Status.EMPTY -> Unit
                        Status.SUCCESS -> {
                            showLoading.value = false
                            myToast.showToast("Transaction updated successfully.")
                            fetchTransactionsByDate()
                            EventEmitter postEvent Event.HomeViewModel(
                                deleteId = null, transaction = null, refreshPage = true
                            )
                            _result.value = Result.Success
                        }

                        Status.ERROR -> {
                            showLoading.value = false
                            _result.value = Result.Error
                        }

                        Status.LOADING -> showLoading.value = true
                    }
                }
        }
    }


    fun getFilteredTransactions(trnx: FilterTrnx) {
        trnx.authId = appPreference.userId ?: ""
        _filterTrnx.value = trnx
        fetchTransactionsByDate()
    }

    fun deleteTransactionDb(transactionId: String) {
        viewModelScope.launch {
            accountRepository.deleteTransaction(transactionId).distinctUntilChanged()
                .collectLatest { resources ->
                    when (resources.status) {
                        Status.INITIAL -> Unit
                        Status.EMPTY -> Unit
                        Status.SUCCESS -> {
                            showLoading.value = false
                            myToast.showToast("Transaction deleted successfully.")
                            EventEmitter postEvent Event.HomeViewModel(
                                deleteId = null, transaction = null, refreshPage = true
                            )
                            fetchTransactionsByDate()
                            _result.value = Result.Success
                        }

                        Status.ERROR -> {
                            showLoading.value = false
                            myToast.showToast("Failed to delete transaction: ${resources.message}")
                            println("Failed to delete transaction: ${resources.message}")
                            _result.value = Result.Error
                        }

                        Status.LOADING -> {
                            showLoading.value = true
                        }
                    }
                }
        }
    }

    override fun handleInternalEvent(event: Event) {
        super.handleInternalEvent(event)
        when (event) {
            is Event.TransactionsViewModel -> {
                fetchTransactionsByDate()
            }

            else -> {}
        }
    }


}