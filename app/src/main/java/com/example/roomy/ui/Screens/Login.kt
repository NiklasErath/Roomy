package com.example.roomy.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Preview
@Composable
fun Login(
    modifier: Modifier = Modifier,
//    navController: NavController
){
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    Column(Modifier.fillMaxSize()) {
        Text(text="Login Page", fontSize = 30.sp)
        Text(text="Please sign in to continue", fontSize = 20.sp)
        OutlinedTextField(
            value = name,
            onValueChange = { newValue:String -> name = newValue },
            Modifier.fillMaxWidth(),
            label = { Text("Email") },
            leadingIcon = {Icon(imageVector = Icons.Filled.Email, contentDescription = "Email")},
            placeholder = {Text(text="example@outlook.com")}
            )

        OutlinedTextField(
            value = password,
            onValueChange = { newValue:String -> password = newValue },
            Modifier.fillMaxWidth(),
            label = { Text("Password") },
            leadingIcon = {Icon(imageVector = Icons.Filled.Lock, contentDescription = "Password")},
            visualTransformation =  PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),        )


        Button(onClick = {
//        navController.navigate(Screens.Groups.name)
        }) {
            Text(text="Log In")
        }

        Row (Modifier.fillMaxWidth()){
            Text(text = "Don't have an account?")
            Text(text = "Sign Up!")


        }
    }


}