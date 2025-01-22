package com.example.roomy.ui.ViewModels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomy.db.Supabase.UserSessionManager
import com.example.roomy.db.UserRepository
import com.example.roomy.ui.States.SessionState
import com.example.roomy.ui.States.UserState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.concurrent.timer

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class RegisterState {
    data object Idle : RegisterState()
    data object Loading : RegisterState()
    data object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

sealed class UserError {
    data class Error(val message: String)
}

class UserViewModel(
    private val userRepository: UserRepository,
    private val stateViewModel: StateViewModel
) : ViewModel() {

    private val _session = MutableStateFlow(SessionState(""))
    private val _loggedInUser = MutableStateFlow(UserState("", "", ""))

    val currentUserSession = _session.asStateFlow()
    val loggedInUser = _loggedInUser.asStateFlow()


    // error Message
    private val _userError = MutableStateFlow(UserError.Error(""))
    val userError = _userError.asStateFlow()

    // login State to redirect the user if necessary
    private val _loginState = mutableStateOf<LoginState>(LoginState.Idle)
    val loginState: State<LoginState> = _loginState

    // register State to redirect the user if necessary
    private val _registerState = mutableStateOf<RegisterState>(RegisterState.Idle)
    val registerState: State<RegisterState> = _registerState

    // Login user
    private suspend fun logInUser(userEmail: String, userPassword: String, context: Context) {
        val signIn = userRepository.signIn(userEmail, userPassword)
        val currentSession = userRepository.getSessionAndAccessToken(context)
        _session.update { oldState ->
            oldState.copy(
                userId = currentSession
            )
        }
        if (!signIn) {
            _userError.update { oldState ->
                oldState.copy("Failed to login! Try again!")
            }
        }

    }

    // get the Information of the current logged in user by ID
    private suspend fun getUserInformation() {

        val user = userRepository.getUserById(currentUserSession.value.userId)
        if (user == null) {
            _userError.update { oldState ->
                oldState.copy("Failed to get user information")
            }
        } else {
            _loggedInUser.update { oldState ->
                oldState.copy(
                    userId = user.id,
                    username = user.username,
                    email = user.email
                )
            }
        }
    }

    // login the user and get all his information
    fun logInAndFetchUserInformation(userEmail: String, userPassword: String, context: Context) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                logInUser(userEmail, userPassword, context)
                getUserInformation()
                _loginState.value = LoginState.Success
                resetLoginState()

            } catch (e: Exception) {
                _userError.update { oldState ->
                    oldState.copy("Username or Password is wrong")
                }
            }
        }
    }


    // check if there is already a session when launching the app and if there is one fetch all the needed data
    fun logInAndFetchInformationWithSessionToken(context: Context) {
        val token = UserSessionManager.getSessionToken(context)

        if (token == null) {
            _loginState.value = LoginState.Idle
            _userError.update { oldState -> oldState.copy("Login from Session failed") }
        } else {

            // if a  session exists get all the information
            viewModelScope.launch {
                try {
                    val session = userRepository.fetchSession()

                    if (session?.user != null) {
                        val currentUserId = session.user!!.id
                        _session.update { oldState -> oldState.copy(userId = currentUserId) }

                        getUserInformation()
                        _loginState.value = LoginState.Success
                        resetLoginState()

                    } else {
                        _loginState.value = LoginState.Error("NavLogin")
                    }
                } catch (e: Exception) {
                    Log.e("SIGN IN ERROR", "Failed to log in: ${e.message}")

                    _userError.update { oldState -> oldState.copy("Failed to get user information from token") }
                    _loginState.value = LoginState.Idle
                }
            }
        }
    }

    // logout function
    fun logout(callback: (Boolean) -> Unit, context: Context) {
        viewModelScope.launch {
            val logout = userRepository.logout()
            if (!logout) {
                _userError.update { oldState ->
                    oldState.copy("Logout failed")
                }
                callback(true)
            } else {
                UserSessionManager.clearSession(context)
                stateViewModel.clearGroupsState()
                _session.update { oldState ->
                    oldState.copy("")
                }
                _loggedInUser.update { oldState ->
                    oldState.copy("", "", "")
                }
                _loginState.value = LoginState.Idle
                callback(false)
            }
        }
    }

    // register a new user
    fun signUp(userEmail: String, userPassword: String, userName: String, context: Context) {
        viewModelScope.launch {
            clearUserError()
            try {

                // check if the username and email is still available
                val emailExists = userRepository.checkExistingEmail(userEmail)
                val userNameExists = userRepository.checkExistingUserName(userName)

                if (emailExists) {
                    _userError.update { oldState ->
                        oldState.copy("Email address is already taken")
                    }
                }
                if (userNameExists) {
                    _userError.update { oldState ->
                        oldState.copy("Username is already taken")
                    }
                }

                if (!emailExists && !userNameExists) {
                    userRepository.signUp(userEmail, userPassword)
                    userRepository.signIn(userEmail, userPassword)

                    val currentSession = userRepository.getSessionAndAccessToken(context)
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
                }
            } catch (e: Exception) {
                _userError.update { oldState ->
                    oldState.copy("Sign up failed! Try again!")
                }
            }
        }
    }

    // get a user by his username
    fun getUserByUsername(username: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = userRepository.getUserByName(username)
            if (user == null) {
                _userError.update { oldState ->
                    oldState.copy("No user found")
                }
                callback(false)
            } else {
                callback(true)
            }
        }
    }

    // update the username and check if he already exists
    fun updateUserName(username: String, userId: String) {
        viewModelScope.launch {
            val newUsername = userRepository.getUserByName(username)
            if (newUsername == null) {
                _userError.update { oldState ->
                    oldState.copy("Username already exists")
                }
            } else {
                val user = userRepository.updateUserInformation(userId, username)
                if (!user) {
                    _userError.update { oldState ->
                        oldState.copy("username could not be updated")
                    }
                } else {
                    stateViewModel.updateUserName(username, userId)

                    _loggedInUser.update { oldstate ->
                        oldstate.copy(
                            userId = userId,
                            username = username,
                            email = loggedInUser.value.email
                        )
                    }
                }
            }

        }
    }

    //  Clear LoginState for Future Logins
    private fun resetLoginState() {
        viewModelScope.launch {
            delay(100) // Adjust the delay time as needed (e.g., 2 seconds)
            _loginState.value = LoginState.Idle
        }

    }

    // clear the user error state
    fun clearUserError() {
        _userError.update { oldState ->
            oldState.copy("")
        }
    }


}
