package com.ruchitech.cashentery.ui.screens.transactions

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.ruchitech.cashentery.helper.Event
import com.ruchitech.cashentery.helper.SharedViewModel
import com.ruchitech.cashentery.ui.screens.add_transactions.AddTransactionData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor() : SharedViewModel() {
    private val _transactionsFlow = MutableStateFlow<List<AddTransactionData>>(emptyList())
    val transactionsFlow: StateFlow<List<AddTransactionData>> = _transactionsFlow
    private val _groupByTag = MutableStateFlow<Map<String?, List<AddTransactionData>>?>(null)
    val groupByTag: StateFlow<Map<String?, List<AddTransactionData>>?> = _groupByTag
    private val db = FirebaseFirestore.getInstance()

    init {
        fetchTransactions()
    }

    private fun fetchTransactions() {
        val userId = "W5mzbR4YFSTClH6Tsf28LilEH9d2" //auth.currentUser?.uid

        if (userId == null) {
            println("User is not authenticated.")
            _transactionsFlow.value = emptyList()
            return
        }

        db.collection("users").document(userId).collection("transactions")
            .get()
            .addOnSuccessListener { result ->

                val transactionList = result.toObjects(AddTransactionData::class.java)
                //   transactions.value = transactionList
                _transactionsFlow.value = transactionList.sortedByDescending { it.timeInMiles }
                /*   _groupByTag.value = transactions.sortedByDescending { it.timeInMiles }.groupBy { it.timeInMiles.toString() }.mapValues { entry ->
                       entry.value.sortedByDescending { it.timeInMiles }
                   }*/
                _groupByTag.value = groupTransactionsByDate(transactionList)
            }
            .addOnFailureListener { exception ->
                Log.e("MyViewModel", "Error getting transactions: ${exception.message}")
                _transactionsFlow.value = emptyList()
            }
    }

    private fun groupTransactionsByDate(transactions: List<AddTransactionData>): Map<String?, List<AddTransactionData>> {
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