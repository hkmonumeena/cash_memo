package com.ruchitech.cashentery.ui.screens.common_ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruchitech.cashentery.ui.theme.nonScaledSp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagSelectionChips(
    availableTags: List<String> = listOf(),
    selectedTags: List<String> = listOf(),
    onTagSelectionChange: (List<String>) -> Unit,
) {
    val selectedItems = remember { mutableStateOf(selectedTags.toMutableSet()) }
    var searchQuery by remember { mutableStateOf("") }

    // Filter tags based on search query
    val filteredTags = remember(searchQuery) {
        availableTags.filter { tag ->
            tag.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Search bar
        CustomSearchBar(query = searchQuery, onQueryChange = { newQuery ->
            searchQuery = newQuery
        }, onSearch = {}, onClear = {
            searchQuery = ""

        }, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp)
        )

        SpacerHeight(10)

        if (filteredTags.isEmpty()) {
            Text(
                text = "No tags found",
                fontSize = 16.sp.nonScaledSp,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                modifier = Modifier.padding(16.dp)
            )
        } else {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                filteredTags.forEach { tag ->
                    FilterChip(selected = selectedItems.value.contains(tag),
                        onClick = {
                            if (selectedItems.value.contains(tag)) {
                                selectedItems.value.remove(tag)
                            } else {
                                selectedItems.value.add(tag)
                            }
                            onTagSelectionChange(selectedItems.value.toList())
                        },
                        label = { Text(tag,fontSize = 14.sp.nonScaledSp,) },
                        leadingIcon = {
                            if (selectedItems.value.contains(tag)) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = if (selectedItems.value.contains(tag)) Color(0xFFCCFFCC) else Color.White,
                            // selectedContainerColor = Color(0xFFCCFFCC),
                            selectedLabelColor = Color.Black, labelColor = Color.Gray
                        )
                    )
                }
            }
        }
    }
}
