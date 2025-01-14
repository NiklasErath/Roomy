package com.example.roomy.db

import android.util.Log
import com.example.roomy.db.Supabase.supabase
import com.example.roomy.db.data.GroupInformation
import com.example.roomy.db.data.Groups
import com.example.roomy.db.data.UserInformation
import io.github.jan.supabase.postgrest.from

class GroupRepository {

    suspend fun getGroupsByUserId(userId: String): List<Groups> {
        try {
            val response = supabase
                .from("parent_group")
                .select { filter { eq("user_id", userId) } }
                .decodeList<Groups>()

            Log.d("TAG", "Got the group Ids: $response")
            return response
        } catch (e: Exception) {
            // Log the error and throw an exception
            Log.d("TAG", "Could not get the groups: ${e.message}")
            throw IllegalStateException("No groups available")
        }
    }

    suspend fun getGroupInformationById(groupId: Int): GroupInformation {
        try {
            val response = supabase
                .from("group_information")
                .select { filter { eq("group_id", groupId) } }
                .decodeSingle<GroupInformation>()
            return response
        } catch (e: Exception) {
            Log.d("TAG", "Could not get the groups: ${e.message}")
            throw IllegalStateException("No groups available")
        }
    }

    suspend fun createGroup(groupName: String, userId: String ):GroupInformation{
        try{
            val group = GroupInformation(name = groupName, creatorId = userId)
            val response = supabase.from("group_information").insert(group){select()}.decodeSingle<GroupInformation>()
            Log.d("TAG", "$response")
            return response
        } catch (e: Exception) {
            Log.d("TAG", "Could not create group: ${e.message}")
            throw IllegalStateException("No groups available")
        }
    }

    suspend fun addMemberToGroup(userId: String, groupId: Int){
        try {
            val relation = Groups(userId = userId, groupId = groupId)
            supabase.from("parent_group").insert(relation)
        } catch (e: Exception) {
            Log.d("TAG", "Could not insert relation ${e.message}")
        }

    }

}