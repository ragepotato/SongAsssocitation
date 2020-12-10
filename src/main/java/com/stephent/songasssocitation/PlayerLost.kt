package com.stephent.songasssocitation

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_answer_result.*
import kotlinx.android.synthetic.main.activity_gameover.*
import kotlinx.android.synthetic.main.popup_choose_word.view.*
import kotlinx.android.synthetic.main.toast_nextplayer.view.*
import org.w3c.dom.Text
import java.util.*
import kotlin.Comparator
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.media.MediaPlayer
import android.os.PersistableBundle
import kotlinx.android.synthetic.main.party_final_scores.view.*


private const val GET_REASON_LOSS = "com.stephent.songassociation.get_reason_loss"
private const val GET_CURRENT_SCORE = "com.stephent.songassociation.get_current_score"

class PlayerLost : AppCompatActivity() {
    private lateinit var clockPlayer: MediaPlayer
    private lateinit var endgameTextView: TextView
    private lateinit var highscoreTextView: TextView
    private var currentPlayer : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameover)
        endgameTextView = findViewById(R.id.whyYouLostText)
        endgameTextView.setText(intent.getStringExtra(GET_REASON_LOSS))

        highscoreTextView = findViewById(R.id.highScore)
        var currentScore = intent.getIntExtra(GET_CURRENT_SCORE, 1)
        currentPlayer = GameViewModel.playerList.get(currentScore)
        youLoseText.setText(currentPlayer + " is out!")

        finalScore.setText(currentPlayer + "\'s final score: " + GameViewModel.playerMap.get(currentPlayer))


        if (savedInstanceState == null) {
            GameViewModel.playerList.remove(currentPlayer)
            println("---------lalallala here-----------------")
            clockPlayer = MediaPlayer.create(this, R.raw.wronganswer)
            clockPlayer.setVolume(0.01F, 0.01F)
            clockPlayer.start()
        }


        var scoreboard = "<b>SCORES</b><br>"

        GameViewModel.playerMap.forEach { i ->
            if (GameViewModel.playerList.contains(i.key)){
                scoreboard = scoreboard + "<font color='#005bee'>" + i.key + "</font> - " + i.value + "<br>"

            }
            else{
                scoreboard = scoreboard + "<strike><font color='#EE0000'>" + i.key + "</font></strike> - " + i.value + "<br>"
            }
        }
        println(scoreboard)
        highscoreTextView.setText(Html.fromHtml(scoreboard))

//        val preferences = getSharedPreferences("PREFS", 0)
//
//        var easyBest = preferences.getInt("easyBest", 0)
//        var mediumBest = preferences.getInt("mediumBest", 0)
//        var hardBest = preferences.getInt("hardBest", 0)
//
//        val editor = preferences.edit()
//
//        if (GameViewModel.currentDifficulty.equals("EASY")) {
//            if (currentScore >= easyBest) editor.putInt("easyBest", currentScore)
//            easyBest = currentScore
//            editor.apply()
//        }
//        if (GameViewModel.currentDifficulty.equals("MEDIUM")) {
//            if (currentScore >= mediumBest) editor.putInt("mediumBest", currentScore)
//            mediumBest = currentScore
//            editor.apply()
//        }
//        if (GameViewModel.currentDifficulty.equals("HARD")) {
//            if (currentScore >= hardBest) editor.putInt("hardBest", currentScore)
//            hardBest = currentScore
//            editor.apply()
//        }
//
//
//        highscoreTextView.setText(Html.fromHtml("<u><b>High Scores</b></u> <br>Easy: " + easyBest + "<br>Medium: " + mediumBest + "<br>Hard: " + hardBest))


        back_home_button.text = "NEXT"

        back_home_button.setOnClickListener {
            if (GameViewModel.playerList.size == 1){


                var theWinner = GameViewModel.playerMap.maxBy { it.value }
                val toastLayout = layoutInflater.inflate(R.layout.party_final_scores, null)

                val dialogView = LayoutInflater.from(this).inflate(R.layout.party_final_scores, null)
                val mBuilder = AlertDialog.Builder(this).setView(dialogView)
                val alertDialogView = mBuilder.show()
                val readyButton = alertDialogView.findViewById<Button>(R.id.ready_end_button)
                dialogView.longest_lasting.text = "Longest lasting: " + GameViewModel.playerList.get(0)
                dialogView.highest_score.text = "Highest score: " + theWinner!!.key
                readyButton.setOnClickListener {
                    alertDialogView.dismiss()
                    val intent = Intent(this, MainActivity::class.java).apply {
                    }
                    startActivity(intent)
                    clockPlayer.stop()
                    clockPlayer.release()
                    finish()
                }




            }
            else{
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


                    println(GameViewModel.playerMap)
                    println(GameViewModel.playerMap)


                    //currentScore = currentScore + 1

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


                    val intent = PartySpeechToText.newIntent(this@PlayerLost, currentScore, currentWord)
                    startActivity(intent)
                    clockPlayer.stop()
                    clockPlayer.release()
                    finish()



                }


            }




        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        println("currentPlayer: " + currentPlayer)
        outState.putString("player", currentPlayer)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentPlayer = savedInstanceState.getString("player", "Player")
        youLoseText.setText(currentPlayer + " is out!")
        finalScore.setText(currentPlayer + "\'s final score: " + GameViewModel.playerMap.get(currentPlayer))
    }

    companion object {
        fun newIntent(
            packageContext: Context,
            endgameReason: String,
            currentScore: Int
        ): Intent {
            return Intent(packageContext, PlayerLost::class.java).apply {
                putExtra(GET_REASON_LOSS, endgameReason)
                putExtra(GET_CURRENT_SCORE, currentScore)
            }
        }
    }
}