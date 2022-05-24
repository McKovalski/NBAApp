package com.example.myapplication.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

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
) : Serializable {
    fun fullName(): String {
        return "$first_name $last_name"
    }

    fun heightImperial(): String {
        return "$height_feet\'$height_inches\""
    }

    fun fullPosition(): String {
        return when (position) {
            "G" -> "Guard"
            "F" -> "Forward"
            "C" -> "Center"
            "G-F" -> "Guard-Forward"
            "F-G" -> "Forward-Guard"
            "C-F" -> "Center-Forward"
            "F-C" -> "Forward-Center"
            else -> "N/A"
        }
    }

    fun toFavouritePlayer(position: Int): FavouritePlayer {
        return FavouritePlayer(
            this.id,
            this.first_name,
            this.last_name,
            this.height_feet,
            this.height_inches,
            this.weight_pounds,
            this.position,
            this.team,
            position
        )
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
) {
    fun toPlayer(): Player {
        return Player(
            this.id,
            this.first_name,
            this.last_name,
            this.height_feet,
            this.height_inches,
            this.weight_pounds,
            this.position,
            this.team
        )
    }
}