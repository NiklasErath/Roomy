package com.example.roomy.db

import android.util.Log
import com.example.roomy.db.Supabase.supabase
import com.example.roomy.db.data.Groups
import com.example.roomy.db.data.UserInformation
import io.github.jan.supabase.postgrest.from

class GroupRepository {

    suspend fun getGroupsByUserId(userId: String): Groups{
        try {
            val response = supabase
                .from("parent_group")
                .select { filter { eq("user_id", userId) } }
                .decodeSingle<Groups>()

            Log.d("TAG", "Got the group Ids: $response")
            return response
        } catch (e: Exception) {
        // Log the error and throw an exception
        Log.d("TAG", "Could not get the groups: ${e.message}")
        throw IllegalStateException("No groups available")
    }


    }
}