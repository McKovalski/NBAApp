package com.example.myapplication.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlayerImage(
    @PrimaryKey
    val playerId: Int,
    val imageUrl: String,
    val imageCaption: String,
    val id: Int
)

data class PlayerImagePost(
    val playerId: Int,
    val imageUrl: String,
    val imageCaption: String
)
