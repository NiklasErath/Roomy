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
    ) :Item {
        try {

            var newItem = supabase.from("items").insert(item){select()}.decodeSingle<Item>()
            Log.d("TAG", "Could not Add Item: ${newItem}")



            return newItem

        } catch (e: Exception) {
            // Log the error and throw an exception
            Log.d("TAG", "Could not Add Item: ${e.message}")
            throw e
        }
    }

    suspend fun updateItem(
        item: Item
    ) {
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

        } catch (e: Exception) {
            // Log the error and throw an exception
            Log.d("TAG", "Could not Add Item: ${e.message}")
            throw e
        }
    }


    suspend fun deleteItem(
        item: Item
    ) {
        try {

            item.id?.let {
                supabase.from("items").delete{
                    filter{
                        eq("item_id", item.id)
                    }
                }
            }

        } catch (e: Exception) {
            // Log the error and throw an exception
            Log.d("TAG", "Could not Delete Item: ${e.message}")
            throw e
        }
    }

}