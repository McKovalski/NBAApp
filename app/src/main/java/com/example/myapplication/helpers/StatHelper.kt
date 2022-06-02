package com.example.myapplication.helpers

import com.example.myapplication.models.Stats
import kotlin.math.roundToInt

class StatHelper {

    fun getFieldGoalPercentage(stats: List<Stats>, teamId: Int): Int {
        var totalFga = 0
        var totalFgm = 0
        for (stat in stats) {
            if (stat.team.id == teamId) {
                totalFga += stat.fga
                totalFgm += stat.fgm
            }
        }
        return ((totalFgm / totalFga.toFloat()) * 100).roundToInt()
    }

    fun getThreePointPercentage(stats: List<Stats>, teamId: Int): Int {
        var totalFg3a = 0
        var totalFg3m = 0
        for (stat in stats) {
            if (stat.team.id == teamId) {
                totalFg3a += stat.fg3a
                totalFg3m += stat.fg3m
            }
        }
        return ((totalFg3m / totalFg3a.toFloat()) * 100).roundToInt()
    }

    fun getStats(stats: List<Stats>, teamId: Int): Map<String, Int> {
        val statsMap = linkedMapOf(
            "fg_pct" to 0,
            "fg3_pct" to 0,
            "reb" to 0,
            "ast" to 0,
            "tov" to 0,
            "oreb" to 0,
        )
        var totalFga = 0
        var totalFgm = 0
        var totalFg3a = 0
        var totalFg3m = 0
        for (stat in stats) {
            if (stat.team.id == teamId) {
                totalFga += stat.fga
                totalFgm += stat.fgm
                totalFg3a += stat.fg3a
                totalFg3m += stat.fg3m
                statsMap["reb"] = statsMap["reb"]?.plus(stat.reb) ?: 0
                statsMap["ast"] = statsMap["ast"]?.plus(stat.ast) ?: 0
                statsMap["tov"] = statsMap["tov"]?.plus(stat.turnover) ?: 0
                statsMap["oreb"] = statsMap["oreb"]?.plus(stat.oreb) ?: 0
            }
        }
        statsMap["fg_pct"] = ((totalFgm / totalFga.toFloat()) * 100).roundToInt()
        statsMap["fg3_pct"] = ((totalFg3m / totalFg3a.toFloat()) * 100).roundToInt()

        return statsMap
    }
}