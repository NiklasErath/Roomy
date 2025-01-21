package com.example.roomy.ui

import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.roomy.R
import com.example.roomy.ui.ViewModels.RegisterState
import com.example.roomy.ui.ViewModels.UserViewModel

@Composable
fun Register(
    userViewModel: UserViewModel,
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }


    val registerState by userViewModel.registerState
    var context = LocalContext.current

    LaunchedEffect(registerState) {
        if (registerState is RegisterState.Success) {
            navController.navigate(Screens.Home.name)
        }
        if (registerState is RegisterState.Error) {
            val errorMessage = (registerState as RegisterState.Error).message
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
        Text(text = "Sign up", fontSize = integerResource(id = R.integer.heading1).sp)
        Spacer(Modifier.height(100.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { newValue: String -> email = newValue },
            Modifier.fillMaxWidth(),
            label = { Text("Email") },
            leadingIcon = { Icon(imageVector = Icons.Filled.Email, contentDescription = "Email") },
            placeholder = { Text(text = "example@outlook.com") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),

            )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { newValue: String -> username = newValue },
            Modifier.fillMaxWidth(),
            label = { Text("Username") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Username"
                )
            }
        )
        if (username.length in 1..2){
            Text(text = "The username must be at least 3 characters long",
                color = Color.Red,)
        }

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { newValue: String -> password = newValue },
            Modifier.fillMaxWidth(),
            label = { Text("Password") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Password"
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )
        if (password.length in 1..5){
            Text(text = "Password has to be more than 5 characters",
                color = Color.Red,)
        }
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { newValue: String -> confirmPassword = newValue },
            Modifier.fillMaxWidth(),
            label = { Text("Confirm Password") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Confirm Password"
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )

        Spacer(Modifier.height(100.dp))



        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = {


                if (password != confirmPassword) {
                    Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT).show()

                }
                else if (password.length <= 5) {
                    Toast.makeText(context, "Password has to be more than 5 characters!", Toast.LENGTH_SHORT).show()

                }
                else if (username.length <= 2){
                    Toast.makeText(context, "Username has to be more than 3 characters!", Toast.LENGTH_SHORT).show()

                }
                else {
                    userViewModel.signUp(email, password, username, context)

                }


            }) {
                Text(text = "Sign Up  ", fontSize = 20.sp)
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Arrow Right",
                    modifier = Modifier.size(20.dp)
                )
            }

        }

        Spacer(Modifier.height(160.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = "Already have an Account? ")
            Text(text = "Log in!", Modifier.clickable {
                navController.navigate(Screens.Login.name)
            }, fontWeight = FontWeight.Bold)


        }
        Spacer(Modifier.height(20.dp))
    }


}