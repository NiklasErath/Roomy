package com.example.roomy.ui.States

import com.example.roomy.db.data.Balance
import com.example.roomy.db.data.Item
import com.example.roomy.db.data.Payments
import com.example.roomy.db.data.UserInformation

data class GroupState (
    val groupId: Int,
    val groupName: String,
    val creatorId: String,
    val groupMembers: List<UserInformation>,
    val shoppingListItems: List<Item> = emptyList(),  // This could be a list of shopping items or inventory items
    val inventoryItems: List<Item> = emptyList(),
    val itemCount: Int,
    val payments: List<Payments> = emptyList(),
    val balances: List<Balance> = emptyList()
)