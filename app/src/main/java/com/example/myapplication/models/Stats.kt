package com.example.myapplication.models

data class Stats(
    val min: String, // "34:46"
    val fgm: Float,
    val fga: Float,
    val fg3m: Float,
    val fg3a: Float,
    val ftm: Float,
    val fta: Float,
    val oreb: Float,
    val dreb: Float,
    val reb: Float,
    val ast: Float,
    val stl: Float,
    val blk: Float,
    val turnover: Float,
    val pf: Float,
    val pts: Float,
    val fg_pct: Float,
    val fg3_pct: Float,
    val ft_pct: Float,
    val game: Match,
    val player: Player,
    val team: Team
)