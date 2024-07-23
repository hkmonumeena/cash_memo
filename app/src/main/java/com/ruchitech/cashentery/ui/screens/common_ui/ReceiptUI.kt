package com.ruchitech.cashentery.ui.screens.common_ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.icu.text.DateFormat
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import java.io.File
import java.util.Date

@Composable
fun ReceiptUI(
    transaction: Transaction,
    onShareClick: (String, Uri) -> Unit
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var captureNow by remember {
        mutableStateOf(false)
    }
    var uri by remember {
        mutableStateOf<Uri?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Receipt",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Transaction ID: ${transaction.id}")
        Text("Date: ${transaction.date}")
        Text("Time: ")  //${DateFormat.format("HH:mm:ss", Date(transaction.timeInMillis))}
        Text("Type: ${transaction.type?.name}")
        Text("Account: ${transaction.account?.name}")
        Text("Transaction Number: ${transaction.transactionNumber}")
        Text("Amount: ${transaction.amount}")
        Text("Remarks: ${transaction.remarks ?: "None"}")
        Text("Tag: ${transaction.tag ?: "None"}")
        Text("Status: ${transaction.status.name}")
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                captureNow = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Share Receipt")
        }

        CaptureAsImage(
            content = {
                // The content to be captured
                ReceiptUI(transaction = transaction, onShareClick = { text, uri ->
                    Log.e("jfglgfg", "ReceiptUI:$text $uri")
                })
            },
            onImageCaptured = { uri ->
                Log.e("jfglgfg", "ReceiptUI: $uri")
                imageUri = uri
            }
        )

        if (captureNow){


            LaunchedEffect(captureNow) {
              if (imageUri!=null){
                  val shareIntent = Intent().apply {
                      action = Intent.ACTION_SEND
                      putExtra(Intent.EXTRA_TEXT, "Transaction ID: ${transaction.id}\nDate: ${transaction.date}\nAmount: ${transaction.amount}")
                      putExtra(Intent.EXTRA_STREAM, imageUri)
                      type = "image/png"
                  }
                  context.startActivity(Intent.createChooser(shareIntent, "Share Receipt"))
              }
            }

        }
    }
}

@Composable
fun CaptureAsImage(content: @Composable () -> Unit, onImageCaptured: (Uri) -> Unit) {
    val context = LocalContext.current
    val bitmap = remember { Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) }
    val canvas = remember { Canvas(bitmap) }

    // Capture the content of the composable
    val drawable = remember { BitmapDrawable(context.resources, bitmap) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    LaunchedEffect(Unit) {
        val imageCaptureFile = File(context.cacheDir, "receipt_image.png")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(imageCaptureFile).build()
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val uri = Uri.fromFile(imageCaptureFile)
                onImageCaptured(uri)
            }

            override fun onError(exception: ImageCaptureException) {
                // Handle error
                Log.e("jfglgfg", "onError: $exception")
            }
        })
    }
}

