package com.ruchitech.cashentery.ui.screens.home

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("transactions").child(userId)

        myRef.get().addOnSuccessListener { snapshot ->
            val transactions = mutableListOf<AddTransactionData>()
            for (childSnapshot in snapshot.children) {
                val jsonString = childSnapshot.getValue(String::class.java)
                if (jsonString != null) {
                    val transaction = Json.decodeFromString<AddTransactionData>(jsonString)
                    transactions.add(transaction)
                }
            }
            _sumOfExpense.value =
                transactions.filter { it.type == Type.DEBIT }.sumOf { it.amount ?: 0.0 }

            _sumOfIncome.value =
                transactions.filter { it.type == Type.CREDIT }.sumOf { it.amount ?: 0.0 }

            _transactionsFlow.value = transactions.sortedByDescending { it.timeInMiles }
            _groupByTag.value = transactions.groupBy { it.tag }.mapValues { entry ->
                entry.value.sortedByDescending { it.timeInMiles }
            }

        }.addOnFailureListener { e ->
            println("Error retrieving transactions: ${e.message}")
            _transactionsFlow.value = emptyList()
        }
    }
}