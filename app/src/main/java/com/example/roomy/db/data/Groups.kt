package com.example.roomy.db.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Groups(
    @SerialName("user_id") val userId: String,
    @SerialName("group_id") val groupId: Int,
)