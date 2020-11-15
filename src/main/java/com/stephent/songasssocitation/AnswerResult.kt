package com.stephent.songasssocitation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_answer_result.*


private const val GET_ANSWER_FOR_SONG = "com.stephent.songassociation.answer_for_song"

class AnswerResult : AppCompatActivity(){

    private lateinit var answerTextView: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer_result)
        answerTextView = findViewById(R.id.theSpeechResult)
        theSpeechResult.setText(intent.getStringExtra(GET_ANSWER_FOR_SONG))

    }

    companion object{
        fun newIntent(packageContext: Context, songAnswer: String) : Intent{
            return Intent(packageContext, AnswerResult::class.java).apply{
                putExtra(GET_ANSWER_FOR_SONG, songAnswer)
            }
        }
    }
}