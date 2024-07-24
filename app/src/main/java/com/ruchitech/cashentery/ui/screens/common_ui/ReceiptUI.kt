package com.ruchitech.cashentery.ui.screens.common_ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.ruchitech.cashentery.MainActivity
import com.ruchitech.cashentery.helper.getCurrentDateWithTime
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import com.ruchitech.cashentery.ui.screens.home.formatToINR
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


@OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeApi::class)
@Composable
fun ReceiptUI(
    transaction: Transaction,
    onShareClick: (String, Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current as MainActivity
    var captureNow by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val captureController = rememberCaptureController()


    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ZigzagBorderBox(
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 16.dp)
                .capturable(captureController)
                .background(Color(0x33CBCBCB))
                .padding(horizontal = 44.dp, vertical = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .background(Color.White)

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

                Text(
                    text = "Name: John Doe",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Black,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
                SpacerHeight(4)
                Text(
                    text = "Mobile: +1 234 567 890",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Black,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
                SpacerHeight(8)
                DividerLine()
                // Receipt Details
                SpacerHeight(5)
                ReceiptText(label = "Txn ID:", value = transaction.id ?: "N/A")
                SpacerHeight(10)
                ReceiptText(label = "Date:", value = transaction.date ?: "N/A")
                SpacerHeight(10)
                ReceiptText(label = "Tag:", value = transaction.tag ?: "None")
                SpacerHeight(10)
                ReceiptText(label = "Type:", value = transaction.type?.name ?: "N/A")
                SpacerHeight(10)
                ReceiptText(label = "Account:", value = transaction.account?.name ?: "N/A")
                SpacerHeight(10)
                ReceiptText(label = "Status:", value = transaction.status.name)
                SpacerHeight(10)
                ReceiptText(label = "Remarks:", value = transaction.remarks ?: "None")
                SpacerHeight(10)
                DividerLine()
                TotalAmountRow(totalAmount = formatToINR(transaction.amount ?: 0.0))
                DividerLine()
                // Footer
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
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
            }
        }

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 60.dp)
        ) {
            Text(text = "Share Receipt")
        }

        SpacerHeight(20)

        Box(modifier = Modifier.background(Color.Red, shape = CircleShape)) {
            IconButton(onClick = {
                onDismiss()
            }) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                )
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

@Composable
fun ReceiptText(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BasicText(
            text = label,
            style = receiptTextStyle(),
            modifier = Modifier.weight(0.8F)
        )
        BasicText(
            text = value,
            style = receiptTextStyle(),
            modifier = Modifier.weight(2.1f)
        )
    }
}

fun receiptTextStyle() = TextStyle(
    fontSize = 18.sp,
    fontFamily = FontFamily.Monospace,
    color = Color.Black
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


