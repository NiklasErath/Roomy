package com.example.roomy.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun Home(
    modifier: Modifier = Modifier,
    navController: NavController
){

    Column (
        modifier.fillMaxSize()
    ){
        Text(text="Home / ShoppingList Page")
    }
    Button(onClick = {navController.navigate(Screens.Groups.name)}) {
        Text(text="Back to Groups")
    }

}