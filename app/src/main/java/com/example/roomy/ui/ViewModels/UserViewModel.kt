package com.example.roomy.ui.ViewModels

import android.util.Log
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

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _session = MutableStateFlow(SessionState(""))
    private val _loggedInUser = MutableStateFlow(UserState("", "", ""))

    val currentUserSession = _session.asStateFlow()
    val loggedInUser = _loggedInUser.asStateFlow()

    fun logInUser(userEmail: String, userPassword: String) {
        viewModelScope.launch {
            userRepository.signIn(userEmail, userPassword)

            val currentSession = userRepository.getSession()
            _session.update { oldState ->
                oldState.copy(
                    userId = currentSession
                )

            }

        }
    }

    fun getUserInformation() {
        viewModelScope.launch {
            val user = userRepository.getUserById(currentUserSession.value.userId)
            _loggedInUser.update { oldState ->
                oldState.copy(
                    userId = user.id,
                    username = user.username,
                    email = user.email
                )
            }
        }
    }


}
