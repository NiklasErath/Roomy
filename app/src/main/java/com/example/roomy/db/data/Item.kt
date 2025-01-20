package com.example.roomy.db.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Item(
    @SerialName("item_id") val id: Int? = null,
    @SerialName("item_name") val name: String,
    @SerialName("group_id") val groupId: Int,
    @SerialName("status") val status: String,
    @SerialName("quantity") val quantity: Int ? = null,
    @SerialName("icon") val icon: Int ? = null,

    )