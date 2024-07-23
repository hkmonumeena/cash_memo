package com.ruchitech.cashentery.ui.screens.common_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.ui.screens.add_transactions.Transaction
import com.ruchitech.cashentery.ui.theme.TempColor
import com.ruchitech.cashentery.ui.theme.TempColor2
import com.ruchitech.cashentery.ui.theme.TempColor3
import com.ruchitech.cashentery.ui.theme.montserrat_semibold
import com.ruchitech.cashentery.ui.theme.nonScaledSp
import java.util.Locale

@Composable
fun PaymentTypeSwitch(
    modifier: Modifier,
    initialType: Transaction.Type,
    onTypeChange: (type: Transaction.Type, typeText: String) -> Unit,
) {
    var isCredit by remember { mutableStateOf(initialType == Transaction.Type.CREDIT) }
    Row(
        modifier = modifier.background(
            Color(0xFFDACB9F),
            shape = RoundedCornerShape(10.dp)
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (isCredit) TempColor3 else Color(0xFFDACB9F),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isCredit = true
                    onTypeChange(Transaction.Type.CREDIT, "Credit")
                }) {
            Text(
                text = " Credit ".uppercase(Locale.getDefault()),
                fontSize = 14.sp.nonScaledSp,
                modifier = Modifier.padding(10.dp),
                fontFamily = montserrat_semibold,
            )
        }
        Box(
            modifier = Modifier
                .background(
                    if (!isCredit) TempColor2 else Color(0xFFDACB9F),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isCredit = false
                    onTypeChange(Transaction.Type.DEBIT, "Debit")
                }) {
            Text(
                text = "  Debit ".uppercase(Locale.ROOT),
                fontSize = 14.sp.nonScaledSp,
                fontFamily = montserrat_semibold,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}


@Composable
fun TransactionAccountSwitch(
    modifier: Modifier,
    initialType: Transaction.Account,
    onTypeChange: (type: Transaction.Account, typeText: String) -> Unit,
) {
    var accountType by remember {
        mutableStateOf(initialType == Transaction.Account.ONLINE)
    }
    Row(
        modifier = modifier.background(
            Color(0xFFDACB9F),
            shape = RoundedCornerShape(10.dp)
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (accountType) TempColor else Color(0xFFDACB9F),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    accountType = true
                    onTypeChange(Transaction.Account.ONLINE, "ONLINE")
                }) {
            Text(
                text = "ONLINE",
                fontSize = 14.sp.nonScaledSp,
                fontFamily = montserrat_semibold,
                modifier = Modifier.padding(10.dp)
            )
        }
        Box(
            modifier = Modifier
                .background(
                    if (!accountType) TempColor else Color(0xFFDACB9F),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    accountType = false
                    onTypeChange(Transaction.Account.CASH, " CASH ")
                }) {
            Text(
                text = " CASH ",
                fontSize = 14.sp.nonScaledSp,
                modifier = Modifier.padding(10.dp),
                fontFamily = montserrat_semibold,
            )
        }
    }

}



@Composable
fun TransactionStatusSwitch(
    modifier: Modifier,
    initialType: Transaction.Status,
    onTypeChange: (type: Transaction.Status, typeText: String) -> Unit,
) {
    var accountType by remember {
        mutableIntStateOf(when(initialType){
            Transaction.Status.PENDING -> 1
            Transaction.Status.CLEARED -> 2
            Transaction.Status.OVERDUE -> 3
            Transaction.Status.VOID -> 4
        })
    }
    Row(
        modifier = modifier.background(
            Color(0xFFDACB9F),
            shape = RoundedCornerShape(10.dp)
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (accountType==2) Color(0xFF4CAF50).copy(alpha = 0.8F) else Color(0xFFDACB9F),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    accountType = 2
                    onTypeChange(Transaction.Status.CLEARED, "CLEARED")
                }) {
            Text(
                text = "CLEARED",
                fontSize = 14.sp.nonScaledSp,
                fontFamily = montserrat_semibold,
                modifier = Modifier.padding(10.dp)
            )
        }
        Box(
            modifier = Modifier
                .background(
                    if (accountType==1) Color(0xFFFF9800).copy(alpha = 0.8F) else Color(0xFFDACB9F),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    accountType = 1
                    onTypeChange(Transaction.Status.PENDING, "PENDING")
                }) {
            Text(
                text = "PENDING",
                fontSize = 14.sp.nonScaledSp,
                modifier = Modifier.padding(10.dp),
                fontFamily = montserrat_semibold,
            )
        }
        Box(
            modifier = Modifier
                .background(
                    if (accountType==3) Color(0xFFF44336).copy(alpha = 0.8F) else Color(0xFFDACB9F),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    accountType = 3
                    onTypeChange(Transaction.Status.OVERDUE, "OVERDUE")
                }) {
            Text(
                text = "OVERDUE",
                fontSize = 14.sp.nonScaledSp,
                modifier = Modifier.padding(10.dp),
                fontFamily = montserrat_semibold,
            )
        }

        Box(
            modifier = Modifier
                .background(
                    if (accountType==4) Color(0xFF9E9E9E).copy(alpha = 0.8F) else Color(0xFFDACB9F),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    accountType = 4
                    onTypeChange(Transaction.Status.VOID, "VOID")
                }) {
            Text(
                text = "    VOID    ",
                fontSize = 14.sp.nonScaledSp,
                modifier = Modifier.padding(10.dp),
                fontFamily = montserrat_semibold,
            )
        }

    }

}