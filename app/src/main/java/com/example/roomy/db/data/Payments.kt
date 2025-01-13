package com.example.roomy.db.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Payments (
    @SerialName("payment_id")val id: Int,
    @SerialName("created´_at") val created: String,
    @SerialName("group_id")val groupId: Int,
    @SerialName("paid_by")val paidBy: Int,
    val amount: Int,
    val items: List<Int>
)