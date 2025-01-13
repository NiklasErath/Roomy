package com.example.roomy.db.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//serialize to JSN
@Serializable
data class UserInformation(
    @SerialName("user_id") val id: String,
    val username: String,
    val email: String
)