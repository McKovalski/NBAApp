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