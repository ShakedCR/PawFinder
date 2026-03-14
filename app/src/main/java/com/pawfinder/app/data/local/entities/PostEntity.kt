package com.pawfinder.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val userName: String,
    val userEmail: String = "",
    val userImageUrl: String,
    val petName: String,
    val petType: String,
    val status: String,
    val description: String,
    val imageUrl: String,
    val location: String,
    val timestamp: Long
)