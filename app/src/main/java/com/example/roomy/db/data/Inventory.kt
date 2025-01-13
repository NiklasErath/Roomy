package com.example.roomy.db.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Inventory(
    @SerialName("inventory_id") val id: Int,
    @SerialName("created_at")val created: String,
    @SerialName("group_id")val groupId: Int,
    @SerialName("item_id")val itemId: Int,
    @SerialName("item_quantity")val quantity: Int
)