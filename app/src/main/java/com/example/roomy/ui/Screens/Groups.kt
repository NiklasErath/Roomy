package com.example.roomy.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.roomy.ui.Screens

@Composable
fun Groups(
    navController: NavController
){

    Column (
        Modifier.fillMaxSize()
    ){
        Text(text="Groups Page")
        Button(onClick = {navController.navigate(Screens.Home.name)}) {
            Text(text="Select and enter Group")
        }
    }


}