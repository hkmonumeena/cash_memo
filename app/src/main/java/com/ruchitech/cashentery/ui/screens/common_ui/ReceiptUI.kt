package com.ruchitech.cashentery.ui.screens.common_ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.ruchitech.cashentery.MainActivity
import com.ruchitech.cashentery.helper.getCurrentDateWithTime
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import com.ruchitech.cashentery.ui.screens.home.formatToINR
import com.ruchitech.cashentery.ui.theme.nonScaledSp
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


@OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeApi::class)
@Composable
fun ReceiptUI(
    transaction: Transaction,
    onShareClick: (String, Uri) -> Unit
) {
    val context = LocalContext.current as MainActivity
    var captureNow by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val captureController = rememberCaptureController()


    Column(
        modifier = Modifier
            .capturable(captureController)
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Payment Receipt",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        DividerLine()

        // Receipt Details
        SpacerHeight(5)
        BasicText(
            text = "Transaction ID: ${transaction.id}",
            style = receiptTextStyle()
        )
        SpacerHeight(10)
        BasicText(
            text = "Date: ${transaction.date}",
            style = receiptTextStyle()
        )
        SpacerHeight(10)
        BasicText(
            text = "Tag: ${transaction.tag ?: "None"}",
            style = receiptTextStyle()
        )
        SpacerHeight(10)
        BasicText(
            text = "Type: ${transaction.type?.name}",
            style = receiptTextStyle()
        )
        SpacerHeight(10)
        BasicText(
            text = "Account: ${transaction.account?.name}",
            style = receiptTextStyle()
        )
        SpacerHeight(10)
        BasicText(
            text = "Status: ${transaction.status.name}",
            style = receiptTextStyle()
        )
        SpacerHeight(10)
  /*      BasicText(
            text = "Amount: ${transaction.amount}",
            style = receiptTextStyle()
        )*/
        SpacerHeight(10)
        BasicText(
            text = "Remarks: ${transaction.remarks ?: "None"}",
            style = receiptTextStyle()
        )
        SpacerHeight(10)
        DividerLine()
        TotalAmountRow(totalAmount = formatToINR(transaction.amount?:0.0))
        DividerLine()
        // Footer
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Created at: ${getCurrentDateWithTime()}",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Gray
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
            )


            Text(
                text = "By:    Cash Entry",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Share Button
        if (!captureNow) {
            Button(
                onClick = {
                    captureNow = true
                    scope.launch {
                        val bitmapAsync = captureController.captureAsync()
                        try {
                            bitmap = bitmapAsync.await()
                            // Do something with `bitmap`.
                            bitmap?.let {
                                transaction.id?.let { id -> shareReceipt(context, it, id) }
                            }
                        } catch (error: Throwable) {
                            // Handle error
                        } finally {
                            captureNow = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Share Receipt")
            }
        }
    }
}
@Composable
fun TotalAmountRow(totalAmount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Total Amount:",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = Color.Black
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = totalAmount,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = Color.Black
            ),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun DividerLine() {
    BasicText(
        text = "-".repeat(100),
        maxLines = 1,
        style = receiptTextStyle(),
        modifier = Modifier.fillMaxWidth()
    )
}

fun receiptTextStyle() = TextStyle(
    fontSize = 18.sp,
    fontFamily = FontFamily.Monospace,
    color = Color.Black,

)

fun shareReceipt(context: Context, imageBitmap: ImageBitmap, transactionId: String) {
    // Convert ImageBitmap to Bitmap
    val bitmap = imageBitmap.asAndroidBitmap()

    // Save the Bitmap to a temporary file
    val file = File(context.getExternalFilesDir(null), "receipt_$transactionId.png")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    // Get a URI for the file
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    // Create the share intent
    val shareIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, "Here is the receipt for transaction ID: $transactionId")
        type = "image/png"
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    // Launch the share intent
    context.startActivity(Intent.createChooser(shareIntent, "Share Receipt Image"))
}


