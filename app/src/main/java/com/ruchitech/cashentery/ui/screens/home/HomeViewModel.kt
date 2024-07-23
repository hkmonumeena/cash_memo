package com.ruchitech.cashentery.ui.screens.home

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ruchitech.cashentery.helper.Event
import com.ruchitech.cashentery.helper.SharedViewModel
import com.ruchitech.cashentery.helper.sharedpreference.AppPreference
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
) : SharedViewModel() {
    private val _transactionsFlow = MutableStateFlow<List<Transaction>>(emptyList())
    val transactionsFlow: StateFlow<List<Transaction>> = _transactionsFlow

    private val _groupByTag = MutableStateFlow<Map<String?, List<Transaction>>?>(null)
    val groupByTag: StateFlow<Map<String?, List<Transaction>>?> = _groupByTag

    private val _sumOfExpense = MutableStateFlow<Double?>(0.0)
    val sumOfExpense: StateFlow<Double?> = _sumOfExpense

    private val _sumOfIncome = MutableStateFlow<Double?>(0.0)
    val sumOfIncome: StateFlow<Double?> = _sumOfIncome

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    var transactions = ArrayList<Transaction>()

    init {
        //appPreference.userId =  "monumeenatest" //"W5mzbR4YFSTClH6Tsf28LilEH9d2"
        fetchTransactions()
    }

    fun updateData() {
        fetchTransactions()
    }

    private fun fetchTransactions() {
        val userId = appPreference.userId // "W5mzbR4YFSTClH6Tsf28LilEH9d2" //auth.currentUser?.uid
        if (userId == null) {
            println("User is not authenticated.")
            _transactionsFlow.value = emptyList()
            return
        }

        db.collection("users").document(userId).collection("transactions").get()
            .addOnSuccessListener { result ->
                transactions.clear()
                val transactionList = result.toObjects(Transaction::class.java)
                result.forEach { document ->
                    val transaction =
                        document.toObject(Transaction::class.java).copy(document.id)
                    //transaction.id = document.id
                    transactions.add(transaction)
                    val documentId = document.id // <--- Get the document ID here
                }

                _sumOfExpense.value =
                    transactions.filter { it.type == Transaction.Type.DEBIT }.sumOf { it.amount ?: 0.0 }

                _sumOfIncome.value =
                    transactions.filter { it.type == Transaction.Type.CREDIT }.sumOf { it.amount ?: 0.0 }

                _transactionsFlow.value = transactions.sortedByDescending { it.timeInMillis }
                _groupByTag.value = transactions.groupBy {
                    it.tag
                }.mapValues { entry ->
                    entry.value.sortedByDescending { it.timeInMillis }
                }
                appPreference.categoriesList = transactions.map { it.tag?:"" }.distinct().toList()
            }.addOnFailureListener { exception ->
                Log.e("MyViewModel", "Error getting transactions: ${exception.message}")
            }
    }

    private fun updateTransaction(updatedTransaction: Transaction) {
        // Find the index of the transaction to be updated
        val index = transactions.indexOfFirst { it.id == updatedTransaction.id }

        // If the transaction exists, update it
        if (index != -1) {
            transactions[index] = updatedTransaction
        }

        _sumOfExpense.value =
            transactions.filter { it.type == Transaction.Type.DEBIT }.sumOf { it.amount ?: 0.0 }

        _sumOfIncome.value =
            transactions.filter { it.type == Transaction.Type.CREDIT }.sumOf { it.amount ?: 0.0 }

        _transactionsFlow.value = transactions.sortedByDescending { it.timeInMillis }

        _groupByTag.value = transactions.sortedByDescending { it.timeInMillis }.groupBy { it.tag }
    }

    private fun deleteTransaction(deleteId: String) {
        // Find the index of the transaction to be deleted
        val index = transactions.indexOfFirst { it.id == deleteId }

        // If the transaction exists, remove it
        if (index != -1) {
            transactions.removeAt(index)
        }

        _sumOfExpense.value =
            transactions.filter { it.type == Transaction.Type.DEBIT }.sumOf { it.amount ?: 0.0 }

        _sumOfIncome.value =
            transactions.filter { it.type == Transaction.Type.CREDIT }.sumOf { it.amount ?: 0.0 }

        _transactionsFlow.value = transactions.sortedByDescending { it.timeInMillis }

        _groupByTag.value = transactions.sortedByDescending { it.timeInMillis }.groupBy { it.tag }
    }

    fun signout(){
        auth.signOut()
        appPreference.isUserLoggedIn = false
        appPreference.userId =null
    }


    override fun handleInternalEvent(event: Event) {
        super.handleInternalEvent(event)
        when (event) {
            is Event.HomeViewModel -> {
                if (event.transaction != null) {
                    updateTransaction(event.transaction)
                }
                if (!event.deleteId.isNullOrEmpty()) {
                    deleteTransaction(event.deleteId)
                }
                if (event.refreshPage) {
                    fetchTransactions()
                }
            }

            is Event.TransactionDetailsViewModel -> Unit
            is Event.TransactionsViewModel -> Unit
        }
    }

}