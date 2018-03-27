package com.sbelloz.whopay

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView


/**
 * @author Simone Bellotti
 */

class StarterView : AppCompatTextView {

    interface OnStarterViewListener {
        fun onStart()
        fun onStop()
        fun onFinish()
    }

    private val values = arrayOf("3", "2", "1", "Go!")
    private val runnable = object : Runnable {
        override fun run() {
            val size = values.size
            if (size > 0 && index < size) {
                Log.d("StarterView", "counting! " + values[index])
                valuesHandler.sendEmptyMessage(index)
                valuesHandler.postDelayed(this, 1000)
                index++
            } else {
                listener?.onFinish()
                Log.d("StarterView", "callbacks removed!")
                stop()
            }
        }
    }

    private var valuesHandler: Handler = MyHandler(this, *values)
    private var isStarted = false
    private var listener: OnStarterViewListener? = null
    private var index = 0

    constructor(context: Context) : super(context) {
        setTextColor(Color.BLACK)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setTextColor(Color.BLACK)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }

    fun start() {
        if (!isStarted) {
            valuesHandler.post(runnable)
            isStarted = true
            listener?.onStart()
        }
    }

    fun stop() {
        if (isStarted) {
            valuesHandler.removeCallbacks(runnable)
            isStarted = false
            index = 0
            listener?.onStop()
            reset()
        }
    }

    fun setOnStarterViewListener(listener: OnStarterViewListener) {
        this.listener = listener
    }

    private fun reset() {
        animate().cancel()
        text = null
        scaleX = 1f
        scaleY = 1f
        alpha = 1f

    }

    private class MyHandler(private val textView: TextView, private vararg val values: String) : Handler() {

        override fun handleMessage(msg: Message) {
            textView.scaleX = 1f
            textView.scaleY = 1f
            textView.alpha = 1f

            textView.text = values[msg.what]
            textView.animate()
                    .setDuration(1000)
                    .scaleX(5f)
                    .scaleY(5f)
                    .alpha(0f)
        }
    }

}
