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

    // add Balance when a user joins a group
    suspend fun addBalance(groupId: Int, owedBy: String, owedTo: String, amount: Int): Boolean {
        try {
            val userBalance = Balance(
                groupId = groupId,
                owedBy = owedBy,
                owedTo = owedTo,
                amount = amount,
            )
            Log.d("BALANCE", "FUNCTIOn")


            val existingBalance: Balance? = supabase.from("balance").select {
                filter {
                    eq("group_id", groupId)
                    eq("owed_by", owedBy)
                    eq("owed_to", owedTo)
                }
            }.decodeSingleOrNull<Balance>()

            Log.d("BALANCE", "$existingBalance")
            if (existingBalance == null) {
                supabase.from("balance").insert(userBalance)
            } else {
                val newAmount = existingBalance.amount + amount
                val balanceId = existingBalance.id
                Log.d("BALANCE", "$newAmount")
                supabase.from("balance").update({ set("amount", newAmount) }) {
                    filter {
                        if (balanceId != null) {
                            eq("balance_id", balanceId)
                        }
                    }
                }
            }

            val counterBalance: Balance? = supabase.from("balance").select {
                filter {
                    eq("group_id", groupId)
                    eq("owed_by", owedTo)
                    eq("owed_to", owedBy)
                }
            }.decodeSingleOrNull<Balance>()

            if (counterBalance != null) {
                val newCounterAmount = counterBalance.amount - amount
                if (newCounterAmount > 0) {
                    supabase.from("balance").update({ set("amount", newCounterAmount) }) {
                        filter {
                            counterBalance.id?.let { eq("balance_id", it) }
                        }
                    }
                } else {
                    try {
                        counterBalance.id?.let { deleteBalanceByBalanceId(it) }
                        Log.d("DELETE", "DELETED")
                        /*
                        if (newCounterAmount < 0){
                            val newBalance = abs(newCounterAmount)
                            addBalance(groupId, owedTo, owedBy, newBalance)
                        }

                         */
                    } catch (e: Exception) {
                        Log.d("TAG", "delete Balance failed")
                    }
                }
            }

            return true
        } catch (e: Exception) {
            return false
        }

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