package com.ruchitech.cashentery.ui.screens.chatview

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ruchitech.cashentery.helper.Event
import com.ruchitech.cashentery.helper.EventEmitter
import com.ruchitech.cashentery.helper.Result
import com.ruchitech.cashentery.helper.SharedViewModel
import com.ruchitech.cashentery.helper.sharedpreference.AppPreference
import com.ruchitech.cashentery.helper.toast.MyToast
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class TransactionDetailsViewModel @Inject constructor(
    private val appPreference: AppPreference,
    private val myToast: MyToast,
) : SharedViewModel() {
    private val _transactionsFlow = MutableStateFlow<List<Transaction>>(emptyList())
    val transactionsFlow: StateFlow<List<Transaction>> = _transactionsFlow
    private val _categories =
        MutableStateFlow(appPreference.categoriesList.ifEmpty { arrayListOf() })
    val categories: StateFlow<List<String>> = _categories
    val showLoading = mutableStateOf(false)
    private val db = FirebaseFirestore.getInstance()

    private val _result = MutableStateFlow<Result?>(null)
    val result: StateFlow<Result?> = _result
    private val debounceInterval = 2000L // 1 second
    private var lastEventTime: Long = 0

    fun resetState() {
        _result.value = Result.ResetState
    }

    fun getTransactionDetails(transactionId: String): List<Transaction> {
        val listType = object : TypeToken<List<Transaction>>() {}.type
        val transactions: List<Transaction> = Gson().fromJson(transactionId, listType)
        _transactionsFlow.value = transactions.sortedBy { it.timeInMillis }
        return transactions
    }

    private fun addTransaction(newTransaction: Transaction) {
        val tempTagList = categories.value.toMutableList()
        tempTagList.add(
            newTransaction.tag?.trim()
                ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.ROOT) else it.toString() }
                ?: ""
        )
        _categories.value = tempTagList.toList()
        appPreference.categoriesList = categories.value
        val currentTransactions = _transactionsFlow.value.toMutableList()
        currentTransactions.add(newTransaction)
        val sortedTransactions = currentTransactions.sortedBy { it.timeInMillis }
        _transactionsFlow.value = sortedTransactions
    }


    fun updateTransaction(updatedTransaction: Transaction) {
        val currentTransactions = _transactionsFlow.value
        val updatedTransactions = currentTransactions.map { transaction ->
            if (transaction.id == updatedTransaction.id) {
                updatedTransaction
            } else {
                transaction
            }
        }
        _transactionsFlow.value = updatedTransactions
        updateTransactionDb(updatedTransaction)
    }

    private fun updateTransactionDb(transaction: Transaction) {
        val transactionData = hashMapOf(
            "account" to transaction.account,
            "amount" to transaction.amount,
            "date" to transaction.date,
            "remarks" to transaction.remarks,
            "tag" to transaction.tag,
            "timeInMiles" to transaction.timeInMillis,
            "transactionNumber" to transaction.transactionNumber,
            "type" to transaction.type,
            "status" to transaction.status
        )

        showLoading.value = true
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("transactions").child(appPreference.userId ?: "")
        val transactionId = myRef.push().key // Generate a unique ID for the transaction

        if (transactionId != null) {
            val transactionWithId = transaction.id
            val jsonString = Json.encodeToString(transactionWithId)
            db.collection("users").document(appPreference.userId ?: "").collection("transactions")
                .document(transaction.id ?: "") // assume transactionData has an id property
                .set(transactionData, SetOptions.merge())
                .addOnSuccessListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(2000)
                        showLoading.value = false
                        _result.value = Result.Success
                    }
                    //myToast.showToast("Transaction updated successfully.")
                    EventEmitter postEvent Event.HomeViewModel(
                        transaction = transaction
                    )
                    println("Transaction updated successfully.")
                }
                .addOnFailureListener { exception ->
                    Log.e("MyViewModel", "Error updating transaction: ${exception.message}")
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(2000)
                        showLoading.value = false
                    }
                    myToast.showToast("Failed to update transaction: ${exception.message}")
                    println("Failed to update transaction: ${exception.message}")
                    _result.value = Result.Success
                }

        } else {
            showLoading.value = false
            _result.value = Result.Error
            println("Failed to generate transaction ID.")
        }
    }

    fun deleteTransactionDb(transactionId: String, dataToEdit: Transaction?) {
        showLoading.value = true
        db.collection("users").document(appPreference.userId ?: "").collection("transactions")
            .document(transactionId)
            .delete()
            .addOnSuccessListener {
                val currentTransactions = _transactionsFlow.value.toMutableList()
                val dataToDelete = currentTransactions.find { it.id == transactionId }
                currentTransactions.remove(dataToEdit)
                _transactionsFlow.value = currentTransactions
                if (currentTransactions.isEmpty()) {
                    _result.value = Result.Success
                }
                showLoading.value = false
                myToast.showToast("Transaction deleted successfully.")
                EventEmitter postEvent Event.HomeViewModel(
                    deleteId = transactionId,
                    transaction = null
                )
                _result.value = Result.Success
                println("Transaction deleted successfully.")
            }
            .addOnFailureListener { exception ->
                Log.e("MyViewModel", "Error deleting transaction: ${exception.message}")
                showLoading.value = false
                myToast.showToast("Failed to delete transaction: ${exception.message}")
                println("Failed to delete transaction: ${exception.message}")
                _result.value = Result.Error
            }
    }

    override fun handleInternalEvent(event: Event) {

        Log.e("mlgfjhkmf", "handleInternalEvent: 163")
        when (event) {
            is Event.HomeViewModel -> Unit
            is Event.TransactionDetailsViewModel -> {
                val currentTime = System.currentTimeMillis()
                val timeDifference = currentTime - lastEventTime
                Log.e("mlgfjhkmf", "handleInternalEvent: $lastEventTime")
                if (timeDifference >= debounceInterval) {
                    if (event.transaction != null) {
                        Log.e("mlgfjhkmf", "handleInternalEvent: 168")
                        val findOut = transactionsFlow.value.find {
                            it.tag?.toLowerCase(Locale.current) == event.transaction.tag?.toLowerCase(
                                Locale.current
                            )
                        }
                        if (findOut != null) {
                            addTransaction(event.transaction)
                        }
                    }
                }
                lastEventTime = currentTime
            }

            is Event.TransactionsViewModel -> Unit
        }
    }


}