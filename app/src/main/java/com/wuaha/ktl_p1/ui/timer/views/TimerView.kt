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

    private var currentTheme: Int = 1
    private val themes = listOf(
        Theme(Color.GREEN, Color.BLACK, Color.WHITE),
        Theme(Color.BLUE, Color.GREEN, Color.BLACK)
    )

    private var currentTextSize: Float = 60f

    private data class Theme(val borderColor: Int, val backgroundColor: Int, val textColor: Int)

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = themes[currentTheme - 1].borderColor
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = themes[currentTheme - 1].backgroundColor
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = themes[currentTheme - 1].textColor
        textSize = currentTextSize
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
     * Actualiza los colores basados en el tema actual
     */
    private fun updateColors() {
        borderPaint.color = themes[currentTheme - 1].borderColor
        backgroundPaint.color = themes[currentTheme - 1].backgroundColor
        textPaint.color = themes[currentTheme - 1].textColor
        invalidate()
    }

    /**
     * Establece el tema actual
     */
    fun applyTheme(themeNumber: Int) {
        if (themeNumber in 1..themes.size) {
            currentTheme = themeNumber
            updateColors()
        }
    }

    /**
     * Cambia el tamaño del texto
     */
    fun setTextSize(newSize: Float) {
        currentTextSize = newSize
        textPaint.textSize = currentTextSize
        invalidate()
    }

    /**
     * Cambia la alineación del texto
     */
    fun setTextAlignment(newAlignment: Paint.Align) {
        textPaint.textAlign = newAlignment
        invalidate()
    }

    /**
     * Obtiene la alineación actual del texto.
     */
    fun getTimerTextAlignment(): Paint.Align {
        return textPaint.textAlign
    }

    // Método público para obtener currentTheme
    fun getCurrentTheme(): Int {
        return currentTheme
    }

    // Método público para obtener currentTextSize
    fun getCurrentTextSize(): Float {
        return textPaint.textSize
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Dibuja el temporizador
        val padding = 16f
        val rectLeft = padding
        val rectTop = padding
        val rectRight = width - padding
        val rectBottom = height - padding
        canvas.drawRoundRect(rectLeft, rectTop, rectRight, rectBottom, 30f, 30f, backgroundPaint)
        canvas.drawRoundRect(rectLeft, rectTop, rectRight, rectBottom, 30f, 30f, borderPaint)

        // Dibuja la hora
        val timeText = String.format("%02d:%02d", hours, minutes)
        val centerX = width / 2f
        val centerY = height / 2f
        canvas.drawText(timeText, centerX, centerY + textPaint.textSize / 3, textPaint)
    }
}
