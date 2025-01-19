package com.example.roomy.db

import android.util.Log
import com.example.roomy.db.Supabase.supabase
import com.example.roomy.db.data.Balance
import io.github.jan.supabase.postgrest.from

class BalanceRepository {

    suspend fun getBalanceByGroupId(groupId: Int): List<Balance>? {
        try {
            val response = supabase.from("balance").select {
                filter {
                    eq("group_id", groupId)
                }
            }.decodeList<Balance>()
            return response
        } catch (e: Exception) {
            Log.e("HELP BALANCE", "Error fetching user lent: ${e.message}", e)
            return null
        }
    }


    // update the balance when a user bought smth
    suspend fun updateBalance(
        userId: String,
        groupMemberId: String,
        amountOwe: Int,
        amountLent: Int
    ): Boolean {
        try {
            supabase.from("balance").update(
                { set("amount", amountOwe) }
            ) {
                filter {
                    eq("user_owes", groupMemberId)
                    eq("user_lent", userId)
                }
            }
            supabase.from("balance").update(
                { set("amount", amountLent) }
            ) {
                filter {
                    eq("user_lent", groupMemberId)
                    eq("user_owes", userId)
                }
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    // add Balance when a user joins a group
    suspend fun addBalance(groupId: Int, owedBy: String, owedTo: String, amount: Int): Boolean {
        try {
            val userBalance = Balance(
                groupId = groupId,
                owedBy = owedBy,
                owedTo = owedTo,
                amount = amount,
            )

            supabase.from("balance").insert(userBalance)

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