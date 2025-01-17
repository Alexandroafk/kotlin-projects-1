package com.wuaha.ktl_p1.ui.timer.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View


class TimerView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var hours: Int = 0
    private var minutes: Int = 0
    private var attachedText: String = "Punto01"
    private var showText: Boolean = false

    private var borderColor: Int = Color.GREEN
    private var backgroundColor: Int = Color.BLACK
    private var timeTextColor: Int = Color.WHITE

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = borderColor
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = backgroundColor
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = timeTextColor
        textSize = 60f
        textAlign = Paint.Align.CENTER
    }

    /**
     * Coloca el tiempo personalizado.
     */
    fun updateTime(newHours: Int, newMinutes: Int) {
        hours = (newHours % 24 + 24) % 24 // Ensure valid 24-hour format
        minutes = (newMinutes % 60 + 60) % 60 // Ensure valid 60-minute format
        invalidate() // Refresh the view
    }

    /**
     * Añade Minutos a la hora actual
     */
    fun addMinutes(minutesToAdd: Int) {
        val totalMinutes = hours * 60 + minutes + minutesToAdd
        hours = (totalMinutes / 60) % 24
        minutes = totalMinutes % 60
        invalidate()
    }

    /**
     * Añade Horas a la hora actual
     */
    fun addHours(hoursToAdd: Int) {
        hours = (hours + hoursToAdd) % 24
        invalidate()
    }

    /**
     * Cambia la visivilidad del texto adjunto del temporizador
     */
    fun toggleShowText() {
        showText = !showText
        invalidate()
    }

    /**
     * Establece los colores del temporizador
     */
    fun setColors(newBorderColor: Int, newBackgroundColor: Int, newTimeTextColor: Int) {
        borderColor = newBorderColor
        backgroundColor = newBackgroundColor
        timeTextColor = newTimeTextColor

        borderPaint.color = borderColor
        backgroundPaint.color = backgroundColor
        textPaint.color = timeTextColor

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Se dibuja el temporizador
        val padding = 16f
        val rectLeft = padding
        val rectTop = padding
        val rectRight = width - padding
        val rectBottom = height - padding
        canvas.drawRoundRect(rectLeft, rectTop, rectRight, rectBottom, 30f, 30f, backgroundPaint)
        canvas.drawRoundRect(rectLeft, rectTop, rectRight, rectBottom, 30f, 30f, borderPaint)

        // Draw the time text
        val timeText = String.format("%02d:%02d", hours, minutes)
        val centerX = width / 2f
        val centerY = height / 2f
        canvas.drawText(timeText, centerX, centerY + textPaint.textSize / 3, textPaint)

        // Optionally draw the attached text
        if (showText) {
            textPaint.textSize = 40f
            canvas.drawText(attachedText, centerX, centerY - textPaint.textSize, textPaint)
            textPaint.textSize = 60f // Reset text size for the time
        }
    }
}