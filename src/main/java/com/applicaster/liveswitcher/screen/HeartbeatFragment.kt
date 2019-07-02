package com.applicaster.liveswitcher.screen

import android.os.CountDownTimer
import android.support.v4.app.Fragment

abstract class HeartbeatFragment : Fragment() {
    var timer: CountDownTimer? = null
    val HEARTBEAT_TIME: Long = 60000 // 1 minute

    override fun onStart() {
        super.onStart()
        startTimer()
    }

    private fun startTimer() {
        timer = object : CountDownTimer(HEARTBEAT_TIME, 1000) {
            override fun onFinish() {
                heartbeat()
                timer?.start()
            }

            override fun onTick(millisUntilFinished: Long) {
                // do nothing
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

    abstract fun heartbeat()
}
