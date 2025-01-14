package com.example.roomy.db

import android.util.Log
import androidx.compose.foundation.layout.Column
import com.example.roomy.db.Supabase.supabase
import com.example.roomy.db.data.Item
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class ItemRepository {

    suspend fun getAllItems(): List<Item> {
        try {
            val response = supabase
                .from("items").select(Columns.ALL)
                .decodeList<Item>()

            return response
        } catch (e: Exception) {
            // Log the error and throw an exception
            Log.d("TAG", "Could not get the ShoppingList: ${e.message}")
            throw e
        }
    }
}