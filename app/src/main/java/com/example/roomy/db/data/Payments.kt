package com.example.roomy.db.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Payments (
    @SerialName("payment_id")val id: Int? = null,
    @SerialName("created_at") val created: String? = null,
    @SerialName("group_id")val groupId: Int,
    @SerialName("paid_by")val paidBy: String,
    val amount: Int,
    val items: String
)