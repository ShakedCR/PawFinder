package com.pawfinder.app.data.local.mappers

import com.pawfinder.app.data.local.entities.PostEntity
import com.pawfinder.app.model.Post

fun PostEntity.toPost(): Post {
    return Post(
        id = id,
        userId = userId,
        userName = userName,
        userEmail = userEmail,
        userImageUrl = userImageUrl,
        petName = petName,
        petType = petType,
        status = status,
        description = description,
        imageUrl = imageUrl,
        location = location,
        timestamp = timestamp
    )
}

fun Post.toPostEntity(): PostEntity {
    return PostEntity(
        id = id,
        userId = userId,
        userName = userName,
        userEmail = userEmail,
        userImageUrl = userImageUrl,
        petName = petName,
        petType = petType,
        status = status,
        description = description,
        imageUrl = imageUrl,
        location = location,
        timestamp = timestamp
    )
}