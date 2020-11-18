package com.stephent.songasssocitation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    init {

    }
    var number = 0





    val currentIndex: MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }

    companion object{
        val easyQuestionBank = mutableListOf<String>(
            "love", "girl", "boy", "money", "like", "place", "you", "one", "face", "bad", "good", "friend", "way", "somebody", "someone"
        ).shuffled()
    }







}