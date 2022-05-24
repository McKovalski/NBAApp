package com.example.myapplication.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Team(
    @PrimaryKey
    @ColumnInfo(name = "team_id")
    val id: Int,
    val abbreviation: String,
    val city: String,
    val conference: String,
    val division: String,
    val full_name: String,
    val name: String,
) : Serializable {

    fun toFavouriteTeam(position: Int): FavouriteTeam {
        return FavouriteTeam(
            this.id,
            this.abbreviation,
            this.city,
            this.conference,
            this.division,
            this.full_name,
            this.name,
            position
        )
    }
}

@Entity
data class FavouriteTeam(
    @PrimaryKey
    @ColumnInfo(name = "team_id")
    val id: Int,
    val abbreviation: String,
    val city: String,
    val conference: String,
    val division: String,
    val full_name: String,
    val name: String,
    val dbOrderPosition: Int
) {
    fun toTeam(): Team {
        return Team(
            this.id,
            this.abbreviation,
            this.city,
            this.conference,
            this.division,
            this.full_name,
            this.name
        )
    }
}