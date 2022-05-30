package com.example.myapplication.models

data class PlayerImage(
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