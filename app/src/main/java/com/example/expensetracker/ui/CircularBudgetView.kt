package com.example.expensetracker.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min
import androidx.core.graphics.toColorInt

// defines the circular progress bar for budget tracking
class CircularBudgetView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var percent: Int = 0
        set(value) {
            field = value.coerceIn(0, 100)
            invalidate()
        }

    var text: String = ""
        set(value) {
            field = value
            invalidate()
        }

    //paints the text that displays total spend / total budget
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 48f
        typeface = Typeface.DEFAULT_BOLD
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size = min(width, height)
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = size / 2f - 20

        // Background ring
        paint.color = Color.LTGRAY
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 20f
        canvas.drawCircle(centerX, centerY, radius, paint)

        // Progress arc
        paint.color = getColorForPercent(percent)
        val oval = RectF(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
        canvas.drawArc(oval, -90f, (360f * percent / 100f), false, paint)

        // Centered text
        val textY = centerY - ((textPaint.descent() + textPaint.ascent()) / 2)
        canvas.drawText(text, centerX, textY, textPaint)

    }

    //sets the ring colour to green when at <70% of budget, yellow when at >70%/<100% of budget and red when at >=100% of budget
    private fun getColorForPercent(percent: Int): Int {
        return when {
            percent < 70 -> "#4CAF50".toColorInt() // Green
            percent < 100 -> "#FFC107".toColorInt() // Yellow
            else -> "#F44336".toColorInt() // Red
        }
    }
}
