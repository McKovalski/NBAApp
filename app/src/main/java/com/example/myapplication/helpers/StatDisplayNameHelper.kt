package com.example.myapplication.helpers

class StatDisplayNameHelper {

    fun getDisplayName(stat: String): String {
        return when (stat) {
            "min" -> "Time played (MIN)"
            "fgm" -> "Field goals made (FGM)"
            "fga" -> "Field goals attempts (FGA)"
            "fg3m" -> "Field goal 3-pointers made (FG3M)"
            "fg3a" -> "Field goal 3-pointers attempts (FG3A)"
            "ftm" -> "Free throws made (FTM)"
            "fta" -> "Free throws attempts (FTA)"
            "oreb" -> "Offensive rebounds (OREB)"
            "dreb" -> "Defensive rebounds (DREB)"
            "reb" -> "Rebounds (REB)"
            "ast" -> "Assists (AST)"
            "stl" -> "Steals (STL)"
            "blk" -> "Blocks (BLK)"
            "turnover" -> "Turnover (TOV)"
            "pf" -> "Personal fouls (PF)"
            "pts" -> "Points (PTS)"
            "fg_pct" -> "Field goal percentage (FG%)"
            "fg3_pct" -> "Field goal 3-pointer percentage (FG3%)"
            "ft_pct" -> "Free throw percentage (FT%)"
            else -> ""
        }
    }
}