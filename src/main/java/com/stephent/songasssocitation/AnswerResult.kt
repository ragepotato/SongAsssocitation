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





private const val GET_ANSWER_FOR_SONG = "com.stephent.songassociation.answer_for_song"

class AnswerResult : AppCompatActivity(){

    private lateinit var answerTextView: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer_result)
        answerTextView = findViewById(R.id.theSpeechResult)
        theSpeechResult.setText(intent.getStringExtra(GET_ANSWER_FOR_SONG))
        retrieveWebInfo()
        continuePlaying()


    }

    companion object{
        fun newIntent(packageContext: Context, songAnswer: String) : Intent{
            return Intent(packageContext, AnswerResult::class.java).apply{
                putExtra(GET_ANSWER_FOR_SONG, songAnswer)
            }
        }
    }



    private fun continuePlaying(){
        next_question_button.setOnClickListener {
            val intent = Intent(this, SpeechToText::class.java)
            startActivity(intent)
        }
    }

    private fun retrieveWebInfo(){
        lateinit var builder : StringBuilder
        thread{
            // network call, so run it in the background


            val stringLyric = intent.getStringExtra(GET_ANSWER_FOR_SONG).replace(" ", "%20").replace("'", "%27")
            println(stringLyric)
            println("https://www.google.com/search?q=" + stringLyric)
            val doc =
                Jsoup.connect("https://www.google.com/search?q=" + stringLyric + "%20lyrics" )
                    .get()

            val firstResultSong = doc.getElementsByAttributeValueContaining("class", "qrShPb kno-ecr-pt PZPZlf mfMhoc").first().text()
            println(firstResultSong)
            val firstResultArtist = doc.getElementsByAttributeValueContaining("class", "wwUB2c PZPZlf").first().text()
            println(firstResultSong + " - " + firstResultArtist)


//            val firstResult = doc.getElementsByAttributeValueContaining("class", "lyric-meta within-lyrics").first()
//
//
//            val textSong = firstResult.getElementsByClass("lyric-meta-title").select("a").text()
//
//            var textArtist = ""
//            if (!firstResult.getElementsByAttributeValueContaining("class", "lyric-meta-artists").isEmpty()){
//                textArtist = firstResult.getElementsByClass("lyric-meta-artists").select("a").text()
//
//            }
//            else if (!firstResult.getElementsByAttributeValueContaining("class", "lyric-meta-album-artist").isEmpty()){
//                textArtist = firstResult.getElementsByClass("lyric-meta-album-artist").select("a").text()
//            }
//            else{
//                textArtist = "unknown"
//            }






            //val textElements = doc.text()

            //val imageUrl = imageElements[0].absUrl("src")

            // can't access UI elements from the background thread
            this.runOnUiThread{
                //foundSong.setText(textElements[0].text())
                foundSong.text = firstResultSong + " - " + firstResultArtist
                //Picasso.get().load(imageUrl).into(imgTitle)

            }
        }
    }
}