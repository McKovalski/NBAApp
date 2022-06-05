package com.example.myapplication.helpers

import android.util.Log
import com.example.myapplication.models.Player
import com.example.myapplication.models.Stats
import com.example.myapplication.models.Team
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt

class StatHelper {

    fun getFieldGoalPercentage(stats: List<Stats>, teamId: Int): Int {
        var totalFga = 0
        var totalFgm = 0
        for (stat in stats) {
            if (stat.team.id == teamId && stat.min != null) {
                totalFga += stat.fga!!
                totalFgm += stat.fgm!!
            }
        }
        return ((totalFgm / totalFga.toFloat()) * 100).roundToInt()
    }

    fun getThreePointPercentage(stats: List<Stats>, teamId: Int): Int {
        var totalFg3a = 0
        var totalFg3m = 0
        for (stat in stats) {
            if (stat.team.id == teamId && stat.min != null) {
                totalFg3a += stat.fg3a!!
                totalFg3m += stat.fg3m!!
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
            if (stat.team.id == teamId && stat.min != null) {
                totalFga += stat.fga!!
                totalFgm += stat.fgm!!
                totalFg3a += stat.fg3a!!
                totalFg3m += stat.fg3m!!
                statsMap["reb"] = statsMap["reb"]?.plus(stat.reb!!) ?: 0
                statsMap["ast"] = statsMap["ast"]?.plus(stat.ast!!) ?: 0
                statsMap["tov"] = statsMap["tov"]?.plus(stat.turnover!!) ?: 0
                statsMap["oreb"] = statsMap["oreb"]?.plus(stat.oreb!!) ?: 0
            }
        }
        statsMap["fg_pct"] = ((totalFgm / totalFga.toFloat()) * 100).roundToInt()
        statsMap["fg3_pct"] = ((totalFg3m / totalFg3a.toFloat()) * 100).roundToInt()

        return statsMap
    }

    fun getTopPlayers(
        stats: List<Stats>,
        allTeams: List<Team>
    ): Map<String, List<Pair<Player, Int>>> {
        val topStats = linkedMapOf(
            "pts" to mutableListOf<Pair<Player, Int>>(),
            "fgm" to mutableListOf(),
            "fg3m" to mutableListOf(),
            "reb" to mutableListOf(),
            "ast" to mutableListOf(),
            "tov" to mutableListOf(),
            "oreb" to mutableListOf()
        )
        for (stat in stats) {
            if (stat.min != null) {
                val teamIndex = allTeams.map { t -> t.id }.indexOf(stat.team.id)
                val team = allTeams[teamIndex]
                val player = Player(
                    stat.player.id,
                    stat.player.first_name,
                    stat.player.last_name,
                    null,
                    null,
                    null,
                    stat.player.position,
                    team
                )
                topStats["pts"]?.add(Pair(player, stat.pts!!))
                topStats["fgm"]?.add(Pair(player, stat.fgm!!))
                topStats["fg3m"]?.add(Pair(player, stat.fg3m!!))
                topStats["reb"]?.add(Pair(player, stat.reb!!))
                topStats["ast"]?.add(Pair(player, stat.ast!!))
                topStats["tov"]?.add(Pair(player, stat.turnover!!))
                topStats["oreb"]?.add(Pair(player, stat.oreb!!))
            }
        }
        val sortedStats = topStats.mapValues {
            it.value.sortedBy { pair -> pair.second }
        }
        Log.d("top stats", sortedStats.toString())
        return sortedStats
    }
}