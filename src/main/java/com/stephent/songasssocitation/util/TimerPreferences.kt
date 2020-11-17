package com.stephent.songasssocitation.util

import android.content.Context

import androidx.preference.PreferenceManager
import com.stephent.songasssocitation.SpeechToText


class TimerPreferences {



    companion object{

        private const val PREVIOUS_TIMERLENGTH_CODE = "com.stephent.songassocitation.previous_timerlength"
        private const val TIME_STATE_CODE = "com.stephent.songassociation.time_state"
        private const val SECONDS_REMAINING_CODE = "com.stephent.songassociation.seconds_remaining"

        fun getTimerLength(context: Context): Int{
            return 1
        }



        fun getPreviousTimerLengthSeconds(context: Context) : Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIMERLENGTH_CODE, 0)

        }

        fun setPreviousTimerLengthSeconds(seconds: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMERLENGTH_CODE, seconds)
            editor.apply()
        }

        fun getTimeState (context: Context): SpeechToText.TimeState{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIME_STATE_CODE, 0)
            return SpeechToText.TimeState.values()[ordinal]
        }

        fun setTimeState(state: SpeechToText.TimeState, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = state.ordinal
            editor.putInt(TIME_STATE_CODE, ordinal)
            editor.apply()
        }

        fun getSecondsRemaining(context: Context) : Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(SECONDS_REMAINING_CODE, 0)

        }

        fun setSecondsRemaining(seconds: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_CODE, seconds)
            editor.apply()
        }


    }
}

