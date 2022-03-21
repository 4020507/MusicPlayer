package com.example.musicplayer.model

import com.google.gson.annotations.SerializedName

data class SongInfo(
    @SerializedName("singer")
    val singer:String,
    @SerializedName("album")
    val album:String,
    @SerializedName("title")
    val title:String,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("file")
    val file: String,
    @SerializedName("lyrics")
    val lyrics: String
)
