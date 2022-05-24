package com.example.myapplication.models

data class SeasonAverages(
    val games_played: Int,
    val player_id: Int,
    val season: Int,
    val min: String, // "34:46"
    val fgm: Float,
    val fg3m: Float,
    val fg3a: Float,
    val ftm: Float,
    val fta: Float,
    val oreb: Float,
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
) {
    fun getMinutes(): Float {
        val minutes = min.split(":")[0].toFloat()
        val seconds = min.split(":")[1].toFloat()

        return minutes + (seconds / 60f)
    }
}