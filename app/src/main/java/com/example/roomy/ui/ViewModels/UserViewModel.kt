package com.example.roomy.ui.ViewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

object SessionStateHolder {
    val session = MutableStateFlow(SessionState(""))
}
object LoggedInUserStateHolder {
    val loggedInUser = MutableStateFlow(UserState("", "", ""))
}

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginState = mutableStateOf<LoginState>(LoginState.Idle)
    val loginState: State<LoginState> = _loginState

    private val _session = SessionStateHolder.session
    private val _loggedInUser = LoggedInUserStateHolder.loggedInUser

    val currentUserSession = _session.asStateFlow()
    val loggedInUser = _loggedInUser.asStateFlow()

    suspend fun logInUser(userEmail: String, userPassword: String) {
        userRepository.signIn(userEmail, userPassword)
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
            userRepository.signUp(userEmail, userPassword)

            val currentSession = userRepository.getSession()
            _session.update { oldState ->
                oldState.copy(
                    userId = currentSession
                )

            }

        }
    }


}
