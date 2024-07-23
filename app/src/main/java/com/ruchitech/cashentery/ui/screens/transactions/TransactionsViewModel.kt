package com.ruchitech.cashentery.ui.screens.transactions

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(private val appPreference: AppPreference,private val myToast: MyToast) : SharedViewModel() {
    private val _transactionsFlow = MutableStateFlow<List<Transaction>>(emptyList())
    val transactionsFlow: StateFlow<List<Transaction>> = _transactionsFlow
    private val _groupByTag = MutableStateFlow<Map<String?, List<Transaction>>?>(null)
    val groupByTag: StateFlow<Map<String?, List<Transaction>>?> = _groupByTag
    private val db = FirebaseFirestore.getInstance()
    private val _categories =
        MutableStateFlow(appPreference.categoriesList.ifEmpty { arrayListOf() })
    val categories: StateFlow<List<String>> = _categories
    val showLoading = mutableStateOf(false)

    private val _result = MutableStateFlow<Result?>(null)
    val result: StateFlow<Result?> = _result
    init {
        fetchTransactions()
    }
    fun resetState() {
        _result.value = Result.ResetState
    }
    private fun fetchTransactions() {
        val userId = appPreference.userId  //"W5mzbR4YFSTClH6Tsf28LilEH9d2" //auth.currentUser?.uid

        if (userId == null) {
            println("User is not authenticated.")
            _transactionsFlow.value = emptyList()
            return
        }

        db.collection("users").document(userId).collection("transactions")
            .get()
            .addOnSuccessListener { result ->
                val transactions = ArrayList<Transaction>()
                result.forEach { document ->
                    val transaction =
                        document.toObject(Transaction::class.java).copy(id = document.id)
                    transactions.add(transaction)
                }

                //   transactions.value = transactionList
                _transactionsFlow.value = transactions.sortedByDescending { it.timeInMiles }
                /*   _groupByTag.value = transactions.sortedByDescending { it.timeInMiles }.groupBy { it.timeInMiles.toString() }.mapValues { entry ->
                       entry.value.sortedByDescending { it.timeInMiles }
                   }*/
                _groupByTag.value = groupTransactionsByDate(transactions)
            }
            .addOnFailureListener { exception ->
                Log.e("MyViewModel", "Error getting transactions: ${exception.message}")
                _transactionsFlow.value = emptyList()
            }
    }

    private fun groupTransactionsByDate(transactions: List<Transaction>): Map<String?, List<Transaction>> {
        return transactions
            .sortedByDescending { it.timeInMiles }
            .groupBy {
                // Convert timeInMiles to a Date object
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it.timeInMiles ?: 0
                // Extract the date part as a string (e.g., "2024-06-26")
                "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${
                    calendar.get(
                        Calendar.DAY_OF_MONTH
                    )
                }"
            }
            .mapValues { entry ->
                entry.value.sortedByDescending { it.timeInMiles }
            }
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
            "timeInMiles" to transaction.timeInMiles,
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
                    fetchTransactions()
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(3000)
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
        Log.e("juhygfd", "deleteTransactionDb:$transactionId -> $dataToEdit")
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
                fetchTransactions()
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
        super.handleInternalEvent(event)
        when (event) {
            is Event.TransactionsViewModel -> {
                fetchTransactions()
            }

            else -> {}
        }
    }


}