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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.ruchitech.cashentery.MainActivity
import com.ruchitech.cashentery.helper.getCurrentDateWithTime
import com.ruchitech.cashentery.helper.numberToWords
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
    onShareClick: (String, Uri) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var captureNow by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val captureController = rememberCaptureController()

    // State to manage settings dialog visibility
    var isSettingsDialogVisible by remember { mutableStateOf(false) }

    // State to manage which items to show on the receipt
    var settings by remember {
        mutableStateOf(
            ReceiptSettings(
                showName = true,
                showMobile = true,
                showTxnID = true,
                showDate = true,
                showTag = true,
                showType = true,
                showAccount = true,
                showStatus = true,
                showRemarks = true,
                showCreatedDate = true
            )
        )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ZigzagBorderBox(
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 16.dp)
                .capturable(captureController)
                .background(Color(0x33CBCBCB))
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .background(Color.White)

            ) {
                // Header
                Text(
                    text = "PAYMENT RECEIPT",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                SpacerHeight(8)

                if (settings.showName) {
                    Text(
                        text = "Name: John Doe",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (settings.showMobile) {
                    SpacerHeight(4)
                    Text(
                        text = "Mobile: +1 234 567 890",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                SpacerHeight(8)
                DividerLineStars()
                // Receipt Details
                SpacerHeight(5)
                if (settings.showTxnID) {
                    ReceiptText(label = "Txn ID:", value = transaction.id ?: "N/A")
                    SpacerHeight(10)
                }
                if (settings.showDate) {
                    ReceiptText(label = "Date:", value = transaction.date ?: "N/A")
                    SpacerHeight(10)
                }
                if (settings.showTag) {
                    ReceiptText(label = "Tag:", value = transaction.tag ?: "None")
                    SpacerHeight(10)
                }
                if (settings.showType) {
                    ReceiptText(label = "Type:", value = transaction.type?.name ?: "N/A")
                    SpacerHeight(10)
                }
                if (settings.showAccount) {
                    ReceiptText(label = "Account:", value = transaction.account?.name ?: "N/A")
                    SpacerHeight(10)
                }
                if (settings.showStatus) {
                    ReceiptText(label = "Status:", value = transaction.status.name)
                    SpacerHeight(10)
                }
                if (settings.showRemarks) {
                    ReceiptText(label = "Remarks:", value = transaction.remarks ?: "None")
                    SpacerHeight(10)
                }
                DividerLine()
                TotalAmountRow(totalAmount = formatToINR(transaction.amount ?: 0.0))
                Text(
                    text = "${numberToWords(transaction.amount?.toLong() ?: 0L)}",
                    fontSize = 14.sp.nonScaledSp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Black,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)
                )
                SpacerHeight(8)
                DividerLine()
                // Footer
                if (settings.showCreatedDate) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Created at: ${getCurrentDateWithTime()}",
                            style = TextStyle(
                                fontSize = 12.sp.nonScaledSp,
                                fontFamily = FontFamily.SansSerif,
                                color = Color.Gray
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                        )


                        Text(
                            text = "By: Cash Entry",
                            style = TextStyle(
                                fontSize = 12.sp.nonScaledSp,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    DividerLineStars()
                    SpacerHeight(10)
                    Text(
                        text = "Thank you!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceAround) {
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
                    .padding(horizontal = 10.dp)
                    .weight(1F)

            ) {
                Text(text = "Share Receipt")
            }

            Button(
                onClick = {
                    isSettingsDialogVisible = true // Show the settings dialog
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .weight(1F)
            ) {
                Text(text = "Settings")
            }

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

    if (isSettingsDialogVisible) {
        SettingsDialog(
            settings = settings,
            onDismiss = { isSettingsDialogVisible = false },
            onSave = { updatedSettings -> settings = updatedSettings }
        )
    }
}

@Composable
private fun TotalAmountRow(totalAmount: String) {
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
private fun DividerLine() {
    BasicText(
        text = "-".repeat(100),
        maxLines = 1,
        style = receiptTextStyle(),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun DividerLineStars() {
    BasicText(
        text = "*".repeat(100),
        maxLines = 1,
        style = receiptTextStyle(),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ReceiptText(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BasicText(
            text = label,
            style = receiptTextStyle(),
            modifier = Modifier.weight(0.5F)
        )
        BasicText(
            text = value,
            style = receiptTextStyle(),
            modifier = Modifier.weight(2f)
        )
    }
}

@Composable
private fun receiptTextStyle() = TextStyle(
    fontSize = 14.sp.nonScaledSp,
    fontFamily = FontFamily.Monospace,
    color = Color.Black
)


// Data class to hold settings state
data class ReceiptSettings(
    val showName: Boolean = true,
    val showMobile: Boolean = true,
    val showTxnID: Boolean = true,
    val showDate: Boolean = true,
    val showTag: Boolean = true,
    val showType: Boolean = true,
    val showAccount: Boolean = true,
    val showStatus: Boolean = true,
    val showRemarks: Boolean = true,
    val showCreatedDate: Boolean = true
)

// Settings dialog implementation
@Composable
fun SettingsDialog(
    settings: ReceiptSettings,
    onDismiss: () -> Unit,
    onSave: (ReceiptSettings) -> Unit
) {
    var localSettings by remember { mutableStateOf(settings) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Receipt Settings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                SpacerHeight(16)

                CheckboxRow("Show Name", localSettings.showName) { localSettings = localSettings.copy(showName = it) }
                CheckboxRow("Show Mobile", localSettings.showMobile) { localSettings = localSettings.copy(showMobile = it) }
                CheckboxRow("Show Transaction ID", localSettings.showTxnID) { localSettings = localSettings.copy(showTxnID = it) }
                CheckboxRow("Show Date", localSettings.showDate) { localSettings = localSettings.copy(showDate = it) }
                CheckboxRow("Show Tag", localSettings.showTag) { localSettings = localSettings.copy(showTag = it) }
                CheckboxRow("Show Type", localSettings.showType) { localSettings = localSettings.copy(showType = it) }
                CheckboxRow("Show Account", localSettings.showAccount) { localSettings = localSettings.copy(showAccount = it) }
                CheckboxRow("Show Status", localSettings.showStatus) { localSettings = localSettings.copy(showStatus = it) }
                CheckboxRow("Show Remarks", localSettings.showRemarks) { localSettings = localSettings.copy(showRemarks = it) }
                CheckboxRow("Show Created Date", localSettings.showCreatedDate) { localSettings = localSettings.copy(showCreatedDate = it) }

                SpacerHeight(16)

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        onSave(localSettings)
                        onDismiss()
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

// Helper function to create a checkbox row
@Composable
fun CheckboxRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(text = label, fontSize = 16.sp)
    }
}



private fun shareReceipt(context: Context, imageBitmap: ImageBitmap, transactionId: String) {
    val bitmap = imageBitmap.asAndroidBitmap()
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


