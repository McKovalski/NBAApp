package com.example.myapplication.models

import java.io.Serializable

data class Match(
    val id: Int,
    val date: String, // "2022-05-27T00:00:00.000Z"
    val home_team_score: Int,
    val visitor_team_score: Int,
    val season: Int,
    val period: Int,
    val status: String,
    val time: String,
    val postseason: Boolean,
    val home_team: Team,
    val visitor_team: Team,
) : Serializable

data class MatchInStat(
    val id: Int,
    val date: String, // "2022-05-27T00:00:00.000Z"
    val home_team_score: Int,
    val visitor_team_score: Int,
    val season: Int,
    val period: Int,
    val status: String,
    val time: String,
    val postseason: Boolean,
    val home_team_id: Int,
    val visitor_team_id: Int,
) : Serializable {

    fun toMatch(home_team: Team, visitor_team: Team): Match {
        return Match(
            id,
            date,
            home_team_score,
            visitor_team_score,
            season,
            period, status, time, postseason, home_team, visitor_team
        )
    }
}