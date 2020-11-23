package com.stephent.songasssocitation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_answer_result.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.lang.StringBuilder
import kotlin.concurrent.thread
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




private const val GET_GUESS_FOR_SONG = "com.stephent.songassociation.guess_for_song"
private const val GET_TITLE_FOR_SONG = "com.stephent.songassociation.title_for_song"
private const val GET_ARTIST_FOR_SONG = "com.stephent.songassociation.artist_for_song"
private const val GET_CURRENT_SCORE = "com.stephent.songassociation.get_current_score"

class AnswerResult : AppCompatActivity() {

    private lateinit var answerTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer_result)


//        answerTextView = findViewById(R.id.theSpeechResult)
//        theSpeechResult.setText(intent.getStringExtra(GET_GUESS_FOR_SONG))

        foundSong.setText(intent.getStringExtra(GET_TITLE_FOR_SONG))

        foundArtist.setText(intent.getStringExtra(GET_ARTIST_FOR_SONG))




        next_question_button.setOnClickListener {
            var currentScore = intent.getIntExtra(GET_CURRENT_SCORE, 1) + 1
            println("Current score: " + currentScore)
            val intent = SpeechToText.newIntent(this@AnswerResult, currentScore)

            startActivity(intent)
        }

    }

    companion object {

        fun newIntent(
            packageContext: Context,
            songGuess: String,
            songTitle: String,
            songArtist: String,
            currentScore: Int
        ): Intent {
            return Intent(packageContext, AnswerResult::class.java).apply {
                putExtra(GET_GUESS_FOR_SONG, songGuess)
                putExtra(GET_TITLE_FOR_SONG, songTitle)
                putExtra(GET_ARTIST_FOR_SONG, songArtist)
                putExtra(GET_CURRENT_SCORE, currentScore)
            }
        }
    }



}