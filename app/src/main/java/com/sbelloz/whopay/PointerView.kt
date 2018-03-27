package com.sbelloz.whopay

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import java.util.*

/**
 * @author Simone Bellotti
 */

open class PointerView : View {

    interface OnPointsUpdateListener {
        fun onPointsChanged(size: Int)
    }

    private val pointersMap = SparseArray<PointF>()
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val targetPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    internal var listener: OnPointsUpdateListener? = null
    private var selectedPointer = -1

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    fun ClosedRange<Int>.random() =
            Random().nextInt(endInclusive - start) + start

    fun selectRandomly() {
        val totalPointers = pointersMap.size()
        if (totalPointers > 0) {
            selectedPointer = (0..totalPointers).random()
            invalidate()
        }
    }

    private fun init(attrs: AttributeSet?) {
        circlePaint.color = Color.RED
        targetPaint.color = Color.BLUE
        circlePaint.textSize = TEXT_SIZE //TODO
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        (0 until pointersMap.size())
                .map { i: Int -> pointersMap.valueAt(i) }
                .forEach { point ->
                    var paintToUse = circlePaint
                    if (selectedPointer >= 0) {
                        val selectedPoint = pointersMap.valueAt(selectedPointer)
                        if (selectedPoint == point) {
                            paintToUse = targetPaint
                        }
                    }
//                    canvas.drawCircle(point.x, point.y, CIRCLE_SIZE, paintToUse)
                    canvas.drawOval(RectF(point.x - OVAL_WIDTH,
                            point.y - OVAL_HEIGHT,
                            point.x + OVAL_WIDTH,
                            point.y + OVAL_HEIGHT), paintToUse)
                }

        canvas.drawText("Total pointers: " + pointersMap.size(), 150f, 150f, circlePaint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        listener = null
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)

        when (action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN -> {
                val x = event.getX(pointerIndex)
                val y = event.getY(pointerIndex)
                val point = PointF(x, y)
                pointersMap.put(pointerId, point)
                listener?.onPointsChanged(pointersMap.size())
            }
            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until event.pointerCount) {
                    val point = pointersMap.get(event.getPointerId(i))
                    if (point != null) {
                        val newX = event.getX(i)
                        val newY = event.getY(i)
                        point.set(newX, newY)
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_POINTER_UP,
            MotionEvent.ACTION_CANCEL -> {
                pointersMap.remove(pointerId)
                this.selectedPointer = -1
                listener?.onPointsChanged(pointersMap.size())
                performClick()
            }
        }
        invalidate()
        return true
    }

    companion object {
        private const val TAG = "GestureView"
        const val CIRCLE_SIZE = 120.0f
        const val OVAL_WIDTH = 200.0f
        const val OVAL_HEIGHT = 220.0f
        const val TEXT_SIZE = 50f
    }


}
