package com.stephent.songasssocitation

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_ingame.*
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.text.HtmlCompat
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.popup_instructions.*
import okhttp3.*
import java.io.IOException


const val EXTRA_MESSAGE = 2001

class MainActivity : AppCompatActivity() {

    private lateinit var instructionsDialog: Dialog
    private var whichDifficulty = "EASY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton = findViewById<Button>(R.id.true_button)
        val instructButton = findViewById<Button>(R.id.instructions_button)
        val easyButton = findViewById<ToggleButton>(R.id.easy_button)
        val mediumButton = findViewById<ToggleButton>(R.id.medium_button)
        val hardButton = findViewById<ToggleButton>(R.id.hard_button)


        difficulty_info.setText("Easy difficulty of words\n30 seconds timer")

        val toggleButton = findViewById<RadioGroup>(R.id.toggleGroup)
        mediumButton.setTextColor(Color.GRAY)
        hardButton.setTextColor(Color.GRAY)




        startButton.setOnClickListener {
            println(whichDifficulty)
            GameViewModel.setDifficulty(whichDifficulty)
            val intent = SpeechToText.newIntent(this@MainActivity, 0)
            startActivity(intent)
        }

        instructButton.setOnClickListener {
//            val window = PopupWindow(this)
//            val view = LayoutInflater.from(this).inflate(R.layout.popup_instructions, null)
//            window.contentView = view
//            val gotit = view.findViewById<Button>(R.id.got_it_button)
//            gotit.setOnClickListener {
//                window.dismiss()
//            }

           val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_instructions, null)
            val mBuilder = AlertDialog.Builder(this).setView(dialogView)
            val alertDialogView = mBuilder.show()
            val gotit = alertDialogView.findViewById<Button>(R.id.got_it_button)
            gotit.setOnClickListener {
               alertDialogView.dismiss()
            }

        }




        easyButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                easyButton.setTextColor(Color.BLACK)
                whichDifficulty = "EASY"
                var easyString = "Easy difficulty of words\n30 second timer"
                difficulty_info.setText(easyString)
            } else if (!hardButton.isChecked && !mediumButton.isChecked) {
                easyButton.isChecked = true
            } else {
                easyButton.setTextColor(Color.GRAY)
            }
        }
        mediumButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mediumButton.setTextColor(Color.BLACK)
                whichDifficulty = "MEDIUM"
                var mediumString = "Medium difficulty of words\n25 second timer"
                difficulty_info.setText(mediumString)
            } else if (!hardButton.isChecked && !easyButton.isChecked) {
                mediumButton.isChecked = true
            } else {
                mediumButton.setTextColor(Color.GRAY)
            }
        }
        hardButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hardButton.setTextColor(Color.BLACK)
                whichDifficulty = "HARD"
                var hardString = "Hard difficulty of words\n20 second timer"
                difficulty_info.setText(hardString)
            } else if (!mediumButton.isChecked && !easyButton.isChecked) {
                hardButton.isChecked = true
            } else {
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


    override fun onSaveInstanceState(outState: Bundle) {

        println(whichDifficulty)
        super.onSaveInstanceState(outState)
        outState.putString("whichDifficulty", whichDifficulty)


        println("-------------TIMER CANCELLED---------------")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        whichDifficulty = savedInstanceState.getString("whichDifficulty").toString()
        if (!whichDifficulty.equals("EASY")) {
            easy_button.setTextColor(Color.GRAY)
            easy_button.isChecked = false
        }
    }


//    fun showInstructions(view: View){
//        instructionsDialog.setContentView(R.layout.popup_instructions)
//        got_it_button.setOnClickListener {
//            instructionsDialog.dismiss()
//        }
//        instructionsDialog.window.setBackgroundDrawable((ColorDrawable)Color.TRANSPARENT)
//        instructionsDialog.show()
//    }


}


