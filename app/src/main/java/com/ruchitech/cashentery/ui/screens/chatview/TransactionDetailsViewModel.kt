package com.ruchitech.cashentery.ui.screens.chatview

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.ruchitech.cashentery.helper.Event
import com.ruchitech.cashentery.helper.EventEmitter
import com.ruchitech.cashentery.helper.Result
import com.ruchitech.cashentery.helper.SharedViewModel
import com.ruchitech.cashentery.helper.sharedpreference.AppPreference
import com.ruchitech.cashentery.helper.toast.MyToast
import com.ruchitech.cashentery.retrofit.model.TransactionsBytag
import com.ruchitech.cashentery.retrofit.model.UpdateTag
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
class TransactionDetailsViewModel @Inject constructor(
    private val appPreference: AppPreference,
    private val myToast: MyToast,
    private val accountRepository: AccountRepository,
) : SharedViewModel() {
    private val _transactionsFlow = MutableStateFlow<TransactionsBytag?>(null)
    val transactionsFlow: StateFlow<TransactionsBytag?> = _transactionsFlow
    private val _categories =
        MutableStateFlow(appPreference.categoriesList.ifEmpty { arrayListOf() })
    val categories: StateFlow<List<String>> = _categories
    val showLoading = mutableStateOf(false)
    val selectedTag = mutableStateOf("")
    val userData = mutableStateOf(appPreference.userData)
    private val db = FirebaseFirestore.getInstance()

    private val _result = MutableStateFlow<Result?>(null)
    val result: StateFlow<Result?> = _result
    private val debounceInterval = 2000L // 1 second
    private var lastEventTime: Long = 0


    init {

    }

    fun resetState() {
        _result.value = Result.ResetState
    }

    private fun fetchTransactionsByTag(data: TrxnByTagRes) {
        viewModelScope.launch {
            accountRepository.transactionsByTag(data)
                .distinctUntilChanged()
                .collectLatest { resources ->
                    when (resources.status) {
                        Status.INITIAL -> Unit
                        Status.EMPTY -> Unit
                        Status.SUCCESS -> {
                            hideLoading()
                            _transactionsFlow.value = resources.data
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

    fun updateTransaction(updatedTransaction: Transaction) {
        viewModelScope.launch {
            accountRepository.updateTransaction(updatedTransaction)
                .distinctUntilChanged()
                .collectLatest { resources ->
                    when (resources.status) {
                        Status.INITIAL -> Unit
                        Status.EMPTY -> Unit
                        Status.SUCCESS -> {
                            showLoading.value = false
                            myToast.showToast("Transaction updated successfully.")
                            getData(selectedTag.value)
                            EventEmitter postEvent Event.HomeViewModel(
                                deleteId = null,
                                transaction = null,
                                refreshPage = true
                            )
                            _result.value = Result.Success
                        }

                        Status.ERROR -> {
                            showLoading.value = false
                        }

                        Status.LOADING -> showLoading.value = true
                    }
                }
        }
    }

    fun updateTag(updatedTag: UpdateTag) {
        val dataToSend = updatedTag.copy(authId = appPreference.userId ?: "")
        viewModelScope.launch {
            accountRepository.updateTag(dataToSend)
                .distinctUntilChanged()
                .collectLatest { resources ->
                    when (resources.status) {
                        Status.INITIAL -> Unit
                        Status.EMPTY -> Unit
                        Status.SUCCESS -> {
                            showLoading.value = false
                            myToast.showToast(resources.data?.message?:"Tag updated successfully")
                            getData(updatedTag.newTagName)
                            EventEmitter postEvent Event.HomeViewModel(
                                deleteId = null,
                                transaction = null,
                                refreshPage = true
                            )
                        }

                        Status.ERROR -> {
                            showLoading.value = false
                        }

                        Status.LOADING -> showLoading.value = true
                    }
                }
        }
    }


    fun getData(tag: String) {
        selectedTag.value = tag
        val data = TrxnByTagRes(
            tag = tag,
            authId = appPreference.userId ?: "",
            startDate = "",
            endDate = ""
        )

        fetchTransactionsByTag(data)
    }

    fun deleteTransactionDb(transactionId: String) {
        viewModelScope.launch {
            accountRepository.deleteTransaction(transactionId)
                .distinctUntilChanged()
                .collectLatest { resources ->
                    when (resources.status) {
                        Status.INITIAL -> Unit
                        Status.EMPTY -> Unit
                        Status.SUCCESS -> {
                            showLoading.value = false
                            myToast.showToast("Transaction deleted successfully.")
                            EventEmitter postEvent Event.HomeViewModel(
                                deleteId = null,
                                transaction = null,
                                refreshPage = true
                            )
                            getData(selectedTag.value)
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
        when (event) {
            is Event.HomeViewModel -> Unit
            is Event.TransactionDetailsViewModel -> {
                val currentTime = System.currentTimeMillis()
                val timeDifference = currentTime - lastEventTime
                if (timeDifference >= debounceInterval) {
                    if (event.transaction != null) {
                        val findOut = transactionsFlow.value?.transactions?.find {
                            it.tag?.toLowerCase(Locale.current) == event.transaction.tag?.toLowerCase(
                                Locale.current
                            )
                        }
                        if (findOut != null) {
                            //    addTransaction(event.transaction)
                            getData(selectedTag.value)
                        }
                    }
                }
                lastEventTime = currentTime
            }

            is Event.TransactionsViewModel -> Unit
        }
    }


}