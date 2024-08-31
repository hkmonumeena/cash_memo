package com.ruchitech.cashentery.ui.screens

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.ruchitech.cashentery.helper.RequestState
import com.ruchitech.cashentery.helper.sharedpreference.AppPreference
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

class Repository @Inject constructor(
    private val appPreference: AppPreference,
   private val db: FirebaseFirestore,
) {

    fun fetchAllTransactions(): Flow<RequestState<List<Transaction>>> {
        return flow {
            emit(RequestState.Loading)
            try {
                val result = Tasks.await(
                    db.collection("users")
                        .document(appPreference.userId ?: "")
                        .collection("transactions")
                        .get()
                )
                val transactions = result.documents.map { document ->
                    document.toObject(Transaction::class.java)?.copy(id = document.id,authId = appPreference.userId)
                }.filterNotNull()
                emit(RequestState.Success(transactions))
            } catch (exception: Exception) {
                emit(RequestState.Error(exception.message ?: "Unknown error"))
            }
        }.flowOn(Dispatchers.IO) // Ensure the flow runs on the IO dispatcher
    }
}

fun writeTransactionsToJsonFile(context: Context, transactions: List<Transaction>, fileName: String) {
    // Create a JSON string from the list of transactions
    val jsonString = Json.encodeToString(transactions)

    Log.e("kljhughuyil", "$jsonString", )

    // Get the Downloads directory
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    // Create the file in the Downloads directory
    val file = File(downloadsDir, fileName)

    // Write the JSON string to the file
    file.writeText(jsonString)

    println("Transactions have been written to ${file.absolutePath}")
}