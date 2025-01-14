package com.example.roomy.db

import android.util.Log
import androidx.compose.foundation.layout.Column
import com.example.roomy.db.Supabase.supabase
import com.example.roomy.db.data.Groups
import com.example.roomy.db.data.Item
import com.example.roomy.db.data.ShoppingList
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class ItemRepository {

    suspend fun getAllItems(groupId:Int): Pair<List<Item>, List<Item>> {
        try {
            val shoppingListItems = supabase
                .from("items")
                .select { filter { eq("status", "shoppingList"); eq("group_id", groupId) } }
                .decodeList<Item>()

            val inventoryItems = supabase
                .from("items")
                .select { filter { eq("status", "inventory"); eq("group_id", groupId) } }
                .decodeList<Item>()

            return Pair(shoppingListItems, inventoryItems)
        } catch (e: Exception) {
            // Log the error and throw an exception
            Log.d("TAG", "Could not get the ShoppingLists: ${e.message}")
            throw e
        }
    }


    suspend fun addItem(
        item: Item
    ) {
        try {

            supabase.from("items").insert(item)

        } catch (e: Exception) {
            // Log the error and throw an exception
            Log.d("TAG", "Could not Add Item: ${e.message}")
            throw e
        }
    }

}