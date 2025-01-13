package com.example.roomy.db

import android.util.Log
import co.touchlab.kermit.Tag
import com.example.roomy.db.Supabase.supabase
import com.example.roomy.db.data.UserInformation
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import java.util.UUID


class UserRepository {

    suspend fun signUp(userEmail: String, userPassword: String) {
        Log.d("DATA LOGIN", "$userEmail , $userPassword")

        try {


            val result = supabase.auth.signUpWith(Email) {
                email = userEmail
                password = userPassword
            }

            Log.d("TAG", "Sign-up successful: $result")
        } catch (e: Exception) {
            Log.d("Tag","Sign up failed with exception:.................")
            e.message?.let { Log.d("TAG", it) }
        }
    }

    suspend fun signIn(userEmail: String, userPassword: String) {
        try {
            val result = supabase.auth.signInWith(Email) {
                email = userEmail
                password = userPassword
            }
            Log.d("TAG", "Sign-in successful: $result")
        } catch (e: Exception) {
            Log.d("TAG", "Sign in failed")
            e.message?.let { Log.d("TAG", it) }
            Log.d("DATA LOGIN", "$userEmail , $userPassword")

        }
    }

    fun getSession(): String {
        val session = supabase.auth.currentSessionOrNull()

        Log.d("UserID", "${session?.user?.id}")

        val currentUserId = session?.user?.id ?: throw IllegalStateException("User is not logged in. Please log in.")
        val uuid = UUID.fromString(currentUserId)
        Log.d("UUID","$uuid")


        return currentUserId
    }

    suspend fun getUserById(userId: String): UserInformation {
        Log.d("TAG", "HEYXXXYYYXXXX")

        try {
            // Query to fetch user information by userId
            val response = supabase
                .from("user_information") // Table name
                .select{filter { eq("user_id", userId)}} // Select all columns (you can specify specific ones like .select("id", "name", "email"))
                .decodeSingle<UserInformation>()

            Log.d("ICH HABE ES GESCHAFFT", "user info $response")

            return response
        } catch (e: Exception) {
            // Log the error and throw an exception
            Log.d("TAG", "Could not get the User information: ${e.message}")
            throw IllegalStateException("No User information available")
        }

    }
}