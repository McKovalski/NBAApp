package com.example.myapplication.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Player(
    @PrimaryKey
    val id: Int,
    val first_name: String,
    val last_name: String,
    val height_feet: Int?,
    val height_inches: Int?,
    val weight_pounds: Int?,
    val position: String,
    @Embedded val team: Team
) {
    fun fullName(): String {
        return "$first_name $last_name"
    }
}

@Entity
data class FavouritePlayer(
    @PrimaryKey
    val id: Int,
    val first_name: String,
    val last_name: String,
    val height_feet: Int?,
    val height_inches: Int?,
    val weight_pounds: Int?,
    val position: String,
    @Embedded val team: Team,
    val dbOrderPosition: Int
)

data class PlayerResponse(
    val data: List<Player>,
    val meta: Meta
)

data class Meta(
    val total_pages: Int,
    val current_page: Int,
    val next_page: Int?,
    val per_page: Int,
    val total_count: Int,
)