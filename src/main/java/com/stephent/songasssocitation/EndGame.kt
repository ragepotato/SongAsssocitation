package com.stephent.songasssocitation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_answer_result.*
import kotlinx.android.synthetic.main.activity_gameover.*

private const val GET_REASON_LOSS = "com.stephent.songassociation.get_reason_loss"
private const val GET_CURRENT_SCORE = "com.stephent.songassociation.get_current_score"

class EndGame : AppCompatActivity(){
    private lateinit var endgameTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameover)
        endgameTextView = findViewById(R.id.whyYouLostText)
        endgameTextView.setText(intent.getStringExtra(GET_REASON_LOSS))
        var currentScore = intent.getIntExtra(GET_CURRENT_SCORE, 1)
        finalScore.setText("Final Score: " + currentScore)





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