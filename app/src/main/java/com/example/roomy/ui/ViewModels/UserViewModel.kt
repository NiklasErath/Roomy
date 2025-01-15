package com.example.roomy.ui.ViewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomy.db.UserRepository
import com.example.roomy.ui.States.SessionState
import com.example.roomy.ui.States.UserState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _session = MutableStateFlow(SessionState(""))
    private val _loggedInUser = MutableStateFlow(UserState("", "", ""))

    val currentUserSession = _session.asStateFlow()
    val loggedInUser = _loggedInUser.asStateFlow()

    private val _loginState = mutableStateOf<LoginState>(LoginState.Idle)
    val loginState: State<LoginState> = _loginState

    private val _registerState = mutableStateOf<RegisterState>(RegisterState.Idle)
    val registerState: State<RegisterState> = _registerState

    suspend fun logInUser(userEmail: String, userPassword: String) {
        userRepository.signIn("n@n.com", "1234")
        val currentSession = userRepository.getSession()
        _session.update { oldState ->
            oldState.copy(
                userId = currentSession
            )
        }

    }

    suspend fun getUserInformation() {
        Log.d("Tag", "geeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeet userId from currentUserSession")
        Log.d("Tag", currentUserSession.value.userId)

        val user = userRepository.getUserById(currentUserSession.value.userId)
        _loggedInUser.update { oldState ->
            oldState.copy(
                userId = user.id,
                username = user.username,
                email = user.email
            )
        }

    }


    fun logInAndFetchUserInformation(userEmail: String, userPassword: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            try {

                logInUser(userEmail, userPassword)
                getUserInformation()
                _loginState.value = LoginState.Success

            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Unknown Error")


            }


        }
    }


    fun signUp(userEmail: String, userPassword: String, userName: String) {
        viewModelScope.launch {

            try {


                val emailExists = userRepository.checkExistingEmail(userEmail)
                val userNameExists = userRepository.checkExistingUserName(userName)

                if (emailExists) throw IllegalArgumentException("Email already exists")
                else if (userNameExists) throw IllegalArgumentException("Username already exists")

                userRepository.signUp(userEmail, userPassword)
                userRepository.signIn(userEmail, userPassword)

                val currentSession = userRepository.getSession()
                _session.update { oldState ->
                    oldState.copy(
                        userId = currentSession
                    )
                }

                userRepository.updateUserInformation(currentSession, userName)

                _loggedInUser.update { oldState ->
                    oldState.copy(
                        userId = currentSession,
                        username = userName,
                        email = userEmail
                    )
                }

                _registerState.value = RegisterState.Success


            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(e.message ?: "Unknown Error")


            }


        }
    }

    fun getUserByUsername(username: String){
        viewModelScope.launch {
            userRepository.getUserByName(username)
        }
    }

//    fun signUp(userEmail: String, userPassword: String, userName: String) {
//        viewModelScope.launch {
//            try {
//                userRepository.signUp(userEmail, userPassword)
//
//                val currentSession = userRepository.getSession()
//                _session.update { oldState ->
//                    oldState.copy(
//                        userId = currentSession
//                    )
//
//                }
//            }catch (e:Exception){
//                                _registerState.value = RegisterState.Error(e.message ?: "Unknown Error")
//
//
//            }
//
//        }
//    }



}
