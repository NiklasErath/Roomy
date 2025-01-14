package com.example.roomy.ui.States

data class ShoppingListState (
    val id: Int,
    val created: String,
    val itemIds: List<Int>,
    val groupId: Int,
    val quantity: Int,
)