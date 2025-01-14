package com.example.roomy.db.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupInformation (
    @SerialName("group_id") val id: Int? = null,
    @SerialName("created_at")val created: String? = null,
    @SerialName("group_name")val name: String,
    @SerialName("creator_id")val creatorId: String
)