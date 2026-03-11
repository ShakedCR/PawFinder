package com.pawfinder.app.data.local.mappers

import com.pawfinder.app.data.local.entities.UserEntity
import com.pawfinder.app.model.User

fun UserEntity.toUser(): User {
    return User(
        id = id,
        name = name,
        email = email,
        profileImageUrl = profileImageUrl
    )
}

fun User.toUserEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        profileImageUrl = profileImageUrl
    )
}