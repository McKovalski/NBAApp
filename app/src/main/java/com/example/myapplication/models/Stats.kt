package com.example.myapplication.models

data class Stats(
    val id: Int?,
    val min: String?, // "34:46"
    val fgm: Int?,
    val fga: Int?,
    val fg3m: Int?,
    val fg3a: Int?,
    val ftm: Int?,
    val fta: Int?,
    val oreb: Int?,
    val dreb: Int?,
    val reb: Int?,
    val ast: Int?,
    val stl: Int?,
    val blk: Int?,
    val turnover: Int?,
    val pf: Int?,
    val pts: Int?,
    val fg_pct: Float?,
    val fg3_pct: Float?,
    val ft_pct: Float?,
    val game: MatchInStat,
    val player: Player,
    val team: Team
)