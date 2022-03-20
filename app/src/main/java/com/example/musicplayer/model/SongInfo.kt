package com.example.musicplayer.model

data class SongInfo(
    val singer:String,
    val album:String,
    val title:String,
    val duration: Long,
    val image: String,
    val file: String,
    val lyrics: String
)
