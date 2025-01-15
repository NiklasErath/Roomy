package com.example.roomy.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun Balance(
    navController: NavController
){

    Column (
        Modifier.fillMaxSize().padding(horizontal = 20.dp)
    ){
        Text(text="Finance/Balance Page")
        Button(onClick = {navController.navigate(Screens.Home.name)}) {
            Text(text="Back to Groups")
        }
    }


}