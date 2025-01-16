package com.example.roomy.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.roomy.db.api.getSuggestedRecipe
import com.example.roomy.ui.States.ItemsUiState
import com.example.roomy.ui.ViewModels.ItemViewModel
import com.example.roomy.ui.ViewModels.UserViewModel

@Composable
fun RecipeSuggestion(
    navController: NavController,
    itemViewModel: ItemViewModel
) {

    var recipe by remember { mutableStateOf("Fetching Recipe ...") }

    val shoppingListItems by itemViewModel.allInventoryItems.collectAsState(
        initial = ItemsUiState(emptyList())
    )
    val shoppingListItemsNames = shoppingListItems.items.map { it.name }


    LaunchedEffect(shoppingListItemsNames) {
        if (shoppingListItemsNames.isNotEmpty()) {
            getSuggestedRecipe(shoppingListItemsNames) { fetchedRecipe ->
                recipe = fetchedRecipe
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(bottom = 50.dp)
            .verticalScroll(rememberScrollState())
    ) {


        Text(text = recipe)
    }


}
