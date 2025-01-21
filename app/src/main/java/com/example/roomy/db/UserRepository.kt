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

    // create a new account
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

    // login to your account
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

    // update user Information
    suspend fun updateUserInformation(userId: String, userName: String): Boolean {

        try {
            supabase.from("user_information").update(
                { set("username", userName) }
            ) {
                filter { eq("user_id", userId) }
            }

            Log.d("TAG", "Updating UserName Success")

            return true
        } catch (e: Exception) {
            Log.d("TAG", "Updating UserName failed")
            return false

        }


    }


    // get the current session and store the SessionToken in SharedPreferences
    fun getSessionAndAccessToken(context: Context): String {
        val session = supabase.auth.currentSessionOrNull()

        Log.d("UserID", "${session?.user}")


        val currentUserId = session?.user?.id
            ?: throw IllegalStateException("User is not logged in. Please log in.")
        val uuid = UUID.fromString(currentUserId)
        Log.d("UUID", "$uuid")


//        Save Access Token in Shared Preferences
        val accessToken = session.accessToken
        UserSessionManager.saveSessionToken(context, accessToken)


        return currentUserId
    }


    suspend fun fetchSessionWithToken(sessionToken: String): UserSession? {
        try {
            val session = supabase.auth.currentSessionOrNull()
            return session
        } catch (e: Exception) {
            throw e
        }
    }


    // get the user Information by the userId
    suspend fun getUserById(userId: String): UserInformation? {

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
                }
                .decodeSingle<UserInformation>()

            Log.d("ICH HABE ES GESCHAFFT", "user info $response")

            return response
        } catch (e: Exception) {
            Log.d("TAG", "Could not get the User information: ${e.message}")
            return null
        }

    }

//    suspend fun getGroupInformationById(groupIds: List<Int>): List<GroupInformation>? {
//        try {
//            val response = supabase
//                .from("group_information")
//                .select (columns = Columns.ALL){
//                    filter{
//                        isIn("group_id", groupIds)
//                    }
//                }
//                .decodeList<GroupInformation>()
//            Log.d("GroupInfooooooooooooooooooooooooooooo", "$response")
//
//            return response
//        } catch (e: Exception) {
//            Log.d("TAG", "Could not get the groups: ${e.message}")
//            return null
//        }
//    }

    suspend fun getUsersByGroupIds(groupIds: List<Int>): List<Groups>? {

        try {
            val response = supabase
                .from("parent_group")
                .select(columns = Columns.ALL) {
                    filter {
                        isIn("group_id", groupIds)
                    }
                }
                .decodeList<Groups>()  // Decode the response into a list of Groups <group_id, user_id>

            return response

        } catch (e: Exception) {
            Log.d("TAG", "Could not get the groups User IDs: ${e.message}")
            return null
        }


    }


    suspend fun getUserInformationByIds(userIds: List<String>): List<UserInformation>? {

        try {
            val response = supabase
                .from("user_information")
                .select(columns = Columns.ALL) {
                    filter {
                        isIn("user_id", userIds)
                    }
                }
                .decodeList<UserInformation>()  // Decode the response into a list of Groups <group_id, user_id>

            return response

        } catch (e: Exception) {
            Log.d("TAG", "Could not get the groups User IDs: ${e.message}")
            return null
        }

    }

    // get the user information by name
    suspend fun getUserByName(name: String): UserInformation? {
        try {
            val response = supabase
                .from("user_information")
                .select {
                    filter {
                        eq(
                            "username",
                            name
                        )
                    }
                }
                .decodeSingle<UserInformation>()

            Log.d("USER TO ADD", "user info $response")

            return response
        } catch (e: Exception) {
            // Log the error and throw an exception
            Log.d("TAG", "No user found ${e.message}")
            return null
        }

    }

    // check if the Email exists toi make sure the email is unique
    suspend fun checkExistingEmail(userEmail: String): Boolean {

        try {
            val response = supabase.from("user_information").select {
                filter {
                    eq("email", userEmail)
                }
            }.decodeSingle<UserInformation>()

            Log.d("TAG", "UserMail: ${response.email}")


            if (response.email == userEmail) {
                return true

            } else {
                return false
            }

        } catch (e: Exception) {
            Log.d("TAG", "Updating UserName failed")
            e.message?.let { Log.d("TAG", it) }
            return false
        }

    }

    // check if the username already exists to make sure the username is unique
    suspend fun checkExistingUserName(userName: String): Boolean {

        try {
            val response = supabase.from("user_information").select {
                filter {
                    eq("username", userName)
                }
            }.decodeSingle<UserInformation>()

            Log.d("TAG", "UserName: ${response.username}")


            if (response.username == userName) {
                return true

            } else {
                return false
            }

        } catch (e: Exception) {
            Log.d("TAG", "Updating UserName failed")
            e.message?.let { Log.d("TAG", it) }
            return false
        }


    }

    suspend fun logout(): Boolean {
        try {
            supabase.auth.signOut()
            return true
        } catch (e: Exception) {
            return false
        }
    }

}