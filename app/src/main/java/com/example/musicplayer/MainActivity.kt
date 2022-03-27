package com.example.musicplayer

import android.app.ActionBar
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.model.Lyric
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

        //seekbar setting
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        val playingTime = findViewById<TextView>(R.id.playingTime)
        var duration by Delegates.notNull<String>()

        //lyrics setting
        var lyricList: MutableList<Lyric> = mutableListOf()
        var index = -1
        val lyricAdapter = LyricAdapter(this,lyricList)
        val recyclerView = findViewById<RecyclerView>(R.id.lyrics)
        val image = findViewById<ImageView>(R.id.image)
        var lyricSelectedMode = false

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            var progress: Int?= null
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(p2) {
                    progress = p1
                    playingTime.text = milisecondsToTime((p1.toLong()/1000.toLong()))
                    seekBar.setProgress(p1)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                progress?.let { mediaPlayer.seekTo(it) }
                if(mediaPlayer.isPlaying){
                    mediaPlayer.seekTo(seekBar.progress)
                }
                if(index >= 0) lyricList[index].highlighted = false
            }

        })


        recyclerView.layoutManager = LinearLayoutManager(this)
        val params = recyclerView.layoutParams
        params.height = 350
        recyclerView.layoutParams = params
        val closeBtn = findViewById<ImageButton>(R.id.closeBtn)
        val toggleBtn = findViewById<ImageButton>(R.id.toggleBtn)
        lyricAdapter.clicked = object : LyricAdapter.Clicked{
            override fun onClick(view: View, time: Int) {
                Log.d("","hi")

                if(closeBtn.visibility == GONE)
                {
                    closeBtn.visibility = VISIBLE
                    toggleBtn.visibility = VISIBLE
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT
                    recyclerView.layoutParams = params
                    title.visibility = GONE
                    singer.visibility = GONE
                    album.visibility = GONE
                    image.visibility = GONE
                }
                else
                {
                    if(lyricSelectedMode){
                        playingTime.text = milisecondsToTime(time.toLong()/1000.toLong())
                        mediaPlayer.seekTo(time)
                    }
                }
            }

        }
        recyclerView.adapter = lyricAdapter

        closeBtn.setOnClickListener {
            closeBtn.visibility = GONE
            toggleBtn.visibility = GONE
            params.height = 350
            recyclerView.layoutParams = params
            title.visibility = VISIBLE
            singer.visibility = VISIBLE
            album.visibility = VISIBLE
            image.visibility = VISIBLE
            lyricSelectedMode = false
            toggleBtn.setImageResource(R.drawable.notselected)
        }

        toggleBtn.setOnClickListener { lyricSelectedMode = !lyricSelectedMode
            if(lyricSelectedMode){
                toggleBtn.setImageResource(R.drawable.selected)
            }else{
                toggleBtn.setImageResource(R.drawable.notselected)
            }
        }
        class MusicThread : Thread() {
            override fun run() {
                while(mediaPlayer.isPlaying){
                    SystemClock.sleep(100)
                    runOnUiThread {
                        val currTime = mediaPlayer.currentPosition
                        seekBar.setProgress(currTime)
                        playingTime.text = milisecondsToTime(currTime.toLong()/1000.toLong())

                        var temp = currLyricIndex(lyricList,mediaPlayer.currentPosition)

                        if(index != temp){
                            lyricList[temp].highlighted = true

                            if(index >= 0)
                                lyricList[index].highlighted = false
                            index = temp
                            (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(temp,recyclerView.height/3)
                        }
                    }
                }
            }
        }

        fun playMusic(){
            playBtn.visibility = INVISIBLE
            pauseBtn.visibility = VISIBLE
            mediaPlayer.start()
            MusicThread().start()
        }

        fun pauseMusic(){
            playBtn.visibility = VISIBLE
            pauseBtn.visibility = INVISIBLE
            mediaPlayer.pause()
            MusicThread().interrupt()
        }

        playBtn.setOnClickListener{
            playMusic()
        }

        pauseBtn.setOnClickListener {
            pauseMusic()
        }
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

                            for(it in body.lyrics.split("\n")){
                                var lyric = Lyric()
                                lyric.time = timeToMiliseconds(it)
                                lyric.lyric = it.split("]")[1]
                                lyricList.add(lyric)
                            }
                            seekBar.max = mediaPlayer.duration
                            totalTime.text = milisecondsToTime(mediaPlayer.duration.toLong())

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

fun timeToMiliseconds(time:String): Int {
    val timeFormat = time.split("[")[1].split("]")[0].split(":")
    val min = timeFormat[0].toInt()
    val sec = timeFormat[1].toInt()
    val msec = timeFormat[2].toInt()

    return (min * 60000 + sec * 1000 + msec)
}

fun currLyricIndex(lyric: List<Lyric>, index:Int):Int{
    var start = 0
    var mid = 0
    var end = lyric.size - 1

    if(index >= lyric[end].time) return end

    while(end > start){
        mid = (start+end)/2

        if(lyric[mid].time >= index ){
            end = mid
        }
        else
            start = mid + 1
    }

    if(end == 0) return 0
    else    return end - 1
}