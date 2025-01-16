package com.example.roomy.db.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Balance(
    @SerialName("balance_id") val id: Int,
    @SerialName("created_at") val created: String,
    @SerialName("group_id") val groupId: Int,
    @SerialName("user_owes") val userOwes: String,
    @SerialName("user_lent") val userLent: String,
    val amount: Int,
)