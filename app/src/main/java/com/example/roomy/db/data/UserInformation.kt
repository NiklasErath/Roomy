package com.example.roomy.db.data

import kotlinx.serialization.Serializable

//serialize to JSN
@Serializable
data class UserInformation(
    val id: String,
    val username: String,
    val email: String
)