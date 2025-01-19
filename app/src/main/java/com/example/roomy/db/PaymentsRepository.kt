package com.example.roomy.db

import android.util.Log
import com.example.roomy.db.Supabase.supabase
import com.example.roomy.db.data.Item
import com.example.roomy.db.data.Payments
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.selects.select

class PaymentsRepository {

    suspend fun getPaymentsByGroupId(groupId: Int): List<Payments>? {
        try {
            val payments = supabase.from("payments").select { filter { eq("group_id", groupId) } }
                .decodeList<Payments>()
            Log.d("PAYMENT", "$payments")
            return payments
        } catch (e: Exception) {
            Log.d("PAYMENT", "FAILED")
            return null
        }
    }

    suspend fun addPayment(userId: String, groupId: Int, amount: Int, items: String): Payments? {
        try {
            val newPayment = Payments( groupId = groupId, paidBy = userId, amount = amount, items = items)
            supabase.from("payments").insert(newPayment)
            return newPayment
        } catch (e: Exception) {
            return null
        }

    }

    suspend fun deletePayment(paymentId: Int): Boolean{
        try {
            supabase.from("payments").delete{ filter { eq("payment_id", paymentId) }}
            return true
        } catch (e: Exception){
            return false
        }
    }

    suspend fun deleteUserPayments(userId: String, groupId: Int): Boolean{
        try {
            supabase.from("payments").delete{ filter { eq("user_id", userId)
            eq("group_id", groupId)}}
            return true
        } catch (e: Exception){
            return false
        }
    }
}
