package com.example.roomy.db

import android.util.Log
import androidx.compose.foundation.layout.Column
import com.example.roomy.db.Supabase.supabase
import com.example.roomy.db.data.GroupInformation
import com.example.roomy.db.data.Groups
import com.example.roomy.db.data.Item
import com.example.roomy.db.data.ShoppingList
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class ItemRepository {

    // get all Items of a group
    suspend fun getAllItems(groupId: Int): Pair<List<Item>, List<Item>> ? {
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
            return null
        }
    }

    suspend fun getAllItemsForGroups(groupIds: List<Int>): Pair<List<Item>, List<Item>> ? {
        try {

            val shoppingListItems = supabase
                .from("items")
                .select (columns = Columns.ALL)
                {filter { eq("status", "shoppingList"); isIn("group_id", groupIds) } }
                .decodeList<Item>()

            val inventoryItems = supabase
                .from("items")
                .select (columns = Columns.ALL)
                {filter { eq("status", "inventory"); isIn("group_id", groupIds) } }
                .decodeList<Item>()

            return Pair(shoppingListItems, inventoryItems)
        } catch (e: Exception) {
            // Log the error and throw an exception
            Log.d("TAG", "Could not get the ShoppingLists: ${e.message}")
            return null
        }
    }

    // add Item to a List
    suspend fun addItem(
        item: Item
    ): Item? {
        try {

            var newItem = supabase.from("items").insert(item) { select() }.decodeSingle<Item>()
            Log.d("TAG", "Could not Add Item: ${newItem}")



            return newItem

        } catch (e: Exception) {
            // Log the error and throw an exception
            Log.d("TAG", "Could not Add Item: ${e.message}")
            return null
        }
    }

    suspend fun updateItem(
        item: Item
    ): Boolean {
        try {
            Log.d("TAG", "Item: ${item}")


            item.id?.let {
                supabase.from("items").update(
                    {
                        set("status", item.status)
                    }
                ) {
                    filter {
                        eq("item_id", it)
                    }
                }
            }
            return true
        } catch (e: Exception) {
            // Log the error and throw an exception
            Log.d("TAG", "Could not Add Item: ${e.message}")
            return false
        }
    }


    suspend fun deleteItem(
        item: Item
    ): Boolean {
        try {

            item.id?.let {
                supabase.from("items").delete {
                    filter {
                        eq("item_id", item.id)
                    }
                }
            }
            return true
        } catch (e: Exception) {
            // Log the error and throw an exception
            Log.d("TAG", "Could not Delete Item: ${e.message}")
            return false
        }
    }

}