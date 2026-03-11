package com.pawfinder.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pawfinder.app.data.local.dao.PostDao
import com.pawfinder.app.data.local.dao.UserDao
import com.pawfinder.app.data.local.entities.PostEntity
import com.pawfinder.app.data.local.entities.UserEntity

@Database(
    entities = [PostEntity::class, UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao

    abstract fun userDao(): UserDao
}