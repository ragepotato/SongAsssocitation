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
import com.stephent.songasssocitation.util.TimerPreferences
import kotlinx.android.synthetic.main.activity_ingame.*
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_answer_result.*
import org.jsoup.Jsoup
import java.lang.StringBuilder
import kotlin.concurrent.thread

private const val GET_CURRENT_SCORE = "com.stephent.songassociation.get_current_score"


class SpeechToText : AppCompatActivity() {
    lateinit var gameViewModel: GameViewModel


    enum class TimeState {
        Stopped, Paused, Running
    }


    private val REQUEST_CODE_SPEECH_INPUT = 100
    private lateinit var timer: CountDownTimer
    private var timeState = TimeState.Stopped
    private var timeSeconds = 0L

    private var secondsRemaining = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingame)

        gameViewModel = ViewModelProvider(this).get(GameViewModel::class.java)



        button_start.setOnClickListener { v ->
            startTimer()
            timeState = TimeState.Running
            updateButtons()

        }

        button_pause.setOnClickListener { v ->
            timer.cancel()
            timeState = TimeState.Paused
            updateButtons()

        }

        button_stop.setOnClickListener { v ->
            timer.cancel()
            onTimerFinished()

        }


        microphoneImage.setOnClickListener {
            getSpeechInput()
        }
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }


        println(GameViewModel.easyQuestionBank)
        gameViewModel.currentIndex.observe(this, Observer {
            songWord.text = GameViewModel.easyQuestionBank[it]
            currentScoreView.text = "Score: " + gameViewModel.currentIndex.value.toString()
        })

        gameViewModel.number = intent.getIntExtra(GET_CURRENT_SCORE, 0)

        gameViewModel.currentIndex.value = gameViewModel.number


    }

    override fun onResume() {
        super.onResume()
        initTimer()
        //TODO: remove background timer
    }

    override fun onPause() {
        super.onPause()
        if (timeState == TimeState.Running) {
            timer.cancel()
        } else if (timeState == TimeState.Paused) {

        }
        //TimerPreferences.setPreviousTimerLengthSeconds(timeSeconds, this)
        //TimerPreferences.setSecondsRemaining(secondsRemaining, this)
        //TimerPreferences.setTimeState(timeState, this)

    }

    private fun initTimer() {
        //timeState = TimerPreferences.getTimeState(this)

        if (timeState == TimeState.Stopped)
            setNewTimerLength()
        else
            setPreviousTimeLength()
        secondsRemaining = if (timeState == TimeState.Running || timeState == TimeState.Paused)
            TimerPreferences.getSecondsRemaining(this)
        else
            timeSeconds
        if (timeState == TimeState.Running)
            startTimer()
        updateButtons()
        updateCountdown()

    }

    private fun onTimerFinished() {
        timeState = TimeState.Stopped
        setNewTimerLength()
        progress_countdown.progress = 0
        TimerPreferences.setSecondsRemaining(timeSeconds, this)
        secondsRemaining = timeSeconds

        updateButtons()
        updateCountdown()
    }

    private fun startTimer() {
        println("-----------------STARTING TIMER-----------------")
        timeState = TimeState.Running
        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() {
                onTimerFinished()
            }

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdown()
            }


        }.start()
    }

    private fun setNewTimerLength() {
        val lengthInMinutes = TimerPreferences.getTimerLength(this)
        timeSeconds = (lengthInMinutes * 60L)
        progress_countdown.max = timeSeconds.toInt()
    }

    private fun setPreviousTimeLength() {
        timeSeconds = TimerPreferences.getPreviousTimerLengthSeconds(this)
        progress_countdown.max = timeSeconds.toInt()
    }


    private fun updateCountdown() {
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        timer_countdown.text = "$minutesUntilFinished:${
        if (secondsStr.length == 2) secondsStr
        else "0" + secondsStr}"
        progress_countdown.progress = (timeSeconds - secondsRemaining).toInt()


    }

    private fun updateButtons() {
        when (timeState) {
            TimeState.Running -> {
                button_start.isEnabled = false
                button_pause.isEnabled = true
                button_stop.isEnabled = true
            }
            TimeState.Stopped -> {
                button_start.isEnabled = true
                button_pause.isEnabled = false
                button_stop.isEnabled = false
            }
            TimeState.Paused -> {
                button_start.isEnabled = true
                button_pause.isEnabled = false
                button_stop.isEnabled = true
            }
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
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                    //speechResult.text = result[0]
                    val answer = result[0]



                    if (answer.contains(songWord.text, ignoreCase = true)){
                        if(answer.split(" ").toTypedArray().size > 5){


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
//                        if (!doc.getElementsByAttributeValueContaining(
//                                "class",
//                                "qrShPb kno-ecr-pt PZPZlf mfMhoc"
//                            ).isEmpty()
//                        ) {
//                            firstResultSong = doc.getElementsByAttributeValueContaining(
//                                "class",
//                                "qrShPb kno-ecr-pt PZPZlf mfMhoc"
//                            ).first().text()
//                            println(firstResultSong)
//                            firstResultArtist =
//                                doc.getElementsByAttributeValueContaining("class", "wwUB2c PZPZlf")
//                                    .first()
//                                    .text()
//                            println(firstResultSong + " - " + firstResultArtist)
//                            val intent = AnswerResult.newIntent(
//                                this@SpeechToText,
//                                answer,
//                                firstResultSong,
//                                firstResultArtist,
//                                gameViewModel.number
//                            )
//                            startActivity(intent)
//
//                            println(gameViewModel.currentIndex.value.toString())
//                            finish()
//                        } else
                            if (!doc.getElementsByAttributeValueContaining(
                                    "class",
                                    "LC20lb DKV0Md"
                                ).isEmpty()
                            ) {
                                val firstResultLink =
                                    doc.getElementsByAttributeValueContaining("class", "LC20lb DKV0Md")
                                        .first()
                                        .text()

                                println(firstResultLink)

                                if (firstResultLink.split(" – ").toTypedArray().size <=1){
                                    firstResultArtist = ""
                                    println("firstResultArtist: " + firstResultArtist)

                                    println(firstResultLink.split(" – ").toTypedArray().size)
                                    firstResultSong =
                                        firstResultLink.split(" – ").toTypedArray()[0].split(" Lyrics").toTypedArray()[0]

                                    println(firstResultArtist)
                                    println(firstResultSong)
                                }
                                else {
                                    firstResultArtist = firstResultLink.split(" – ").toTypedArray()[0]
                                    println("firstResultArtist: " + firstResultArtist)

                                    println(firstResultLink.split(" – ").toTypedArray().size)
                                    firstResultSong =
                                        firstResultLink.split(" – ").toTypedArray()[1].split(" Lyrics").toTypedArray()[0]

                                    println(firstResultArtist)
                                    println(firstResultSong)
                                }


                                val intent = AnswerResult.newIntent(
                                    this@SpeechToText,
                                    answer,
                                    firstResultSong,
                                    firstResultArtist,
                                    gameViewModel.number
                                )
                                startActivity(intent)

                                println(gameViewModel.currentIndex.value.toString())
                                finish()
                                // can't access UI elements from the background thread

                            }

                            else{
                                //song doesn't exist
                                val intent = EndGame.newIntent(
                                    this@SpeechToText,
                                    "Song doesn't exist",
                                    gameViewModel.number
                                )
                                startActivity(intent)
                                finish()
                            }
                        }



                        }
                        else{
                            //not long enough
                            val intent = EndGame.newIntent(
                                this@SpeechToText,
                                "Not enough words",
                                gameViewModel.number
                            )
                            startActivity(intent)
                            finish()
                        }

                    }
                    else{
                        //song doesn't use word
                        val intent = EndGame.newIntent(
                            this@SpeechToText,
                            "Didn't use needed word",
                            gameViewModel.number
                        )
                        startActivity(intent)
                        finish()
                    }




                } else if (resultCode != Activity.RESULT_OK) {
                    //speechResult.text = "nullFail"
                } else {
                    //speechResult.text = "failed"
                }
            }

        }
    }


    companion object {
        fun newIntent(packageContext: Context, currentScore: Int): Intent {
            return Intent(packageContext, SpeechToText::class.java).apply {
                putExtra(GET_CURRENT_SCORE, currentScore)
            }
        }
    }

}