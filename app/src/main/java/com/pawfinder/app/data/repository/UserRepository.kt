package com.pawfinder.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.pawfinder.app.data.local.dao.UserDao
import com.pawfinder.app.data.local.mappers.toUser
import com.pawfinder.app.data.local.mappers.toUserEntity
import com.pawfinder.app.model.User
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val userDao: UserDao
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    suspend fun getUserById(userId: String): User? {
        return try {
            val doc = usersCollection.document(userId).get().await()
            val user = doc.toObject(User::class.java)?.copy(id = doc.id)
            user?.let { userDao.insertUser(it.toUserEntity()) }
            user
        } catch (e: Exception) {
            userDao.getUserById(userId)?.toUser()
        }
    }

    suspend fun insertUser(user: User) {
        usersCollection.document(user.id).set(user).await()
        userDao.insertUser(user.toUserEntity())
    }

    suspend fun updateUser(user: User) {
        usersCollection.document(user.id).set(user).await()
        userDao.insertUser(user.toUserEntity())
    }

    suspend fun deleteUserById(userId: String) {
        usersCollection.document(userId).delete().await()
        userDao.deleteUserById(userId)
    }

    suspend fun deleteAllUsers() {
        userDao.deleteAllUsers()
    }
}