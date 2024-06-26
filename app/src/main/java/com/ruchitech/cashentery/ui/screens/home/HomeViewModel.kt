package com.ruchitech.cashentery.ui.screens.home

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ruchitech.cashentery.ui.screens.add_transactions.AddTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _transactionsFlow = MutableStateFlow<List<AddTransaction>>(emptyList())
    val transactionsFlow: StateFlow<List<AddTransaction>> = _transactionsFlow

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        fetchTransactions()
    }

    private fun fetchTransactions() {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            println("User is not authenticated.")
            _transactionsFlow.value = emptyList()
            return
        }

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("transactions").child(userId)

        myRef.get().addOnSuccessListener { snapshot ->
            val transactions = mutableListOf<AddTransaction>()
            for (childSnapshot in snapshot.children) {
                val jsonString = childSnapshot.getValue(String::class.java)
                if (jsonString != null) {
                    val transaction = Json.decodeFromString<AddTransaction>(jsonString)
                    transactions.add(transaction)
                }
            }
            _transactionsFlow.value = transactions
        }.addOnFailureListener { e ->
            println("Error retrieving transactions: ${e.message}")
            _transactionsFlow.value = emptyList()
        }
    }
}