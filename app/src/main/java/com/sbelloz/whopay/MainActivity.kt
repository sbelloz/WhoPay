package com.sbelloz.whopay

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gestureView.listener = object : PointerView.OnPointsUpdateListener {
            override fun onPointsChanged(size: Int) {
                starterView.stop()
                if (size > 1) {
                    starterView.start()
                }
            }
        }

        starterView.setOnStarterViewListener(object : StarterView.OnStarterViewListener {
            override fun onStart() {
                Log.d("MainActivity", "onStart: ")
            }

            override fun onStop() {
                Log.d("MainActivity", "onStop: ")
            }

            override fun onFinish() {
                gestureView.selectRandomly()
            }
        })
    }

}
