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
            Log.d("Tag", "Sign up failed with exception:.................")
            e.message?.let { Log.d("TAG", it) }

            throw e
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

            throw e

        }
    }

    suspend fun updateUserInformation(userId: String, userName: String) {

        try {
            supabase.from("user_information").update(
                { set("username", userName) }
            ) {
                filter { eq("user_id", userId) }
            }
            Log.d("TAG", userId)
            Log.d("TAG", userName)



            Log.d("TAG", "Updating UserName Success")


        } catch (e: Exception) {
            Log.d("TAG", "Updating UserName failed")
            e.message?.let { Log.d("TAG", it) }

        }


    }

    fun getSession(): String {
        val session = supabase.auth.currentSessionOrNull()

        Log.d("UserID", "${session?.user?.id}")

        val currentUserId = session?.user?.id
            ?: throw IllegalStateException("User is not logged in. Please log in.")
        val uuid = UUID.fromString(currentUserId)
        Log.d("UUID", "$uuid")


        return currentUserId
    }

    suspend fun getUserById(userId: String): UserInformation {
        Log.d("TAG", "HEYXXXYYYXXXX")

        try {
            // Query to fetch user information by userId
            val response = supabase
                .from("user_information") // Table name
                .select {
                    filter {
                        eq(
                            "user_id",
                            userId
                        )
                    }
                } // Select all columns (you can specify specific ones like .select("id", "name", "email"))
                .decodeSingle<UserInformation>()

            Log.d("ICH HABE ES GESCHAFFT", "user info $response")

            return response
        } catch (e: Exception) {
            // Log the error and throw an exception
            Log.d("TAG", "Could not get the User information: ${userId}")

            Log.d("TAG", "Could not get the User information: ${e.message}")
            throw e
        }

    }

    suspend fun getUserByName(name: String): UserInformation {
        try {
            // Query to fetch user information by userId
            val response = supabase
                .from("user_information") // Table name
                .select {
                    filter {
                        eq(
                            "username",
                            name
                        )
                    }
                } // Select all columns (you can specify specific ones like .select("id", "name", "email"))
                .decodeSingle<UserInformation>()

            Log.d("USER TO ADD", "user info $response")

            return response
        } catch (e: Exception) {
            // Log the error and throw an exception
            Log.d("TAG", "No user found ${e.message}")
            throw e
        }

    }

    suspend fun checkExistingEmail(userEmail: String): Boolean {

        try {
            val response = supabase.from("user_information").select {
                filter {
                    eq("email", userEmail)
                }
            }.decodeSingle<UserInformation>()

            Log.d("TAG", "UserMail: ${response.email}")


            if(response.email == userEmail){
                return true

            }else{
                return false
            }

        }catch (e:Exception){
            Log.d("TAG", "Updating UserName failed")
            e.message?.let { Log.d("TAG", it) }
            return false
        }

    }

    suspend fun checkExistingUserName(userName: String): Boolean {

        try {
            val response = supabase.from("user_information").select {
                filter {
                    eq("username", userName)
                }
            }.decodeSingle<UserInformation>()

            Log.d("TAG", "UserName: ${response.username}")


            if(response.username == userName){
                return true

            }else{
                return false
            }

        }catch (e:Exception){
            Log.d("TAG", "Updating UserName failed")
            e.message?.let { Log.d("TAG", it) }
            return false
        }


    }

}