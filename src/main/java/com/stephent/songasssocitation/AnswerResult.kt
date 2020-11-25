package com.stephent.songasssocitation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_answer_result.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.lang.StringBuilder
import kotlin.concurrent.thread
import androidx.core.app.ComponentActivity


import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception


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

        fetchJson()


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

    fun fetchJson(){
        println("Trying to fetch JSON")
        val url = "https://www.theaudiodb.com/api/v1/json/1/search.php?s=" + intent.getStringExtra(GET_ARTIST_FOR_SONG)

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object: okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response?) {
                val body = response?.body()?.string()
                println(body)

                val gson = GsonBuilder().create()

                val artistBody = gson.fromJson(body, ArtistInfo::class.java)
                println(artistBody)



                try {
                    if(artistBody != null){
                        var artistLink = artistBody.artists[0].strArtistThumb
                        val uiHandler = Handler(Looper.getMainLooper())
                        uiHandler.post(Runnable {
                            Picasso.get().load(artistLink).into(imageArtist)
                        })
                    }

                }catch (e: Exception){
                    println(e.message)
                }


            }
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                println("Failed: " + e.toString())
            }


        })
    }



}

class ArtistInfo(val artists: List<Artist>)