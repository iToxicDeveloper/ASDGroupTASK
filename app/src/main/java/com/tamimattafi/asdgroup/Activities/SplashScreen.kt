package com.tamimattafi.asdgroup.Activities
//Started 14/02/2019

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import com.tamimattafi.asdgroup.R
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {

    private var mCountDownTimer : CountDownTimer? = null
    private var hasPausedActivity = false
    private var hasStoppedActivity = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Hide Status Bar
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_splash_screen)

        //Set up timer with 1 second
        setUpUserInterface(1000)
    }

    private fun startUpActivity(mActivity : Activity){
        startActivity(Intent(this,mActivity::class.java))
        finish()
    }

    private fun setUpUserInterface(mTimeInMillis : Int){
        //Set up seekBar to progressBar
        splash_screen_seeksBar.isEnabled = false
        splash_screen_seeksBar.max = mTimeInMillis

        //Setting up timer for the seekBar
        mCountDownTimer = object : CountDownTimer(mTimeInMillis.toLong(),10){
            override fun onFinish() {
                startUpActivity(NewsPage())
            }

            override fun onTick(p0: Long) {
                splash_screen_seeksBar.progress = mTimeInMillis - p0.toInt() + 10
            }
        }

        //Start Timer
        mCountDownTimer!!.start()
    }

    override fun onPause() {
        super.onPause()
        //Stop the timer
        mCountDownTimer!!.cancel()
        hasPausedActivity = true

    }

    override fun onStop() {
        super.onStop()
        //Stop the timer
        mCountDownTimer!!.cancel()
        hasPausedActivity = true
    }

    override fun onResume() {
        super.onResume()
        //Since onResume is called right after onCreat and onStart, we must handle this issue to prevent code duplication
        if (hasStoppedActivity) {
            startUpActivity(NewsPage())
        }
        else {
            if (hasPausedActivity) {
                mCountDownTimer = object : CountDownTimer(splash_screen_seeksBar.progress.toLong(),10){
                    override fun onFinish() {
                        startUpActivity(NewsPage())
                    }

                    override fun onTick(p0: Long) {
                        splash_screen_seeksBar.progress = splash_screen_seeksBar.max - p0.toInt()
                    }

                }
                mCountDownTimer!!.start()
            }
        }


    }
}
