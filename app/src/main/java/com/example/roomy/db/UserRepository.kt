package com.example.roomy.db

import android.content.Context
import android.util.Log
import com.example.roomy.db.Supabase.UserSessionManager
import com.example.roomy.db.Supabase.supabase
import com.example.roomy.db.data.Groups
import com.example.roomy.db.data.UserInformation
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import java.util.UUID


class UserRepository {

    // create a new account with email and password
    suspend fun signUp(userEmail: String, userPassword: String): Boolean {
        try {
            supabase.auth.signUpWith(Email) {
                email = userEmail
                password = userPassword
            }
            return true
        } catch (e: Exception) {
            Log.d("Tag", "Sign up failed: ${e.message}")
            return false
        }
    }

    // login to user account with email and password
    suspend fun signIn(userEmail: String, userPassword: String): Boolean {
        try {
            supabase.auth.signInWith(Email) {
                email = userEmail
                password = userPassword
            }
            return true
        } catch (e: Exception) {
            Log.d("TAG", "Sign in failed: ${e.message}")
            return false
        }
    }

    // update user Information
    suspend fun updateUserInformation(userId: String, userName: String): Boolean {
        try {
            supabase.from("user_information")
                .update({ set("username", userName) })
                { filter { eq("user_id", userId) } }
            return true
        } catch (e: Exception) {
            Log.d("TAG", "Updating UserName failed: ${e.message}")
            return false
        }
    }

    // get the current session and store the SessionToken in SharedPreferences
    fun getSessionAndAccessToken(context: Context): String {
        val session = supabase.auth.currentSessionOrNull()

        // set current user Id
        val currentUserId = session?.user?.id
            ?: throw IllegalStateException("User is not logged in. Please log in.")

        // save Access Token in Shared Preferences
        val accessToken = session.accessToken
        UserSessionManager.saveSessionToken(context, accessToken)

        return currentUserId
    }

    // fetch the session and return it
    fun fetchSession(): UserSession? {
        try {
            val session = supabase.auth.currentSessionOrNull()
            return session
        } catch (e: Exception) {
            throw e
        }
    }


    // get the logged in user Information by the userId
    suspend fun getUserById(userId: String): UserInformation? {
        try {
            val response = supabase
                .from("user_information")
                .select { filter { eq("user_id", userId) } }
                .decodeSingle<UserInformation>()
            return response
        } catch (e: Exception) {
            Log.d("TAG", "Could not get the User information: ${e.message}")
            return null
        }

    }

    // get the users of a group by a list of the group Ids and return a list of Groups with all its Member Ids
    suspend fun getUsersByGroupIds(groupIds: List<Int>): List<Groups>? {
        try {
            val response = supabase
                .from("parent_group")
                .select(columns = Columns.ALL) { filter { isIn("group_id", groupIds) } }
                .decodeList<Groups>()

            return response
        } catch (e: Exception) {
            Log.d("TAG", "Could not get the groups User IDs: ${e.message}")
            return null
        }
    }

    // get the User Information by their Ids
    suspend fun getUserInformationByIds(userIds: List<String>): List<UserInformation>? {
        try {
            val response = supabase
                .from("user_information")
                .select(columns = Columns.ALL) { filter { isIn("user_id", userIds) } }
                .decodeList<UserInformation>()

            return response
        } catch (e: Exception) {
            Log.d("TAG", "Could not get the groups User IDs: ${e.message}")
            return null
        }
    }

    // get the user information by name - used to check if a username is available or not
    suspend fun getUserByName(name: String): UserInformation? {
        try {
            val response = supabase
                .from("user_information")
                .select { filter { eq("username", name) } }
                .decodeSingle<UserInformation>()

            return response
        } catch (e: Exception) {
            Log.d("TAG", "No user found ${e.message}")
            return null
        }
    }

    // check if the Email exists toi make sure the email is unique
    suspend fun checkExistingEmail(userEmail: String): Boolean {
        try {
            supabase.from("user_information")
                .select { filter { eq("email", userEmail) } }
                .decodeSingle<UserInformation>()

            return true
        } catch (e: Exception) {
            Log.d("TAG", "Updating UserName failed: ${e.message}")
            return false
        }

    }

    // check if the username already exists to make sure the username is unique
    suspend fun checkExistingUserName(userName: String): Boolean {
        try {
            supabase.from("user_information")
                .select { filter { eq("username", userName) } }
                .decodeSingle<UserInformation>()

            return true
        } catch (e: Exception) {
            Log.d("TAG", "Updating UserName failed: ${e.message}")
            return false
        }


    }

    // function to logout a user
    suspend fun logout(): Boolean {
        try {
            supabase.auth.signOut()
            return true
        } catch (e: Exception) {
            return false
        }
    }

}