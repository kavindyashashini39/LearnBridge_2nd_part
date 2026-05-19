package com.app.learnbridge.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val level: String,
    val imageUrl: String,
    val isPremium: Boolean = false
)
