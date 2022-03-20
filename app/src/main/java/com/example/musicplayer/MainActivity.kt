package com.example.musicplayer

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.model.SongInfo
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //dataBinding
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,R.layout.activity_main)

        //Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/2020-flo/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(SongInfoService::class.java)
        val title = findViewById<TextView>(R.id.title)
        val singer = findViewById<TextView>(R.id.singer)
        val album = findViewById<TextView>(R.id.album)
        title.setSelected(true)
        singer.setSelected(true)
        album.setSelected(true)

        service.getSongInfo().enqueue(object:Callback<SongInfo>{
            override fun onResponse(call: Call<SongInfo>, response: Response<SongInfo>) {
                Log.d("retrofit: ",response.body().toString())
                val body = response.body()

                when(response.code()){
                    200 -> {
                        if(body != null)
                        {
                            val file = body.file
                            binding.songInfo = SongInfo(
                                body.singer,
                                body.album,
                                body.title,
                                body.duration,
                                body.image,
                                body.file,
                                body.lyrics
                            )

                        }
                    }
                }
            }

            override fun onFailure(call: Call<SongInfo>, t: Throwable) {
                Log.d("retrofit: ","fail connection")
            }

        })

    }
}