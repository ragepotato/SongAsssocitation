package com.stephent.songasssocitation

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
import kotlinx.android.synthetic.main.popup_choose_word.view.*
import kotlinx.android.synthetic.main.toast_nextplayer.view.*
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception


private const val GET_GUESS_FOR_SONG = "com.stephent.songassociation.guess_for_song"
private const val GET_LYRICS_FOR_SONG = "com.stephent.songassociation.guess_for_song"
private const val GET_TITLE_FOR_SONG = "com.stephent.songassociation.title_for_song"
private const val GET_ARTIST_FOR_SONG = "com.stephent.songassociation.artist_for_song"
private const val GET_CURRENT_SCORE = "com.stephent.songassociation.get_current_score"

class PartyAnswerResult : AppCompatActivity() {

    private lateinit var answerTextView: TextView
    private lateinit var clockPlayer : MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer_result)



        foundSong.setText(intent.getStringExtra(GET_TITLE_FOR_SONG))

        foundArtist.setText(intent.getStringExtra(GET_ARTIST_FOR_SONG))

        var foundLyricText = "\"" + intent.getStringExtra(GET_LYRICS_FOR_SONG) + "\""
        foundLyrics.setText(Html.fromHtml(foundLyricText))
        var currentScore = intent.getIntExtra(GET_CURRENT_SCORE, 1)
        var currentPlayer = GameViewModel.playerList.get(currentScore)


        fetchJson()

        if (savedInstanceState == null){
            clockPlayer = MediaPlayer.create(this, R.raw.correctanswer)
            clockPlayer.setVolume(0.01F, 0.01F)
            clockPlayer.start()
        }


        congratsScore.setText("Good job " + currentPlayer + "!")

        next_question_button.setOnClickListener {

            val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_choose_word, null)
            val mBuilder = AlertDialog.Builder(this).setView(dialogView)
            val alertDialogView = mBuilder.show()
            val readyButton = alertDialogView.findViewById<Button>(R.id.ready_start_button)





            dialogView.current_player_label.text = currentPlayer
            readyButton.setOnClickListener {

                alertDialogView.dismiss()


                var currentWord = dialogView.word_for_next.text.toString()
                println(currentWord)
                val toastLayout = layoutInflater.inflate(R.layout.toast_nextplayer, null)

                GameViewModel.playerMap.put(currentPlayer,
                    GameViewModel.playerMap.get(currentPlayer)?.plus(1)!!
                )

                println(GameViewModel.playerMap)
                println(GameViewModel.playerMap)


                currentScore = currentScore + 1

                if (currentScore == GameViewModel.playerList.size) {
                    currentScore = 0
                    GameViewModel.shufflePlayerList()
                    while (currentPlayer == GameViewModel.playerList.get(0)){
                        GameViewModel.shufflePlayerList()
                    }
                    println(GameViewModel.playerList)
                    GameViewModel.currentRound = GameViewModel.currentRound + 1
                }

                Toast(this).apply{
                    toastLayout.player_guess_label.text = GameViewModel.playerList.get(currentScore)
                    duration = Toast.LENGTH_LONG
                    setGravity(Gravity.CENTER, 0,0)
                    view = toastLayout

                }.show()


                val intent = PartySpeechToText.newIntent(this@PartyAnswerResult, currentScore, currentWord)
                startActivity(intent)

                clockPlayer.stop()
                clockPlayer.release()

                finish()



            }
        }

    }

    companion object {

        fun newIntent(
            packageContext: Context,
            songGuess: String,
            songLyrics: String,
            songTitle: String,
            songArtist: String,
            currentScore: Int
        ): Intent {
            return Intent(packageContext, PartyAnswerResult::class.java).apply {
                putExtra(GET_GUESS_FOR_SONG, songGuess)
                putExtra(GET_LYRICS_FOR_SONG, songLyrics)
                putExtra(GET_TITLE_FOR_SONG, songTitle)
                putExtra(GET_ARTIST_FOR_SONG, songArtist)
                putExtra(GET_CURRENT_SCORE, currentScore)
            }
        }
    }

    fun fetchJson() {

        println("1Got herelololollo")
        if (foundArtist.text.toString() == "") {
            println("2Got herelololollo")
            val uiHandler = Handler(Looper.getMainLooper())
            uiHandler.post(Runnable {
                Picasso.get()
                    .load("https://webstockreview.net/images/mystery-clipart-anonymous-1.png")
                    .into(imageArtist)
            })
        } else {
            println("3Got herelololollo")
            println("Trying to fetch JSON")


            var stringFirstArtist = intent.getStringExtra(
                GET_ARTIST_FOR_SONG
            ).split("&")[0]

            val url =
                "https://www.theaudiodb.com/api/v1/json/1/search.php?s=" + stringFirstArtist


            val request = Request.Builder().url(url).build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response?) {
                    val body = response?.body()?.string()
                    //println(body)

                    val gson = GsonBuilder().create()

                    val artistBody = gson.fromJson(body, ArtistInfo::class.java)
                    //println(artistBody)


                    try {
                        if (artistBody != null) {
                            var artistLink = artistBody.artists[0].strArtistThumb
                            val uiHandler = Handler(Looper.getMainLooper())

                            uiHandler.post(Runnable {
                                Picasso.get().load(artistLink).into(imageArtist)
                            })
                        }

                    } catch (e: Exception) {
                        println(e.message)
//                    val uiHandler = Handler(Looper.getMainLooper())
//                    uiHandler.post(Runnable {
//                        Picasso.get().load(artistLink).into(imageArtist)
//                    })
                        val uiHandler = Handler(Looper.getMainLooper())
                        uiHandler.post(Runnable {
                            Picasso.get()
                                .load("https://webstockreview.net/images/mystery-clipart-anonymous-1.png")
                                .into(imageArtist)
                        })
                    }


                }

                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    println("Failed: " + e.toString())
                }


            })
        }
    }


}

