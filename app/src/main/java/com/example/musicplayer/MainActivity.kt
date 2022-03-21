package com.example.musicplayer

import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.SeekBar
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
import kotlin.properties.Delegates

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

        //marquee text setting
        val title = findViewById<TextView>(R.id.title)
        val singer = findViewById<TextView>(R.id.singer)
        val album = findViewById<TextView>(R.id.album)
        title.setSelected(true)
        singer.setSelected(true)
        album.setSelected(true)

        val mediaPlayer = MediaPlayer()
        val playBtn = findViewById<ImageView>(R.id.playBtn)
        val pauseBtn = findViewById<ImageView>(R.id.pauseBtn)
        val totalTime = findViewById<TextView>(R.id.TotalTime)

        fun playMusic(){
            playBtn.visibility = INVISIBLE
            pauseBtn.visibility = VISIBLE
            mediaPlayer.start()
        }

        fun pauseMusic(){
            playBtn.visibility = VISIBLE
            pauseBtn.visibility = INVISIBLE
            mediaPlayer.pause()
        }

        playBtn.setOnClickListener{
            playMusic()
        }

        pauseBtn.setOnClickListener {
            pauseMusic()
        }

        //seekbar setting
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        val playingTime = findViewById<TextView>(R.id.playingTime)
        var duration by Delegates.notNull<String>()
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            var progress: Int?= null
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(p2) {
                    val converted = timeToMiliseconds((duration.toInt() * p1 / 100).toString())
                    progress = p1
                    playingTime.text = milisecondsToTime(converted)
                    seekBar.setProgress(p1)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                mediaPlayer.seekTo(progress!!)
                if(mediaPlayer.isPlaying){
                    mediaPlayer.seekTo(seekBar.progress)
                }
            }

        })
        service.getSongInfo().enqueue(object:Callback<SongInfo>{
            override fun onResponse(call: Call<SongInfo>, response: Response<SongInfo>) {
                Log.d("retrofit: ",response.body().toString())
                val body = response.body()

                when(response.code()){
                    200 -> {
                        if(body != null)
                        {
                            binding.songInfo = SongInfo(
                                body.singer,
                                body.album,
                                body.title,
                                body.duration,
                                body.image,
                                body.file,
                                body.lyrics
                            )
                            duration = body.duration.toString()
                            totalTime.text = milisecondsToTime(mediaPlayer.duration.toLong())
                            mediaPlayer.apply {
                                setDataSource(body.file)
                                prepare()
                                playMusic()
                            }
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

fun milisecondsToTime(miliseconds:Long): String {
    val hours = miliseconds / 60 / 60 % 24
    val minutes = miliseconds / 60 % 60
    val seconds = miliseconds % 60
    val returnStr : String
    if(miliseconds < 3600000){
        returnStr = String.format("%02d:%02d", minutes, seconds)
    }
    else returnStr = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    return returnStr
}

fun timeToMiliseconds(time:String): Long {
    val timeFormat = time.split("[")[1].split("]")[0].split(":")
    val min = timeFormat[0].toInt()
    val sec = timeFormat[1].toInt()
    val msec = timeFormat[2].toInt()

    return (min * 60000 + sec * 1000 + msec).toLong()
}