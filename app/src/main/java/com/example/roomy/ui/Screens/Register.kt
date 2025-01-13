package com.example.roomy.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun Register(
    navController: NavController
){
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }



    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
        Text(text="Sign up", fontSize = 32.sp)
        Spacer(Modifier.height(100.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { newValue:String -> email = newValue },
            Modifier.fillMaxWidth(),
            label = { Text("Email") },
            leadingIcon = {Icon(imageVector = Icons.Filled.Email, contentDescription = "Email")},
            placeholder = {Text(text="example@outlook.com")}
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { newValue:String -> username = newValue },
            Modifier.fillMaxWidth(),
            label = { Text("Username") },
            leadingIcon = {Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "Username")}
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { newValue:String -> password = newValue },
            Modifier.fillMaxWidth(),
            label = { Text("Password") },
            leadingIcon = {Icon(imageVector = Icons.Filled.Lock, contentDescription = "Password")},
            visualTransformation =  PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { newValue:String -> confirmPassword = newValue },
            Modifier.fillMaxWidth(),
            label = { Text("Confirm Password") },
            leadingIcon = {Icon(imageVector = Icons.Filled.Lock, contentDescription = "Confirm Password")},
            visualTransformation =  PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),        )

        Spacer(Modifier.height(100.dp))



        Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
            Button(onClick = {
                navController.navigate(Screens.Groups.name)
            }) {
                Text(text="Sign Up  ", fontSize = 20.sp)
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Arrow Right", modifier = Modifier.size(20.dp))
            }

        }

        Spacer(Modifier.height(160.dp))

        Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
            Text(text = "Already have an Account? ")
            Text(text = "Log in!", Modifier.clickable {
                navController.navigate(Screens.Login.name)
            }, fontWeight = FontWeight.Bold)


        }
        Spacer(Modifier.height(20.dp))
    }


}