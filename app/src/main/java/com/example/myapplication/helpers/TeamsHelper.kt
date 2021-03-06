package com.example.myapplication.helpers

import com.example.myapplication.R

class TeamsHelper() {

    fun getLogoAndColor(teamName: String?): Pair<Int, Int> {
        return when (teamName) {
            "76ers" -> Pair(R.drawable.ic_76ers, R.color.team_76_ers_primary)
            "Bucks" -> Pair(R.drawable.ic_bucks, R.color.team_bucks_primary)
            "Bulls" -> Pair(R.drawable.ic_bulls, R.color.team_bulls_primary)
            "Cavaliers" -> Pair(R.drawable.ic_cavaliers, R.color.team_cavaliers_primary)
            "Celtics" -> Pair(R.drawable.ic_celtics, R.color.team_celtics_primary)
            "Clippers" -> Pair(R.drawable.ic_clippers, R.color.team_clippers_primary)
            "Grizzlies" -> Pair(R.drawable.ic_grizzlies, R.color.team_grizzlies_primary)
            "Hawks" -> Pair(R.drawable.ic_hawks, R.color.team_hawks_primary)
            "Heat" -> Pair(R.drawable.ic_heat, R.color.team_heat_primary)
            "Hornets" -> Pair(R.drawable.ic_hornets, R.color.team_hornets_primary)
            "Jazz" -> Pair(R.drawable.ic_jazz, R.color.team_jazz_primary)
            "Kings" -> Pair(R.drawable.ic_kings, R.color.team_kings_primary)
            "Knicks" -> Pair(R.drawable.ic_knicks, R.color.team_knicks_primary)
            "Lakers" -> Pair(R.drawable.ic_lakers, R.color.team_lakers_primary)
            "Magic" -> Pair(R.drawable.ic_magic, R.color.team_magic_primary)
            "Mavericks" -> Pair(R.drawable.ic_mavericks, R.color.team_mavericks_primary)
            "Nets" -> Pair(R.drawable.ic_nets, R.color.team_nets_primary)
            "Nuggets" -> Pair(R.drawable.ic_nuggets, R.color.team_nuggets_primary)
            "Pacers" -> Pair(R.drawable.ic_pacers, R.color.team_pacers_primary)
            "Pelicans" -> Pair(R.drawable.ic_pelicans, R.color.team_pelicans_primary)
            "Pistons" -> Pair(R.drawable.ic_pistons, R.color.team_pistons_primary)
            "Raptors" -> Pair(R.drawable.ic_raptors, R.color.team_raptors_primary)
            "Rockets" -> Pair(R.drawable.ic_rockets, R.color.team_rockets_primary)
            "Spurs" -> Pair(R.drawable.ic_spurs, R.color.team_spurs_primary)
            "Suns" -> Pair(R.drawable.ic_suns, R.color.team_suns_primary)
            "Thunder" -> Pair(R.drawable.ic_thunder, R.color.team_thunder_primary)
            "Timberwolves" -> Pair(R.drawable.ic_timberwolves, R.color.team_timberwolves_primary)
            "Trail Blazers" -> Pair(R.drawable.ic_trailblazers, R.color.team_blazers_primary)
            "Warriors" -> Pair(R.drawable.ic_warriors, R.color.team_warriors_primary)
            "Wizards" -> Pair(R.drawable.ic_wizards, R.color.team_wizards_primary)
            else -> Pair(-1, -1)
        }
    }

    fun getTransparentColor(teamName: String?): Int {
        return when (teamName) {
            "76ers" -> R.color.team_76_ers_primary_alpha_0_5
            "Bucks" -> R.color.team_bucks_primary_alpha_0_5
            "Bulls" -> R.color.team_bulls_primary_alpha_0_5
            "Cavaliers" -> R.color.team_cavaliers_primary_alpha_0_5
            "Celtics" -> R.color.team_celtics_primary_alpha_0_5
            "Clippers" -> R.color.team_clippers_primary_alpha_0_5
            "Grizzlies" -> R.color.team_grizzlies_primary_alpha_0_5
            "Hawks" -> R.color.team_hawks_primary_alpha_0_5
            "Heat" -> R.color.team_heat_primary_alpha_0_5
            "Hornets" -> R.color.team_hornets_primary_alpha_0_5
            "Jazz" -> R.color.team_jazz_primary_alpha_0_5
            "Kings" -> R.color.team_kings_primary_alpha_0_5
            "Knicks" -> R.color.team_knicks_primary_alpha_0_5
            "Lakers" -> R.color.team_lakers_primary_alpha_0_5
            "Magic" -> R.color.team_magic_primary_alpha_0_5
            "Mavericks" -> R.color.team_mavericks_primary_alpha_0_5
            "Nets" -> R.color.team_nets_primary_alpha_0_5
            "Nuggets" -> R.color.team_nuggets_primary_alpha_0_5
            "Pacers" -> R.color.team_pacers_primary_alpha_0_5
            "Pelicans" -> R.color.team_pelicans_primary_alpha_0_5
            "Pistons" -> R.color.team_pistons_primary_alpha_0_5
            "Raptors" -> R.color.team_raptors_primary_alpha_0_5
            "Rockets" -> R.color.team_rockets_primary_alpha_0_5
            "Spurs" -> R.color.team_spurs_primary_alpha_0_5
            "Suns" -> R.color.team_suns_primary_alpha_0_5
            "Thunder" -> R.color.team_thunder_primary_alpha_0_5
            "Timberwolves" -> R.color.team_timberwolves_primary_alpha_0_5
            "Trail Blazers" -> R.color.team_blazers_primary_alpha_0_5
            "Warriors" -> R.color.team_warriors_primary_alpha_0_5
            "Wizards" -> R.color.team_wizards_primary_alpha_0_5
            else -> -1
        }
    }

    fun getDivisionName(division: String): Int {
        return when (division) {
            "Atlantic" -> R.string.atlantic
            "Central" -> R.string.central
            "Northwest" -> R.string.northwest
            "Pacific" -> R.string.pacific
            "Southeast" -> R.string.southeast
            else -> R.string.southwest
        }
    }

    fun getConferenceName(conference: String): Int {
        return when (conference) {
            "West" -> R.string.western
            else -> R.string.eastern
        }
    }

    fun getModelConferenceName(conference: String): Int {
        return when (conference) {
            "Western" -> R.string.west
            else -> R.string.east
        }
    }
}