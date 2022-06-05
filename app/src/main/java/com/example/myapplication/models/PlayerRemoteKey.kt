package com.example.myapplication.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlayerRemoteKey(
    @PrimaryKey
    val id: Int,
    val prevKey: Int?,
    val nextKey: Int?
)