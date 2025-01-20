package com.wuaha.ktl_p1.ui.ruleta.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import com.wuaha.ktl_p1.ui.ruleta.data.RuletaOpcion
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class InteriorRuletaView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var opciones: List<RuletaOpcion> = listOf()
    private var enableRandomOptions: Boolean = true
    // private var anguloSeleccionado: Float = 0f
    private var velocidadAnimacion: Int = 5
    private var duracionAnimacion: Long = 5000
    private var currentRotation = 0f
    private var rotationAnimator: ValueAnimator? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
    }

    init {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    fun setOpciones(nuevasOpciones: List<RuletaOpcion>) {
        if (nuevasOpciones.size < 2) {
            opciones = listOf(
                nuevasOpciones.firstOrNull() ?: RuletaOpcion("Vacío", Color.GRAY),
                RuletaOpcion("Vacío", Color.DKGRAY)
            )
        } else {
            opciones = nuevasOpciones
        }
        if (!RuletaOpcion.validarProbabilidades(opciones)) {
            return
        }
        invalidate()
    }

    fun setEnableRandomOptions(enable: Boolean) {
        enableRandomOptions = enable
    }

//    fun setAnguloSeleccionado(angulo: Float) {
//        anguloSeleccionado = angulo.coerceIn(0f, 360f)
//        invalidate()
//    }

    fun setVelocidadAnimacion(velocidad: Int) {
        velocidadAnimacion = velocidad.coerceIn(1, 10)
    }

    fun setDuracionAnimacion(duracionMs: Long) {
        duracionAnimacion = duracionMs.coerceIn(1000, 30000)
    }

    fun girar(callback: ((RuletaOpcion) -> Unit)? = null) {
        rotationAnimator?.cancel()

        // Calcular rotación aleatoria
        val rotacionesBase = 360f * velocidadAnimacion
        val rotacionesExtra = Random.nextFloat() * 360f * 2 // 0-2 vueltas extra aleatorias
        val anguloFinal = currentRotation + rotacionesBase + rotacionesExtra

        val duracionAleatoria = duracionAnimacion + Random.nextLong(-1000, 1000)

        rotationAnimator = ValueAnimator.ofFloat(currentRotation, anguloFinal).apply {
            duration = duracionAleatoria.coerceIn(1000, 30000)
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                rotation = it.animatedValue as Float
                currentRotation = rotation
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    val opcionSeleccionada = obtenerOpcionSeleccionada()
                    Toast.makeText(context, opcionSeleccionada.texto, Toast.LENGTH_SHORT).show()
                    callback?.invoke(opcionSeleccionada)
                }
            })
            start()
        }
    }

    private fun obtenerOpcionSeleccionada(): RuletaOpcion {
        val anguloNormalizado = (360 - (rotation % 360)) % 360
        var anguloAcumulado = 0f

        opciones.forEach { opcion ->
            val anguloSegmento = (opcion.probabilidad ?: 0f) * 360f / 100f
            if (anguloNormalizado >= anguloAcumulado &&
                anguloNormalizado < anguloAcumulado + anguloSegmento) {
                return opcion
            }
            anguloAcumulado += anguloSegmento
        }
        return opciones.first()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centro = width.coerceAtMost(height) / 2f
        val radio = centro * 0.95f
        var anguloInicio = 0f

        val opcionesDibujar = if (enableRandomOptions) {
            opciones.shuffled()
        } else {
            opciones
        }

        opcionesDibujar.forEach { opcion ->
            val anguloSegmento = (opcion.probabilidad ?: 0f) * 360f / 100f

            paint.color = opcion.colorFondo
            canvas.drawArc(
                centro - radio,
                centro - radio,
                centro + radio,
                centro + radio,
                anguloInicio,
                anguloSegmento,
                true,
                paint
            )

            val anguloTexto = Math.toRadians(anguloInicio + anguloSegmento / 2.0)
            val x = (centro + radio * 0.6 * cos(anguloTexto)).toFloat()
            val y = (centro + radio * 0.6 * sin(anguloTexto)).toFloat()

            textPaint.apply {
                color = opcion.colorTexto
                textSize = opcion.tamañoTexto
            }
            val anguloTextoAdicional = 15f
            canvas.rotate(90f + anguloInicio + anguloSegmento / 2, x, y)

            // Alinear el texto a gusto (-90f)
            canvas.save()
            canvas.rotate(-90f, x + 20, y)
            canvas.drawText(opcion.texto, x, y, textPaint)
            canvas.restore()

            canvas.rotate(-(90f + anguloInicio + anguloSegmento / 2), x, y)

            anguloInicio += anguloSegmento
        }
    }
}