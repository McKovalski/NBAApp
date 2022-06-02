package com.example.myapplication.network.models

import com.example.myapplication.models.*

data class PlayerResponse(
    val data: List<Player>,
    val meta: Meta
)

data class TeamsResponse(
    val data: List<Team>,
    val meta: Meta
)

data class SeasonAveragesResponse(
    val data: List<SeasonAverages>?
)

data class StatsResponse(
    val data: List<Stats>?,
    val meta: Meta
)

data class PlayerImagesResponse(
    val data: List<PlayerImage>?
)

data class MatchesResponse(
    val data: List<Match>,
    val meta: Meta
)

data class Meta(
    val total_pages: Int,
    val current_page: Int,
    val next_page: Int?,
    val per_page: Int,
    val total_count: Int,
)