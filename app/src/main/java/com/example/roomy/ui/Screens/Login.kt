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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.roomy.R
import com.example.roomy.db.UserRepository
import com.example.roomy.ui.ViewModels.LoginState
import com.example.roomy.ui.ViewModels.UserViewModel
import com.example.roomy.ui.ViewModels.UserViewModelFactory
import kotlin.math.log

@Composable
fun Login(
    userRepository: UserRepository,
    navController: NavController
){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val viewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(userRepository)  // Correct way to provide the factory
    )

    val loginState by viewModel.loginState
    var context = LocalContext.current

    LaunchedEffect (loginState){
        if(loginState is LoginState.Success){
            navController.navigate(Screens.Groups.name)
        }
        if(loginState is LoginState.Error ){

            val errorMessage = (loginState as LoginState.Error).message

            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }


    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
        Text(text="Login", fontSize = integerResource(id = R.integer.heading1).sp)
        Spacer(Modifier.height(10.dp))

        Text(text="Please sign in to continue")
        Spacer(Modifier.height(100.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { newValue:String -> email = newValue },
            Modifier.fillMaxWidth(),
            label = { Text("Email") },
            leadingIcon = {Icon(imageVector = Icons.Filled.Email, contentDescription = "Email")},
            placeholder = {Text(text="example@outlook.com")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),

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

        Spacer(Modifier.height(100.dp))



        Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
            Button(onClick = {
                viewModel.logInAndFetchUserInformation(email, password);
//                viewModel.getUserInformation()
//        navController.navigate(Screens.Groups.name)
            }) {
                Text(text="Log In  ", fontSize = 20.sp)
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Arrow Right", modifier = Modifier.size(20.dp))
            }

        }

        Spacer(Modifier.height(220.dp))

        Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
            Text(text = "Don't have an account? ")
            Text(text = "Sign Up!", Modifier.clickable {
                navController.navigate(Screens.Register.name)
            }, fontWeight = FontWeight.Bold)


        }
        Spacer(Modifier.height(20.dp))
    }


}