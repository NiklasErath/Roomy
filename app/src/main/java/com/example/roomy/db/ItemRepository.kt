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

    // get all Items for a list of groups the user is in - inventory and shoppingList and pair them
    suspend fun getAllItemsForGroups(groupIds: List<Int>): Pair<List<Item>, List<Item>>? {
        try {
            // get shopping list items
            val shoppingListItems = supabase
                .from("items")
                .select(columns = Columns.ALL)
                { filter { eq("status", "shoppingList"); isIn("group_id", groupIds) } }
                .decodeList<Item>()
            // get inventory items
            val inventoryItems = supabase
                .from("items")
                .select(columns = Columns.ALL)
                { filter { eq("status", "inventory"); isIn("group_id", groupIds) } }
                .decodeList<Item>()
            // pair them
            return Pair(shoppingListItems, inventoryItems)
        } catch (e: Exception) {
            Log.d("TAG", "Could not get the ShoppingLists: ${e.message}")
            return null
        }
    }

    // add Item to a List
    suspend fun addItem(item: Item): Item? {
        try {
            // store new Item
            val newItem = supabase.from("items")
                .insert(item) { select() }
                .decodeSingle<Item>()

            return newItem
        } catch (e: Exception) {
            Log.d("TAG", "Could not Add Item: ${e.message}")
            return null
        }
    }

    // update an the Item status if its in inventory or shopping list
    suspend fun updateItem(item: Item): Boolean {
        try {
            item.id?.let {
                supabase.from("items")
                    .update({ set("status", item.status) })
                { filter { eq("item_id", it) }
                }
            }
            return true
        } catch (e: Exception) {
            Log.d("TAG", "Could not Update Item: ${e.message}")
            return false
        }
    }


    // delete an item with item id
    suspend fun deleteItem(item: Item): Boolean {
        try {
            item.id?.let {
                supabase.from("items")
                    .delete { filter { eq("item_id", item.id) }
                }
            }
            return true
        } catch (e: Exception) {
            Log.d("TAG", "Could not Delete Item: ${e.message}")
            return false
        }
    }

}