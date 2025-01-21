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

    private val _loginState = mutableStateOf<LoginState>(LoginState.Idle)
    val loginState: State<LoginState> = _loginState

    private val _registerState = mutableStateOf<RegisterState>(RegisterState.Idle)
    val registerState: State<RegisterState> = _registerState

    // Login user
    suspend fun logInUser(userEmail: String, userPassword: String, context: Context) {
        userRepository.signIn(userEmail, userPassword)
        Log.d("SIGN IN ", "USER SIGN IN")
        val currentSession = userRepository.getSessionAndAccessToken(context)
        _session.update { oldState ->
            oldState.copy(
                userId = currentSession
            )
        }

    }

    // get the Information of the user by ID
    suspend fun getUserInformation() {
        Log.d("Tag", currentUserSession.value.userId)

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
                _loginState.value = LoginState.Error(e.message ?: "Unknown Error")


            }


        }
    }


    fun logInAndFetchInformationWithSessionToken(context: Context) {
        val token = UserSessionManager.getSessionToken(context)

        if (token == null) {
            // If token is null, immediately exit and notify user
            _loginState.value = LoginState.Idle
            _userError.update { oldState -> oldState.copy("Login from Session failed") }
            return
        }

        // If the token exists, proceed with session fetching
        viewModelScope.launch {
            try {
                val session = userRepository.fetchSessionWithToken(token)

//                Log.e("SIGN IN ERROR", "Failed to log in: ${e.message}")

                if (session?.user != null) {
                    val currentUserId = session.user!!.id
                    _session.update { oldState -> oldState.copy(userId = currentUserId) }

                    getUserInformation()
                    _loginState.value = LoginState.Success
                    resetLoginState()

                } else {
                    _loginState.value = LoginState.Error("NavLogin")
                }
                // Update login state to success
            } catch (e: Exception) {
                // Handle any exceptions that occur during the session fetching
                Log.e("SIGN IN ERROR", "Failed to log in: ${e.message}")

                // Notify user error
                _userError.update { oldState -> oldState.copy("Failed to get user information from token") }

                // Reset login state to idle
                _loginState.value = LoginState.Idle
            }
        }
    }

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

    // register
    fun signUp(userEmail: String, userPassword: String, userName: String, context: Context) {
        viewModelScope.launch {

            try {


                val emailExists = userRepository.checkExistingEmail(userEmail)
                val userNameExists = userRepository.checkExistingUserName(userName)

                if (emailExists) throw IllegalArgumentException("Email already exists")
                else if (userNameExists) throw IllegalArgumentException("Username already exists")

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


            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(e.message ?: "Unknown Error")


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
            val newUsername = userRepository.checkExistingUserName(username)
            if (newUsername) {
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
    fun resetLoginState() {
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
