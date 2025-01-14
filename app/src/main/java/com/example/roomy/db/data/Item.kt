package com.example.roomy.db.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    @SerialName("item_id") val id: Int,
    @SerialName("group_id") val groupId: Int,
    @SerialName("item_name") val name: String,
    val status: String,
    val quantity: Int,
    val icon: String,
)