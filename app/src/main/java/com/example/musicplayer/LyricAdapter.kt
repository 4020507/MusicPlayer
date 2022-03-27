package com.example.musicplayer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.databinding.LyricBinding
import com.example.musicplayer.model.Lyric

class LyricAdapter(private val context: Context, private val lyrics: List<Lyric>) :
        RecyclerView.Adapter<LyricAdapter.ViewHolder>(){

    interface Clicked{
        fun onClick(view: View, time: Int)
    }
    var clicked:Clicked? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lyricBinding = LyricBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolder(lyricBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBinding(lyrics[position])

        if(clicked != null){
            holder.itemView.setOnClickListener { v->
                clicked?.onClick(v,lyrics[position].time)
            }
        }
    }

    override fun getItemCount(): Int {
        return lyrics.size
    }

    class ViewHolder(private val lyricBinding : LyricBinding) : RecyclerView.ViewHolder(lyricBinding.root){
        fun onBinding(lyric : Lyric){
            lyricBinding.lyric = lyric
        }
    }
}