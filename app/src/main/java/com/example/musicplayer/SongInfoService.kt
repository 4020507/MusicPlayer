package com.example.musicplayer

import com.example.musicplayer.model.SongInfo
import retrofit2.Call
import retrofit2.http.GET

interface SongInfoService {
    @GET("song.json")
    fun getSongInfo(): Call<SongInfo>
}