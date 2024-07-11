package com.ruchitech.cashentery.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.firestore.toObjects
import com.ruchitech.cashentery.helper.sharedpreference.AppPreference
import com.ruchitech.cashentery.ui.screens.add_transactions.AddTransactionData
import com.ruchitech.cashentery.ui.screens.add_transactions.Type
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
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
) : ViewModel() {
    private val _transactionsFlow = MutableStateFlow<List<AddTransactionData>>(emptyList())
    val transactionsFlow: StateFlow<List<AddTransactionData>> = _transactionsFlow

    private val _groupByTag = MutableStateFlow<Map<String?, List<AddTransactionData>>?>(null)
    val groupByTag: StateFlow<Map<String?, List<AddTransactionData>>?> = _groupByTag

    private val _sumOfExpense = MutableStateFlow<Double?>(0.0)
    val sumOfExpense: StateFlow<Double?> = _sumOfExpense

    private val _sumOfIncome = MutableStateFlow<Double?>(0.0)
    val sumOfIncome: StateFlow<Double?> = _sumOfIncome

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    init {
        appPreference.userId = "W5mzbR4YFSTClH6Tsf28LilEH9d2"
        fetchTransactions()
    }

    fun updateData(){
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
                val transactions = ArrayList<AddTransactionData>()

                val transactionList = result.toObjects(AddTransactionData::class.java)
                result.forEach { document ->
                    val transaction = document.toObject(AddTransactionData::class.java).copy(document.id)
                        //transaction.id = document.id
                    transactions.add(transaction)
                    val documentId = document.id // <--- Get the document ID here
                    Log.e("fdhfghfgh", "fetchTransactions:${document.id} ${transaction}")
                    println("Document ID: $documentId")
                    // Do something with the document ID
                }

                _sumOfExpense.value =
                    transactions.filter { it.type == Type.DEBIT }.sumOf { it.amount ?: 0.0 }

                _sumOfIncome.value =
                    transactions.filter { it.type == Type.CREDIT }.sumOf { it.amount ?: 0.0 }

                _transactionsFlow.value = transactions.sortedByDescending { it.timeInMiles }
                _groupByTag.value = transactions.groupBy { it.tag }.mapValues { entry ->
                    entry.value.sortedByDescending { it.timeInMiles }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MyViewModel", "Error getting transactions: ${exception.message}")
            }
    }
}