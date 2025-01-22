package com.example.roomy.db

import android.util.Log
import com.example.roomy.db.Supabase.supabase
import com.example.roomy.db.data.Balance
import io.github.jan.supabase.postgrest.from
import kotlin.math.abs

class BalanceRepository {

    // get the Balance from a group
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

    // Balance function
    // add Balance when there is a new payment
    suspend fun addBalance(groupId: Int, owedBy: String, owedTo: String, amount: Double): Balance? {

        var updatedBalance: Balance? = null
        try {
            Log.d("BALANCE", "Processing balance adjustment")

            // try to get the existing balance or counter balance
            val existingBalance: Balance? = supabase.from("balance").select {
                filter {
                    eq("group_id", groupId)
                    eq("owed_by", owedBy)
                    eq("owed_to", owedTo)
                }
            }.decodeSingleOrNull<Balance>()

            val counterBalance: Balance? = if (existingBalance == null) {
                supabase.from("balance").select {
                    filter {
                        eq("group_id", groupId)
                        eq("owed_by", owedTo)
                        eq("owed_to", owedBy)
                    }
                }.decodeSingleOrNull<Balance>()
            } else {
                null
            }

            // if there is an existingBalance (positive for the user)
            if (existingBalance != null) {
                val newAmount = existingBalance.amount + amount
                if (newAmount > 0) {
                    existingBalance.id?.let { balanceId ->
                        updatedBalance = supabase.from("balance").update({ set("amount", newAmount) }) { select()
                            filter {
                                eq("balance_id", balanceId)
                            }
                        }.decodeSingleOrNull<Balance>()
                    }
                    Log.d("BALANCE", "Updated existing balance amount: $newAmount")
                } else {
                    existingBalance.id?.let { balanceId ->
                        deleteBalanceByBalanceId(balanceId)
                    }
                    Log.d("BALANCE", "Deleted existing balance due to zero or negative amount")
                }
            }

            // if there is an counterBalance (negative for the user)
            if (counterBalance != null) {
                val newCounterAmount = counterBalance.amount - amount
                if (newCounterAmount > 0) {
                    counterBalance.id?.let { balanceId ->
                        updatedBalance = supabase.from("balance").update({ set("amount", newCounterAmount) }) { select()
                            filter {
                                eq("balance_id", balanceId)
                                 }
                        }.decodeSingleOrNull<Balance>()
                    }
                } else if (newCounterAmount < 0) {

                    // when the new CounterAmount is smaller then 0 delete the balance
                    val newBalanceAmount = -newCounterAmount
                    counterBalance.id?.let { balanceId ->
                        deleteBalanceByBalanceId(balanceId)
                    }

                    // after deleting the balance add a new one ( positive for the user)
                    val newBalance = Balance(
                        groupId = groupId,
                        owedBy = owedBy,
                        owedTo = owedTo,
                        amount = newBalanceAmount
                    )
                    updatedBalance = supabase.from("balance").insert(newBalance) { select()} .decodeSingleOrNull<Balance>()

                }
            }

            // if neither existingBalance nor counterBalance exist -  insert a new balance if amount > 0
            if (existingBalance == null && counterBalance == null && amount > 0) {
                val userBalance = Balance(
                    groupId = groupId,
                    owedBy = owedBy,
                    owedTo = owedTo,
                    amount = amount
                )
                updatedBalance = supabase.from("balance").insert(userBalance) { select()} .decodeSingleOrNull<Balance>()
            }

        } catch (e: Exception) {
            Log.e("TAG", "Error in addBalance: ${e.localizedMessage}", e)
            return null
        }
        return updatedBalance
    }


    // use when deleting a balance
    private suspend fun deleteBalanceByBalanceId(balanceId: Int): Boolean {
        try {
            supabase.from("balance").delete { filter { eq("balance_id", balanceId) } }
            Log.d("DELETE", "DELETE TRUUUEE")

            return true
        } catch (e: Exception) {
            Log.d("DELETE", "DELETEFAIL")

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
                    eq("owed_by", userId)
                    eq("group_id", userId)
                }
            }
            supabase.from("balance").delete {
                filter {
                    eq("owed_to", userId)
                    eq("group_id", groupId)
                }
            }
            return true
        } catch (e: Exception) {
            return false

        }
    }
}