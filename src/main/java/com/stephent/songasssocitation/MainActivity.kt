package com.stephent.songasssocitation

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_ingame.*
import kotlinx.android.synthetic.main.activity_main.*

const val EXTRA_MESSAGE = 2001

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.true_button)
        button.setOnClickListener{
            GameViewModel.easyQuestionBank.shuffled()
            val intent = Intent(this, SpeechToText::class.java)
            startActivity(intent)
        }
    }

    fun sendMessage(view: View) {


    }

}
