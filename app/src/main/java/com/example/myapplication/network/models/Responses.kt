package com.example.myapplication.network.models

import com.example.myapplication.models.Player
import com.example.myapplication.models.SeasonAverages
import com.example.myapplication.models.Team

data class PlayerResponse(
    val data: List<Player>,
    val meta: Meta
)

data class TeamsResponse(
    val data: List<Team>,
    val meta: Meta
)

data class SeasonAveragesResponse(
    val data: List<SeasonAverages>
)

data class Meta(
    val total_pages: Int,
    val current_page: Int,
    val next_page: Int?,
    val per_page: Int,
    val total_count: Int,
)