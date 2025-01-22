package com.example.roomy.db

import android.util.Log
import com.example.roomy.db.Supabase.supabase
import com.example.roomy.db.data.Item
import com.example.roomy.db.data.Payments
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.selects.select

class PaymentsRepository {

    // get all payments for a group
    suspend fun getPaymentsByGroupIds(groupIds: List<Int>): List<Payments>? {
        try {
            val payments = supabase.from("payments")
                .select(columns = Columns.ALL) { filter { isIn("group_id", groupIds) } }
                .decodeList<Payments>()
            Log.d("PAYMENT", "$payments")
            return payments
        } catch (e: Exception) {
            Log.d("PAYMENT", "FAILED")
            return null
        }
    }

    // add a new payment to a group
    suspend fun addPayment(userId: String, groupId: Int, amount: Double, items: String): Payments? {
        try {
            val newPayment =
                Payments(groupId = groupId, paidBy = userId, amount = amount, items = items)
            supabase.from("payments").insert(newPayment)
            Log.d("TAG", "PAYMENT")
            return newPayment
        } catch (e: Exception) {
            Log.d("TAG", "PAYMENT FAIL")

            return null
        }

    }

    // delete a payment by paymentId - this is for a future feature
    suspend fun deletePayment(paymentId: Int): Boolean {
        try {
            supabase.from("payments").delete { filter { eq("payment_id", paymentId) } }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    // delete user payments
    suspend fun deleteUserPayments(userId: String, groupId: Int): Boolean {
        try {
            supabase.from("payments").delete {
                filter {
                    eq("user_id", userId)
                    eq("group_id", groupId)
                }
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    // delete all payments when a group gets deleted
    suspend fun deleteGroupPayments(groupId: Int): Boolean {
        try {
            supabase.from("payments").delete {
                filter {
                    eq("group_id", groupId)
                }
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }
}
