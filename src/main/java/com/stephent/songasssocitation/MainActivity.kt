package com.stephent.songasssocitation

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_ingame.*
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.ToggleButton
import android.widget.RadioGroup
import androidx.core.text.HtmlCompat


const val EXTRA_MESSAGE = 2001

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton = findViewById<Button>(R.id.true_button)
        val easyButton = findViewById<ToggleButton>(R.id.easy_button)
        val mediumButton = findViewById<ToggleButton>(R.id.medium_button)
        val hardButton = findViewById<ToggleButton>(R.id.hard_button)
        var whichDifficulty = "EASY"
        difficulty_info.setText("Easy difficulty of words\n20 seconds timer\nMinimum 5 words per guess")

        val toggleButton =  findViewById<RadioGroup>(R.id.toggleGroup)
        mediumButton.setTextColor(Color.GRAY)
        hardButton.setTextColor(Color.GRAY)


        startButton.setOnClickListener{
            println(whichDifficulty)
            GameViewModel.easyQuestionBank.shuffled()
            val intent = Intent(this, SpeechToText::class.java)
            startActivity(intent)
        }

        easyButton.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked){
                easyButton.setTextColor(Color.BLACK)
                whichDifficulty = "EASY"
                var easyString = "Easy difficulty of words\n20 seconds timer\nMinimum 5 words per guess"
                difficulty_info.setText(easyString)
            }
            else if (!hardButton.isChecked && !mediumButton.isChecked){
                easyButton.isChecked = true
            }
            else{
                easyButton.setTextColor(Color.GRAY)
            }
    }
        mediumButton.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked){
                mediumButton.setTextColor(Color.BLACK)
                whichDifficulty = "MEDIUM"
                var mediumString = "Medium difficulty of words\n15 seconds timer\nMinimum 6 words per guess"
                difficulty_info.setText(mediumString)
            } else if (!hardButton.isChecked && !easyButton.isChecked){
                mediumButton.isChecked = true
            }
            else{
                mediumButton.setTextColor(Color.GRAY)
            }
        }
        hardButton.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked){
                hardButton.setTextColor(Color.BLACK)
                whichDifficulty = "HARD"
                var hardString = "Hard difficulty of words\n10 seconds timer\nMinimum 7 words per guess"
                difficulty_info.setText(hardString)
            }else if (!mediumButton.isChecked && !easyButton.isChecked){
                hardButton.isChecked = true
            }

            else{
                hardButton.setTextColor(Color.GRAY)
            }
        }

        toggleButton.setOnCheckedChangeListener(ToggleListener)
    }
    val ToggleListener: RadioGroup.OnCheckedChangeListener =
        RadioGroup.OnCheckedChangeListener { radioGroup, i ->
            for (j in 0 until radioGroup.childCount) {
                val view = radioGroup.getChildAt(j) as ToggleButton
                view.isChecked = view.id == i
            }
        }

    fun onToggle(view: View) {
        (view.parent as RadioGroup).check(view.id)
        // app specific stuff ..
    }


}
