package com.stephent.songasssocitation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_ingame.*

class SpeechToText : AppCompatActivity() {
    private val REQUEST_CODE_SPEECH_INPUT = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingame)

        microphoneImage.setOnClickListener{
            getSpeechInput()
        }

    }

    fun getSpeechInput(){
        val  micIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        //val  micIntent = Intent(RecognizerIntent.ACTION_WEB_SEARCH)
        micIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Sing!")
        micIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        try{
            startActivityForResult(micIntent, REQUEST_CODE_SPEECH_INPUT)


        }
        catch (e: Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_CODE_SPEECH_INPUT ->{
                if (resultCode == Activity.RESULT_OK && null != data){
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    speechResult.text = result[0]
                    val answer = result[0]
                    val intent = AnswerResult.newIntent(this@SpeechToText, answer)
                    startActivity(intent)


                }
                else if (resultCode != Activity.RESULT_OK ){
                    speechResult.text = "nullFail"
                }
                else{
                    speechResult.text = "failed"
                }
            }

        }
    }

}