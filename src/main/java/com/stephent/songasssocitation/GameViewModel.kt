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
            "love", "girl", "boy", "money", "like", "place", "you", "one", "face", "bad", "good", "friend", "way", "somebody", "someone", "heart", "music", "hand", "child", "end", "me", "us", "all", "look", "song",
            "want", "woman", "man", "let", "end", "fall", "who", "know", "thing", "dance", "back", "never", "time", "life", "best", "feel", "right", "get", "once", "people", "hate", "break", "night", "last", "don\'t",
            "please"
        ).shuffled()


        var playerMap : LinkedHashMap<String, Int>
                = LinkedHashMap<String, Int> ()
        var playerList = mutableListOf<String>()

        var currentRound = 1


//        var easyQuestionBank = mutableListOf<String>(
//            "face"
//        ).shuffled()

        var mediumQuestionBank = mutableListOf<String>(
            "kiss", "mine", "blue", "angel", "car", "God", "take", "grow", "kill", "hide", "forget", "head", "think", "eyes", "somewhere", "sound", "still", "keep", "show", "same", "die", "dream",
            "year", "hard", "home", "live", "hope", "only", "story", "dark", "radio", "care", "mean", "away", "world", "word", "little", "baby", "ain\'t", "Hell", "wait", "maybe", "game", "move",
            "body", "light", "fake", "wake", "help", "find"
        ).shuffled()

        var hardQuestionBank = mutableListOf<String>("count", "stolen", "color", "bird", "green", "brother", "picture", "imagine", "issue", "control", "hill", "land", "glory", "regret", "summer",
            "surprise", "matter", "rather", "thrill", "floor", "sorry", "perfect", "pray", "honey", "mind", "fire", "lonely", "enemy", "breathe", "touch", "danger", "groove", "worth", "honest", "without",
            "pressure"
        ).shuffled()

        var holidayQuestionBank = mutableListOf<String>("Christmas", "angel", "bells", "carol", "chestnuts", "Christmas", "family", "cold", "jingle", "gift", "Santa", "snow", "merry", "joy", "happy", "snowman", "heart", "year", "tree", "wish").shuffled()


        //var holidayQuestionBank = mutableListOf<String>("Christmas").shuffled()

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
            else if (difficulty.equals("HOLIDAY")){
                holidayQuestionBank = holidayQuestionBank.shuffled()
                questionBank = holidayQuestionBank as MutableList<String>
                timeBank = 30000
            }
            else if (difficulty.equals("PARTY")){
                currentRound = 1
                timeBank = 30000
                resetPlayerList()
            }

        }

        fun resetPlayerList(){
            playerMap.clear()
            playerList.clear()
            currentRound = 1
        }




        fun shufflePlayerList(){
            playerList = playerList.shuffled() as MutableList<String>
        }


    }







}