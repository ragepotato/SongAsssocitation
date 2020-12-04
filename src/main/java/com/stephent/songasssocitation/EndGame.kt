package com.stephent.songasssocitation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_answer_result.*
import kotlinx.android.synthetic.main.activity_gameover.*
import org.w3c.dom.Text

private const val GET_REASON_LOSS = "com.stephent.songassociation.get_reason_loss"
private const val GET_CURRENT_SCORE = "com.stephent.songassociation.get_current_score"

class EndGame : AppCompatActivity() {
    private lateinit var endgameTextView: TextView
    private lateinit var highscoreTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameover)
        endgameTextView = findViewById(R.id.whyYouLostText)
        endgameTextView.setText(intent.getStringExtra(GET_REASON_LOSS))

        highscoreTextView = findViewById(R.id.highScore)


        var currentScore = intent.getIntExtra(GET_CURRENT_SCORE, 1)
        finalScore.setText("Final Score: " + currentScore)


        val preferences = getSharedPreferences("PREFS", 0)

        var easyBest = preferences.getInt("easyBest", 0)
        var mediumBest = preferences.getInt("mediumBest", 0)
        var hardBest = preferences.getInt("hardBest", 0)

        val editor = preferences.edit()

        if (GameViewModel.currentDifficulty.equals("EASY")) {
            if (currentScore >= easyBest) editor.putInt("easyBest", currentScore)
            easyBest = currentScore
            editor.apply()
        }
        if (GameViewModel.currentDifficulty.equals("MEDIUM")) {
            if (currentScore >= mediumBest) editor.putInt("mediumBest", currentScore)
            mediumBest = currentScore
            editor.apply()
        }
        if (GameViewModel.currentDifficulty.equals("HARD")) {
            if (currentScore >= hardBest) editor.putInt("hardBest", currentScore)
            hardBest = currentScore
            editor.apply()
        }


        highscoreTextView.setText(Html.fromHtml("<u><b>High Scores</b></u> <br>Easy: " + easyBest + "<br>Medium: " + mediumBest + "<br>Hard: " + hardBest))




        back_home_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
            }
            startActivity(intent)
            finish()
        }

    }

    companion object {
        fun newIntent(
            packageContext: Context,
            endgameReason: String,
            currentScore: Int
        ): Intent {
            return Intent(packageContext, EndGame::class.java).apply {
                putExtra(GET_REASON_LOSS, endgameReason)
                putExtra(GET_CURRENT_SCORE, currentScore)
            }
        }
    }
}