package com.pawfinder.app.model

data class Post(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userImageUrl: String = "",
    val petName: String = "",
    val petType: String = "",
    val status: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val location: String = "",
    val timestamp: Long = 0L
)