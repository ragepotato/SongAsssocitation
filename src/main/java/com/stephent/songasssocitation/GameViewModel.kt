package com.stephent.songasssocitation

import android.media.MediaPlayer
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


        var currentDifficulty = "EASY"

        var questionBank = mutableListOf<String>("dog")

        var timeBank = 30000

        var easyQuestionBank = mutableListOf<String>(
            "love", "girl", "boy", "money", "like", "place", "you", "one", "face", "bad", "good", "friend", "way", "somebody", "someone", "heart", "music"
        ).shuffled()





//        var easyQuestionBank = mutableListOf<String>(
//            "face"
//        ).shuffled()

        var mediumQuestionBank = mutableListOf<String>("kiss", "mine", "blue", "angel", "car", "God", "take").shuffled()

        var hardQuestionBank = mutableListOf<String>("count", "stolen", "color", "bird", "green").shuffled()

        fun setDifficulty (difficulty: String) {
            currentDifficulty = difficulty
            if (difficulty.equals("EASY")){
                easyQuestionBank = easyQuestionBank.shuffled()
                questionBank = easyQuestionBank as MutableList<String>
                timeBank = 30000
            }
            else if (difficulty.equals("MEDIUM")){
                mediumQuestionBank = mediumQuestionBank.shuffled()
                questionBank = mediumQuestionBank as MutableList<String>
                timeBank = 25000
            }
            else if (difficulty.equals("HARD")){
                hardQuestionBank = hardQuestionBank.shuffled()
                questionBank = hardQuestionBank as MutableList<String>
                timeBank = 20000
            }

        }


    }







}