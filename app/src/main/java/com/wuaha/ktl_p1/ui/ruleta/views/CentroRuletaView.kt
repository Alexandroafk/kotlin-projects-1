package com.wuaha.ktl_p1.ui.ruleta.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.pow
import kotlin.math.sqrt

class CentroRuletaView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    var texto: String = "Girar"
    var radioBoton: Float = 60f
    var tamañoTexto: Float = 40f
    var colorTexto: Int = Color.BLACK
    var colorFondo: Int = Color.parseColor("#b2f3bb")
    var colorBorde: Int = Color.BLACK
    var onClick: (() -> Unit)? = null

    init {
        isClickable = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = (radioBoton * 2).toInt()
        setMeasuredDimension(size, size)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                if (dentroDelCirculo(event.x, event.y)) {
                    onClick?.invoke()
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun dentroDelCirculo(x: Float, y: Float): Boolean {
        val centroX = width / 2f
        val centroY = height / 2f
        val distancia = sqrt((x - centroX).pow(2) + (y - centroY).pow(2))
        return distancia <= radioBoton
    }

    override fun onDraw(canvas: Canvas) {
        val centroX = width / 2f
        val centroY = height / 2f

        paint.apply {
            style = Paint.Style.FILL
            color = colorFondo
        }
        canvas.drawCircle(centroX, centroY, radioBoton, paint)

        paint.apply {
            style = Paint.Style.STROKE
            strokeWidth = 5f
            color = colorBorde
        }
        canvas.drawCircle(centroX, centroY, radioBoton, paint)

        textPaint.apply {
            color = colorTexto
            textSize = tamañoTexto
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(texto, centroX, centroY + tamañoTexto/3, textPaint)
    }
}