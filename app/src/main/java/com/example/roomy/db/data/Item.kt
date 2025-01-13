package com.example.roomy.db.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    @SerialName("item_id") val id: Int,
    @SerialName("created_at") val created: String,
    @SerialName("item_name") val name: String,
)