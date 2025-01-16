package com.example.roomy.db

import android.util.Log
import androidx.compose.foundation.layout.Column
import com.example.roomy.db.Supabase.supabase
import com.example.roomy.db.data.GroupInformation
import com.example.roomy.db.data.Groups
import com.example.roomy.db.data.UserInformation
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.util.escapeHTML

class GroupRepository {

    // get the groups of the loggedIn user
    suspend fun getGroupsByUserId(userId: String): List<Groups>? {
        try {
            val response = supabase
                .from("parent_group")
                .select { filter { eq("user_id", userId) } }
                .decodeList<Groups>()
            return response
        } catch (e: Exception) {
            // Log the error and throw an exception
            Log.d("TAG", "Could not get the groups: ${e.message}")
            return null
        }
    }

    // get the group information of a group by groupId
    suspend fun getGroupInformationById(groupId: Int): GroupInformation? {
        try {
            val response = supabase
                .from("group_information")
                .select { filter { eq("group_id", groupId) } }
                .decodeSingle<GroupInformation>()
            return response
        } catch (e: Exception) {
            Log.d("TAG", "Could not get the groups: ${e.message}")
            return null
        }
    }

    // create a new group
    suspend fun createGroup(groupName: String, userId: String): GroupInformation? {
        try {
            val group = GroupInformation(name = groupName, creatorId = userId)
            val response = supabase.from("group_information").insert(group) { select() }
                .decodeSingle<GroupInformation>()
            Log.d("TAG", "$response")
            return response
        } catch (e: Exception) {
            Log.d("TAG", "Could not create group: ${e.message}")
            return null
        }
    }

    // get all the group members of a group by ID
    suspend fun getGroupMembers(groupId: Int): List<Groups>? {
        try {
            val response =
                supabase.from("parent_group").select { filter { eq("group_id", groupId) } }
                    .decodeList<Groups>()
            Log.d("GROUP MMM", "GROUP MEMBERS $response")
            return response
        } catch (e: Exception) {
            Log.d("TAG", "Could not create group: ${e.message}")
            return null
        }
    }

    // delete a group in the group information table and kick all members
    suspend fun deleteGroup(groupId: Int): Boolean {
        try {
            supabase.from("group_information").delete { filter { eq("group_id", groupId) } }
            supabase.from("parent_group").delete { filter { eq("group_id", groupId) } }
            return true
        } catch (e: Exception) {
            Log.d("TAG", "Could not delete group: ${e.message}")
            return false
        }
    }

    // add a member to a group by user id ande groupId
    suspend fun addMemberToGroup(userId: String, groupId: Int): Boolean {
        try {
            val existingRelation = supabase.from("parent_group").select {
                filter {
                    eq("group_id", groupId)
                    eq("user_id", userId)
                }
            }.decodeList<Groups>()
            Log.d("EXIST", "$existingRelation")

            if (existingRelation.isEmpty()) {
                val relation = Groups(userId = userId, groupId = groupId)
                supabase.from("parent_group").insert(relation)
                Log.d("MMM", "Add member success")
            } else {
                return false
            }
            return true
        } catch (e: Exception) {
            Log.d("TAG", "Could not insert relation ${e.message}")
            return false
        }

    }

    // kick a member of the group by userId and groupId
    suspend fun kickMemberFromGroup(userId: String, groupId: Int): Boolean {
        try {
            supabase.from("parent_group").delete {
                filter {
                    eq("group_id", groupId)
                    eq("user_id", userId)
                }
            }
            return true
        } catch (e: Exception) {
            Log.d("TAG", "Could not kick user ${e.message}")
            return false
        }
    }

}