package com.stephent.songasssocitation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.RecognizerIntent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_ingame.*
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.media.MediaPlayer
import android.speech.RecognitionListener
import android.view.Gravity
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_answer_result.*
import org.jsoup.Jsoup
import java.lang.StringBuilder
import java.util.*
import kotlin.concurrent.thread

private const val GET_CURRENT_SCORE = "com.stephent.songassociation.get_current_score"
private const val GET_CURRENT_WORD = "com.stephent.songassociation.get_current_word"


class PartySpeechToText : AppCompatActivity() {
    private val START_TIME: Long = GameViewModel.timeBank.toLong()
    private lateinit var gameViewModel: GameViewModel
    private lateinit var textviewCountdown: TextView

    private lateinit var countDownTimer: CountDownTimer
    private var timerRunning: Boolean = false
    private var timeLeftInMillis: Long = START_TIME
    private var timeEnd: Long = System.currentTimeMillis() + timeLeftInMillis
    private lateinit var clockPlayer: MediaPlayer


    private val REQUEST_CODE_SPEECH_INPUT = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        println("CURRENTLY IN PARTY MODE")


        if (GameViewModel.currentDifficulty.equals("HOLIDAY")) {
            setContentView(R.layout.activity_ingame_christmas)
        } else {
            setContentView(R.layout.activity_ingame)
        }





        gameViewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        textviewCountdown = findViewById(R.id.timer_countdown)
//        buttonStartPause = findViewById(R.id.button_start)
//        buttonReset = findViewById(R.id.button_stop)


        progress_countdown.max = (START_TIME / 1000).toInt()




        microphoneImage.setOnClickListener {
            getSpeechInput()
        }


        if (savedInstanceState == null) {
            startTimer()
            updateCountdown()

            println("---------lalallala here-----------------")
        }
        clockPlayer = MediaPlayer.create(this, R.raw.clockticking4)
        clockPlayer.start()

        println(GameViewModel.questionBank)

        var currentScore =  intent.getIntExtra(GET_CURRENT_SCORE, 0)
//        gameViewModel.currentIndex.observe(this, Observer {
//
//        })
        songWord.text = intent.getStringExtra(GET_CURRENT_WORD)
        currentScoreView.text = "Rd. " + GameViewModel.currentRound + ": " + GameViewModel.playerList[currentScore]
//        gameViewModel.number =
//            intent.getIntExtra(GET_CURRENT_SCORE, 0) % GameViewModel.questionBank.size
//
//        gameViewModel.currentIndex.value = gameViewModel.number


    }

    override fun onStart() {
        super.onStart()

    }


    override fun onDestroy() {
        println("Rotating")
        pauseTimer()


        super.onDestroy()
    }


    private fun startTimer() {
        timeEnd = System.currentTimeMillis() + timeLeftInMillis
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountdown()
            }

            override fun onFinish() {
                println("----------ENDING TIME-----------")
                timerRunning = false
                resetTimer()

            }


        }.start()
        timerRunning = true

    }

    private fun pauseTimer() {
        countDownTimer.cancel()
        timerRunning = false

    }

    private fun resetTimer() {
        timeLeftInMillis = START_TIME
        updateCountdown()
        progress_countdown.progress = 0
        progress_countdown.max = (START_TIME / 1000).toInt()
        val intent = PlayerLost.newIntent(
            this@PartySpeechToText,
            "Time ran out",
            intent.getIntExtra(GET_CURRENT_SCORE, 0)
        )
        startActivity(intent)
        clockPlayer.stop()
        clockPlayer.release()
        finish()

    }


    private fun updateCountdown() {
        val minutesLeft = (timeLeftInMillis / 1000) / 60
        val secondsLeft = (timeLeftInMillis / 1000) % 60
        //val secondsStr = secondsInMinuteUntilFinished.toString()
//        timer_countdown.text = "$minutesUntilFinished:${
//        if (secondsStr.length == 2) secondsStr
//        else "0" + secondsStr}"
        textviewCountdown.setText(
            String.format(
                Locale.getDefault(),
                "%02d:%02d",
                minutesLeft,
                secondsLeft
            )
        )
        println("Start time: " + START_TIME + " timeLeftInMillis: " + (START_TIME - timeLeftInMillis) + " secondsLeft: " + secondsLeft)
        progress_countdown.progress = ((START_TIME / 1000) - secondsLeft).toInt()


    }

    override fun onSaveInstanceState(outState: Bundle) {


        super.onSaveInstanceState(outState)
        outState.putLong("millisLeft", timeLeftInMillis)
        outState.putBoolean("timerRunning", timerRunning)
        outState.putLong("timeEnd", timeEnd)
        println("-------------TIMER CANCELLED---------------")
        if (clockPlayer.isPlaying) {
            clockPlayer.stop()
            clockPlayer.release()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        println("Gothereherhehrhehrher")
        timeLeftInMillis = savedInstanceState.getLong("millisLeft")
        timerRunning = savedInstanceState.getBoolean("timerRunning")
        updateCountdown()
        if (timerRunning) {
            timeEnd = savedInstanceState.getLong("timeEnd")
            timeLeftInMillis = timeEnd - System.currentTimeMillis()
            startTimer()
        }

    }

    fun getSpeechInput() {


        val micIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        //val  micIntent = Intent(RecognizerIntent.ACTION_WEB_SEARCH)
        micIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Sing!")
        micIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        try {
            clockPlayer.pause()
            startActivityForResult(micIntent, REQUEST_CODE_SPEECH_INPUT)


        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    pauseTimer()
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                    //speechResult.text = result[0]
                    val answer = result[0]



                    if (answer.contains(songWord.text, ignoreCase = true)) {
                        if (answer.split(" ").toTypedArray().size >= 5) {


                            thread {
                                // network call, so run it in the background
                                println(answer)

                                val stringLyric =
                                    answer.replace(" ", "%20").replace("'", "%27")
                                println(stringLyric)
                                println("https://www.google.com/search?q=" + stringLyric + "%20genius%20lyrics")
                                val doc =
                                    Jsoup.connect("https://www.google.com/search?q=" + stringLyric + "%20genius%20lyrics")
                                        .get()

                                lateinit var firstResultArtist: String
                                lateinit var firstResultSong: String
                                if (!doc.getElementsByAttributeValueContaining(
                                        "class",
                                        "LC20lb DKV0Md"
                                    ).isEmpty()
                                ) {
                                    val firstResultLink =
                                        doc.getElementsByAttributeValueContaining(
                                            "class",
                                            "LC20lb DKV0Md"
                                        )
                                            .first()
                                            .text()

                                    println(firstResultLink)

                                    var linkArray = firstResultLink.split(" – ").toTypedArray()
                                    println(linkArray[0])
                                    println("ggggggg" + linkArray[0] + "gggggggg")


                                    if (linkArray.size <= 1) {

                                        if (firstResultLink.split(" - ").toTypedArray().size > 1) {
                                            if (firstResultLink.split(" - ").toTypedArray()[1].equals(
                                                    "Genius"
                                                )
                                            ) {
                                                println("------------------grgergergege got here")
                                                firstResultArtist = ""
                                                firstResultSong =
                                                    firstResultLink.split(" - ").toTypedArray()[0]
                                            }
                                        } else {
                                            firstResultArtist = ""
                                            println("1111111firstResultArtist: " + firstResultArtist)

                                            println(linkArray.size)
                                            firstResultSong =
                                                linkArray[0].split(" Lyrics").toTypedArray()[0]
                                        }


                                    } else {
                                        firstResultArtist = linkArray[0]
                                        println("222222firstResultArtist: " + firstResultArtist)

                                        println(linkArray.size)
                                        firstResultSong =
                                            linkArray[1].split(" Lyrics").toTypedArray()[0]


                                    }
                                    var firstResultLyrics = ""
                                    if (doc.getElementsByAttributeValueContaining(
                                            "class",
                                            "aCOpRe"
                                        ).first().childrenSize() > 1
                                    ) {
                                        firstResultLyrics =
                                            doc.getElementsByAttributeValueContaining(
                                                "class",
                                                "aCOpRe"
                                            ).first().child(1).toString().replace(
                                                "Check @<em>genius</em> for updates. ... ",
                                                ""
                                            )
                                    } else {
                                        firstResultLyrics =
                                            doc.getElementsByAttributeValueContaining(
                                                "class",
                                                "aCOpRe"
                                            ).first().child(0).toString().replace(
                                                "Check @<em>genius</em> for updates. ... ",
                                                ""
                                            )
                                    }

                                    println(songWord.text.toString())

                                    println(firstResultLyrics)

                                    firstResultSong = firstResultSong.replace("Lyrics", "")

                                    println(firstResultLyrics)
                                    var theLyrics = firstResultLyrics.replace("Lyrics", "")
                                        .replace("<span>", "").replace("</span>", "").replace(
                                        Regex("\\[.*\\]"), ""
                                    ).replace("&nbsp;", "").replace("  ", " ")
                                        .replace("<em>", "<font color='#EE0000'>")
                                        .replace("</em>", "</font>")
                                    println(theLyrics)
                                    theLyrics = theLyrics.replace(
                                        songWord.text.toString(),
                                        "<u><b>" + songWord.text.toString() + "</b></u>",
                                        ignoreCase = true
                                    )
                                    println(theLyrics)

                                    if (firstResultArtist.equals("Genius")) {
                                        println("Artist is genius")
                                        firstResultArtist = ""
                                    }

                                    println("Song title: " + firstResultSong)
                                    println("Artist: " + firstResultArtist)
                                    val intent = PartyAnswerResult.newIntent(
                                        this@PartySpeechToText,
                                        answer,
                                        theLyrics,
                                        firstResultSong,
                                        firstResultArtist,
                                        intent.getIntExtra(GET_CURRENT_SCORE, 0)
                                    )

                                    startActivity(intent)
                                    clockPlayer.stop()
                                    clockPlayer.release()
                                    println(gameViewModel.currentIndex.value.toString())
                                    finish()


                                    // can't access UI elements from the background thread

                                } else {
                                    //song doesn't exist
                                    val intent = PlayerLost.newIntent(
                                        this@PartySpeechToText,
                                        "Song doesn't exist",
                                        intent.getIntExtra(GET_CURRENT_SCORE, 0)
                                    )
                                    startActivity(intent)
                                    clockPlayer.stop()
                                    clockPlayer.release()
                                    finish()
                                }
                            }


                        } else {
                            //not long enough
                            val intent = PlayerLost.newIntent(
                                this@PartySpeechToText,
                                "Not enough words",
                                intent.getIntExtra(GET_CURRENT_SCORE, 0)
                            )
                            startActivity(intent)
                            clockPlayer.stop()
                            clockPlayer.release()
                            finish()
                        }

                    } else {
                        //song doesn't use word
                        val intent = PlayerLost.newIntent(
                            this@PartySpeechToText,
                            "Didn't use needed word",
                            intent.getIntExtra(GET_CURRENT_SCORE, 0)
                        )
                        clockPlayer.stop()
                        clockPlayer.release()
                        startActivity(intent)
                        finish()
                    }


                } else if (resultCode != Activity.RESULT_OK) {
                    clockPlayer.start()
                    //speechResult.text = "nullFail"

                } else {
                    //speechResult.text = "failed"
                }
            }


        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (clockPlayer.isPlaying) {
            clockPlayer.stop()
            clockPlayer.release()
        }
    }

    companion object {
        fun newIntent(packageContext: Context, currentScore: Int, currentWord: String): Intent {
            return Intent(packageContext, PartySpeechToText::class.java).apply {
                putExtra(GET_CURRENT_SCORE, currentScore)
                putExtra(GET_CURRENT_WORD, currentWord)
            }
        }
    }

}


