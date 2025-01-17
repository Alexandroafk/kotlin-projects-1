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
    private var currentTextSize: Float = 60f

    private val themes = listOf(
        Theme("#00FF00", "#000000", "#FFFFFF"), // Verde, Negro, Blanco
        Theme("#0000FF", "#00FF00", "#000000")  // Azul, Verde, Negro
    )

    private data class Theme(
        val borderColor: String,
        val backgroundColor: String,
        val textColor: String
    ) {
        fun getBorderColorInt(): Int = Color.parseColor(borderColor)
        fun getBackgroundColorInt(): Int = Color.parseColor(backgroundColor)
        fun getTextColorInt(): Int = Color.parseColor(textColor)
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = themes[currentTheme - 1].getBorderColorInt()
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = themes[currentTheme - 1].getBackgroundColorInt()
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = themes[currentTheme - 1].getTextColorInt()
        textSize = currentTextSize
        textAlign = Paint.Align.CENTER
    }

    /**
     * Coloca el tiempo personalizado.
     */
    fun updateTime(newHours: Int, newMinutes: Int) {
        hours = (newHours % 24 + 24) % 24 // Asegura formato de 24 horas
        minutes = (newMinutes % 60 + 60) % 60 // Asegura formato de 60 minutos
        invalidate()
    }

    /**
     * Añade minutos a la hora actual
     */
    fun addMinutes(minutesToAdd: Int) {
        val totalMinutes = hours * 60 + minutes + minutesToAdd
        hours = (totalMinutes / 60) % 24
        minutes = totalMinutes % 60
        invalidate()
    }

    /**
     * Añade horas a la hora actual.
     */
    fun addHours(hoursToAdd: Int) {
        hours = (hours + hoursToAdd) % 24
        invalidate()
    }

    /**
     * Establece el tema actual por número.
     */
    fun applyTheme(themeNumber: Int) {
        if (themeNumber in 1..themes.size) {
            currentTheme = themeNumber
            updateColors()
        }
    }

    /**
     * Actualiza los colores basados en el tema actual.
     */
    private fun updateColors() {
        val theme = themes[currentTheme - 1]
        borderPaint.color = theme.getBorderColorInt()
        backgroundPaint.color = theme.getBackgroundColorInt()
        textPaint.color = theme.getTextColorInt()
        invalidate()
    }

    /**
     * Cambia el tamaño del texto.
     */
    fun setTextSize(newSize: Float) {
        currentTextSize = newSize
        textPaint.textSize = currentTextSize
        invalidate()
    }

    /**
     * Cambia la alineación del texto.
     */
    fun setTextAlignment(newAlignment: Paint.Align) {
        textPaint.textAlign = newAlignment
        invalidate()
    }

    /**
     * Obtiene la alineación actual del texto.
     */
    fun getTimeTextAlignment(): Paint.Align {
        return textPaint.textAlign
    }

    /**
     * Obtiene el tema actual.
     */
    fun getCurrentTheme(): Int {
        return currentTheme
    }

    /**
     * Obtiene el tamaño actual del texto.
     */
    fun getCurrentTextSize(): Float {
        return currentTextSize
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
