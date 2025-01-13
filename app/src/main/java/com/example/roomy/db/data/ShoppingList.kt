package com.example.roomy.db.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ShoppingList (
    @SerialName("list_id") val id: Int,
    @SerialName("created_at") val created: String,
    @SerialName("item_id") val itemId: Int,
    @SerialName("group_id") val groupId: Int,
    val quantity:Int
)