package com.pawfinder.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pawfinder.app.data.local.dao.PostDao
import com.pawfinder.app.data.local.dao.UserDao
import com.pawfinder.app.data.local.entities.PostEntity
import com.pawfinder.app.data.local.entities.UserEntity

@Database(
    entities = [PostEntity::class, UserEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE posts ADD COLUMN userEmail TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}