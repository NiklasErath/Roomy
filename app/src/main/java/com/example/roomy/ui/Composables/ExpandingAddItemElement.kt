package com.example.roomy.ui.Composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun ExpandingAddItemElement(
    animatedHeight: Dp,
    isExpanded: Boolean,
    isFocused: Boolean,
    focusRequester: FocusRequester,
    itemName: String,
    updateFocusState: (Boolean) -> Unit,
    updateExpandState: (Boolean) -> Unit,
    updtaeItemName: (String) -> Unit
) {

    Box(
        Modifier
            .fillMaxSize()
            .height(animatedHeight)
            .zIndex(3f)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.background)
                .border(1.dp, Color.White)
                .zIndex(3f)
                .clickable { }, // Ensure this column is rendered above other elements
            verticalArrangement = if (isExpanded || isFocused) Arrangement.SpaceBetween else Arrangement.Bottom
        ) {
//            Spacer(Modifier.height(0.dp))
            // Additional Content at the top
            if (isExpanded) {
                Text(
                    text = "Additional Content Shown in Overlay",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // TextField at the bottom
            Box(
                Modifier
                    .fillMaxWidth()
                    .zIndex(3f)
                    .clickable { }// Ensure the TextField appears above the overlay
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            updateFocusState(focusState.isFocused)
                            if (focusState.isFocused) updateExpandState(true)
                        },
                    value = itemName,
                    onValueChange = { newValue -> updtaeItemName(newValue) },
                    placeholder = { Text(text = "We could use ...") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = "Add Item",
                            Modifier.clickable {
                                updateExpandState(true)                            // Logic to add items
                            }
                        )
                    }
                )
            }
        }
    }
}