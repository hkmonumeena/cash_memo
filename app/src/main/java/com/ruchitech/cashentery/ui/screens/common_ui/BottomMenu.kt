@file:OptIn(ExperimentalMaterial3Api::class)

package com.ruchitech.cashentery.ui.screens.common_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.R
import com.ruchitech.cashentery.ui.theme.MainBackgroundSurface
import com.ruchitech.cashentery.ui.theme.nonScaledSp
import com.ruchitech.cashentery.ui.theme.roboto_medium
import com.ruchitech.cashentery.ui.theme.sfSemibold

@Composable
fun BottomMenu(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onNavigate: (String) -> Unit, // Callback to handle navigation or actions
) {
    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest()
        },
        sheetState = sheetState,
        dragHandle = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(MainBackgroundSurface),
                contentAlignment = Alignment.Center
            ) {
                BottomSheetDefaults.DragHandle()
                HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter))
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.8F)
        ) {
            val menuItems = listOf(
      /*          MenuItemData(
                    text = "Profile",
                    icon = R.drawable.ic_profile
                ),*/
              /*  MenuItemData(
                    text = "Settings",
                    icon =R.drawable.ic_settings
                ),*/
                MenuItemData(
                    text = "Terms and Conditions",
                    icon = R.drawable.terms_cond
                ),
                MenuItemData(
                    text = "Privacy Policy",
                    icon = R.drawable.privacy_policy
                ),
                MenuItemData(
                    text = "Sign Out",
                    icon = R.drawable.signout
                ),
                MenuItemData(
                    text = "Delete Account",
                    icon = R.drawable.delete_account
                )
            )

            LazyColumn {
                item {
                    Text(
                        text = "Menu",
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                        fontSize = 16.sp.nonScaledSp,
                        fontFamily = sfSemibold
                    )
                }
                items(menuItems) { item ->
                    MenuItem(item) {
                        onNavigate(item.text)
                    }
                }
            }

        }
    }
}

@Composable
fun MenuItem(item: MenuItemData, onClick: () -> Unit) {
    Column(modifier = Modifier.clickable {
        onClick()
    }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(25.dp)
                    .background(Color(0xFFDBDBDB), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = item.icon),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(15.dp),
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.text,
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = roboto_medium,
                    color = Color(0xFF363636)
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.Gray
                )

            }
        }
        HorizontalDivider(modifier = Modifier.padding(start = 55.dp, end = 24.dp))
    }
}


data class MenuItemData(
    val text: String,
    val icon: Int,
)
