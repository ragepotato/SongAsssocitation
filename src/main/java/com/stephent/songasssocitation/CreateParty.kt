package com.stephent.songasssocitation

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_make_party.*
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.popup_choose_word.*
import kotlinx.android.synthetic.main.popup_choose_word.view.*
import kotlinx.android.synthetic.main.toast_nextplayer.view.*


private const val GET_NUMBER_PLAYERS = "com.stephent.songassociation.get_number_players"

class CreateParty : AppCompatActivity() {

        var playersList = arrayOf<EditText?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_party)
        println("Here")
        var numberPlayers = intent.getIntExtra(GET_NUMBER_PLAYERS, 1)

        var currentWord = "apple"
        playersList = setPlayerView(numberPlayers)
        if (savedInstanceState != null){
            var list = savedInstanceState.getStringArray("Names")
            for (i in 0..((playersList.size)-1)) {
                playersList[i]?.setText(list?.get(i))

            }
        }


        playerNamesButton.setOnClickListener {
            GameViewModel.resetPlayerList()
            for (i in 0..((playersList.size)-1)) {
                var playerString =  playersList[i]?.text.toString()


                GameViewModel.playerMap.put(playerString, 0)
                GameViewModel.playerList.add(playerString)
            }
            println(GameViewModel.playerMap)
            println(GameViewModel.playerList)

            val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_choose_word, null)
            val mBuilder = AlertDialog.Builder(this).setView(dialogView)
            val alertDialogView = mBuilder.show()
            val readyButton = alertDialogView.findViewById<Button>(R.id.ready_start_button)


            dialogView.current_player_label.text = GameViewModel.playerList.get(GameViewModel.playerList.size - 1)
            readyButton.setOnClickListener {

                alertDialogView.dismiss()


                currentWord = dialogView.word_for_next.text.toString()
                println(currentWord)
                val toastLayout = layoutInflater.inflate(R.layout.toast_nextplayer, null)

                Toast(this).apply{
                    toastLayout.player_guess_label.text = GameViewModel.playerList.get(0)
                    duration = Toast.LENGTH_LONG
                    setGravity(Gravity.CENTER, 0,0)
                    view = toastLayout

                }.show()

                val intent = PartySpeechToText.newIntent(this@CreateParty, 0, currentWord)
                startActivity(intent)

            }

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        GameViewModel.resetPlayerList()
        for (i in 0..((playersList.size)-1)) {
            var playerString =  playersList[i]?.text.toString()


            GameViewModel.playerMap.put(playerString, 0)
            GameViewModel.playerList.add(playerString)
        }




        var list = GameViewModel.playerList.toTypedArray()
        outState.putStringArray("Names", list)

    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

    }


    private fun setPlayerView(numAlarms : Int) : Array<EditText?>{
        val size = numAlarms // total number of TextViews to add

        var linlayout: LinearLayout




        val tv = arrayOfNulls<EditText>(size)

        var tempText: TextView
        var temp: EditText

        for (i in 0..(size-1)) {

            linlayout = LinearLayout(this)


            linlayout.orientation = LinearLayout.HORIZONTAL
            linlayout.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

            tempText = TextView(this)
            temp = EditText(this)



            val font = ResourcesCompat.getFont(this, R.font.fredokaone)

//            val font = Typeface.createFromAsset(
//                assets,
//                "fonts/fredokaone.ttf"
//            )
            temp.setTypeface(font)
            tempText.setTypeface(font)
            temp.width = 100
            temp.setText("Player " + (i+1))
            temp.setLayoutParams(
                ViewGroup.LayoutParams(
                    400,
                    200
                )
            )

            tempText.textSize = 25F
            tempText.text = "Player " + (i + 1) + ": "
            println("layout params: " + temp.layoutParams)
            temp.textSize = 25F
            //arbitrary task

            // add the textview to the linearlayout
            linlayout.addView(tempText)
            linlayout.addView(temp)
            myLinearLayout.addView(linlayout)

            tv[i] = temp


        }


        return tv

    }
    companion object {
        fun newIntent(packageContext: Context, numberPlayers: Int): Intent {
            return Intent(packageContext, CreateParty::class.java).apply {
                putExtra(GET_NUMBER_PLAYERS, numberPlayers)
            }
        }
    }
    }

