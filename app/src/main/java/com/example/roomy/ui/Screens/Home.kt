package com.example.roomy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.roomy.ui.States.GroupsUiState
import com.example.roomy.ui.States.ItemsUiState
import com.example.roomy.ui.ViewModels.GroupViewModel
import com.example.roomy.ui.ViewModels.ItemViewModel

var ListExample = listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)

@Composable
fun Home(
    groupViewModel: GroupViewModel,
    itemViewModel: ItemViewModel,
    navController: NavController
) {
    val rows = ListExample.chunked(3)
    var itemName by remember { mutableStateOf("") }


    val finishedFetching by itemViewModel.finishedFetching

    var renderAfterFetching by remember { mutableStateOf(false) }

    val currentGroup by groupViewModel.currentGroup.collectAsState()

    val inventoryItems by itemViewModel.allInventoryItems.collectAsState(
        initial = ItemsUiState(emptyList())
    )

    val shoppingListItems by itemViewModel.allShoppingListItems.collectAsState(
        initial = ItemsUiState(emptyList())
    )


    LaunchedEffect(Unit) {

        currentGroup.id?.let { itemViewModel.getAllItems(it) }
    }

    LaunchedEffect(finishedFetching){
        if(finishedFetching){
            renderAfterFetching = true
            itemViewModel.resetFinishedFetching()

        }
    }


    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = currentGroup.name)

        Button(onClick = { navController.navigate(Screens.Groups.name) }) {
            Text(text = "Back to Groups")
        }

        if(shoppingListItems.items.isEmpty() && renderAfterFetching){
            Text(text = "Nothing here yet, add a new Item to your Shopping List")
        }else if(renderAfterFetching) {

            shoppingListItems.items.chunked(3).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    rowItems.forEach { item ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Item(item)
                        }
                    }

                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .border(width = 1.dp, color = Color.White)
                    .clip(
                        shape = RoundedCornerShape(
                            topStart = 10.dp,
                            topEnd = 10.dp
                        )
                    ),
                value = itemName,
                onValueChange = { newValue -> itemName = newValue },
                placeholder = { Text(text = "We could use ...") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = "Add Item",
                        Modifier.clickable {

                            currentGroup.id?.let {
                                itemViewModel.addItem(
                                    itemName,
                                    it, "shoppingList", 1, "iconString"
                                )
                            }

                        })
                }
            )
        }
    }


}