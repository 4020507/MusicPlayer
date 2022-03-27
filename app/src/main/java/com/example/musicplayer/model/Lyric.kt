package com.example.musicplayer.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.example.musicplayer.BR
class Lyric : BaseObservable() {

    @get:Bindable
    var time:Int = 0
        set(t){
            field = t
            notifyPropertyChanged(BR.time)
        }

    @get:Bindable
    var lyric: String = ""
        set(t){
            field = t
            notifyPropertyChanged(BR.lyric)
        }

    @get:Bindable
    var highlighted: Boolean = false
        set(t){
            field = t
            notifyPropertyChanged(BR.highlighted)
        }
}