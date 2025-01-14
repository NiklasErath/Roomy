package com.example.roomy.db

import android.util.Log
import com.example.roomy.db.Supabase.supabase
import com.example.roomy.db.data.ShoppingList
import io.github.jan.supabase.postgrest.from

class ShoppingListRepository {

    suspend fun getShoppingListByGroupId(groupId: Int): ShoppingList {
        try {
            val response = supabase
                .from("shopping_list")
                .select { filter { eq("group_id", groupId) } }
                .decodeSingle<ShoppingList>()

            return response
        } catch (e: Exception) {
        // Log the error and throw an exception
        Log.d("TAG", "Could not get the ShoppingList: ${e.message}")
        throw e
    }
    }
}