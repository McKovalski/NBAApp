package com.example.myapplication.models

data class Highlight(
    val eventId: Int,
    val name: String,
    val url: String,
    val playerIdList: List<Int>,
    val id: Int,
    val startTimestamp: Int
)

data class HighlightPost(
    val eventId: Int,
    val name: String,
    val url: String,
    val playerIdList: List<Int>? = null,
    val startTimestamp: Int
)