package com.example.roomy.db.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Balance(
    @SerialName("balance_id") val id: Int? = null,
    @SerialName("created_at") val created: String? = null,
    @SerialName("group_id") val groupId: Int,
    @SerialName("owed_by") val owedBy: String,
    @SerialName("owed_to") val owedTo: String,
    val amount: Double,
)