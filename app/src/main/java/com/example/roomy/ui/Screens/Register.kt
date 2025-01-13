package com.example.roomy.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.roomy.ui.Screens

@Composable
fun Register(
    modifier: Modifier = Modifier,
    navController: NavController
){

    Column {
        Text(text="Register PAge")
    }
    Button(onClick = {navController.navigate(Screens.Groups.name)}) {
        Text(text="Sign Up")
    }

}