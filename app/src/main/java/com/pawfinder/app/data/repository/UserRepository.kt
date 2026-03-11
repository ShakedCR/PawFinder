package com.pawfinder.app.data.repository

import com.pawfinder.app.data.local.dao.UserDao
import com.pawfinder.app.data.local.mappers.toUser
import com.pawfinder.app.data.local.mappers.toUserEntity
import com.pawfinder.app.model.User

class UserRepository(
    private val userDao: UserDao
) {

    suspend fun getUserById(userId: String): User? {
        return userDao.getUserById(userId)?.toUser()
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(user.toUserEntity())
    }

    suspend fun deleteUserById(userId: String) {
        userDao.deleteUserById(userId)
    }

    suspend fun deleteAllUsers() {
        userDao.deleteAllUsers()
    }
}