package com.wuaha.ktl_p1.ui.ruleta.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class RuletaView constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var options = listOf<String>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 70f
        textAlign = Paint.Align.CENTER
    }
    private val rectF = RectF()
    private var sectionAngle: Float = 0f

    fun setOptions(options: List<String>) {
        this.options = options
        sectionAngle = 360f / options.size
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Configurar el rectángulo para la ruleta
        val padding = 50f
        rectF.set(padding, padding, width - padding, height - padding)

        // Dibujar las secciones de la ruleta
        var startAngle = 0f
        for (i in options.indices) {
            paint.color = getRandomColor()
            canvas.drawArc(rectF, startAngle, sectionAngle, true, paint)

            // Dibujar el texto dentro de la sección
            val angle = Math.toRadians((startAngle + sectionAngle / 2).toDouble())
            val textX = (width / 2 + cos(angle) * (rectF.width() / 3)).toFloat()
            val textY = (height / 2 + sin(angle) * (rectF.height() / 3)).toFloat()
            canvas.drawText(options[i], textX, textY, textPaint)

            startAngle += sectionAngle
        }
    }

    fun getSelectedIndex(finalAngle: Float): Int {
        val normalizedAngle = (finalAngle % 360 + 360) % 360 // Asegurar que esté entre 0-360
        return ((options.size - normalizedAngle / sectionAngle).toInt()) % options.size
    }

    private fun getRandomColor(): Int {
        return Color.rgb((50..255).random(), (50..255).random(), (50..255).random())
    }

}