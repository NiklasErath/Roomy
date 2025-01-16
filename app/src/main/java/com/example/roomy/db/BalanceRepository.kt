package com.example.roomy.db

import com.example.roomy.db.Supabase.supabase
import com.example.roomy.db.data.Balance
import io.github.jan.supabase.postgrest.from

class BalanceRepository {
    // get all the user debts
    suspend fun getUserDebtsByGroupId(groupId: Int, userId: String): List<Balance>? {
        try {
            val response = supabase.from("balance").select {
                filter {
                    eq("group_id", groupId)
                    eq("user_owes", userId)
                }
            }
                .decodeList<Balance>()
            return response
        } catch (e: Exception) {
            return null
        }
    }

    // get what the user lents whom
    suspend fun getUserLentByGroupId(groupId: Int, userId: String): List<Balance>? {
        try {
            val response = supabase.from("balance").select {
                filter {
                    eq("group_id", groupId)
                    eq("user_lent", userId)
                }
            }
                .decodeList<Balance>()
            return response
        } catch (e: Exception) {
            return null
        }
    }

    // update the balance when a user bought smth
    suspend fun updateBalance(userId: String, groupMemberId: String, amountOwe:Int, amountLent:Int): Boolean {
        try {
            supabase.from("balance").update(
                { set("amount", amountOwe) }
            )  { filter { eq("user_owes", groupMemberId)
            eq("user_lent", userId)} }
            supabase.from("balance").update (
                { set("amount", amountLent) }
            ){ filter { eq("user_lent", groupMemberId)
                eq("user_owes", userId)} }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    // add Balance when a user joins a group
    suspend fun addBalance(groupId: Int, userId: String, groupMemberId: String): Boolean {
        try {
            val userBalanceOwe = Balance(
                id = 0,
                created = "",
                groupId = groupId,
                userOwes = userId,
                userDebts = groupMemberId,
                amount = 0,
            )

            val userBalanceLent = Balance(
                id = 0,
                created = "",
                groupId = groupId,
                userOwes = groupMemberId,
                userDebts = userId,
                amount = 0,
            )

            supabase.from("balance").insert(userBalanceOwe)
            supabase.from("balance").insert(userBalanceLent)

            return true
        } catch (e: Exception) {
            return false
        }

    }

    // use when deleting a group
    suspend fun deleteBalanceByGroupId(groupId: Int): Boolean {
        try {
            supabase.from("balance").delete { filter { eq("group_id", groupId) } }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    // use when a user leaves the group/ gets kicked
    suspend fun deleteUserBalanceByGroupId(groupId: Int, userId: String): Boolean {
        try {
            supabase.from("balance").delete {
                filter {
                    eq("user_Id", userId)
                    eq("group_id", groupId)
                }
            }
            return true
        } catch (e: Exception) {
            return false
        }

    }

    // delete all balances of a user when he wants to delete his account
    suspend fun deleteBalanceByUserId(groupId: Int, userId: String): Boolean {
        try {
            supabase.from("balance").delete {
                filter {
                    eq("user_owes", userId)
                    eq("group_id", userId)
                }
            }
            supabase.from("balance").delete {
                filter {
                    eq("user_lent", userId)
                    eq("group_id", groupId)
                }
            }
            return true
        } catch (e: Exception) {
            return false

        }
    }
}