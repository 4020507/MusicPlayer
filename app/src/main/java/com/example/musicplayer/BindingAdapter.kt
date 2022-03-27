package com.example.musicplayer

import android.graphics.Typeface
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object BindingAdapter {
    @BindingAdapter("imageUrl")
    @JvmStatic
    fun loadImage(imageView: ImageView, url:String?){
        Glide.with(imageView.context)
            .load(url)
            .placeholder(R.color.white)
            .error(R.color.white)
            .into(imageView)
    }

    @BindingAdapter("songTime")
    @JvmStatic
    fun mSec(view: TextView, mSec: Int) {
        val mSec = mSec.toLong()
        val hours: Long = mSec / 60 / 60 % 24
        val minutes: Long = mSec / 60 % 60
        val seconds: Long = mSec % 60

        if (mSec < 3600000) {
            view.text = String.format("%02d:%02d", minutes, seconds)
        } else {
            view.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
    }

    @BindingAdapter("highlighted")
    @JvmStatic
    fun bold(view: TextView, highlighted: Boolean){
        when(highlighted){
            true -> view.setTypeface(null,Typeface.BOLD)
            false -> view.setTypeface(null,Typeface.NORMAL)
        }
    }

    @BindingAdapter("highlightedSize")
    @JvmStatic
    fun highlightedSize(view: TextView, highlighted: Boolean){
        when(highlighted){
            true -> view.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20.toFloat())
            false -> view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15.toFloat())
        }
    }
}