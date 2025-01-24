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
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import com.wuaha.ktl_p1.ui.ruleta.data.RuletaOpcion
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class RuletaView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Propiedades de la ruleta
    private var opciones: List<RuletaOpcion> = listOf()
    private var opcionesOrdenadas: List<RuletaOpcion> = emptyList()
    private var opcionesConDefault: List<RuletaOpcion> = emptyList()
    private var porcentajeSobrante: Float = 0f
    private var enableRandomOptions: Boolean = true
    private var selectedRuletaAngulo: Float = 0f
    private var velocidadAnimacion: Int = 5
    private var duracionAnimacion: Long = 5000
    private var currentRotation = 0f
    private var rotationAnimator: ValueAnimator? = null

    // Estado de la ruleta
    var isGiroActivo: Boolean = false
        private set

    // Herramientas de dibujo
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
    }

    init {
        textPaint.textSize = 40f
    }

    // --------------------------
    // Configuración de la ruleta
    // --------------------------

    fun setOpciones(nuevasOpciones: List<RuletaOpcion>) {
        val opcionesLimpias = nuevasOpciones.filterNot { it.texto == "Vacío" }

        if (!RuletaOpcion.validarProbabilidades(opcionesLimpias)) {
            Toast.makeText(context, "Error: Probabilidades inválidas", Toast.LENGTH_SHORT).show()
            return
        }

        val sumaProbabilidades = opcionesLimpias.fold(0f) { acc, opcion ->
            acc + (opcion.probabilidad ?: 0f)
        }

        porcentajeSobrante = 100f - sumaProbabilidades

        opcionesConDefault = opcionesLimpias.toMutableList().apply {
            if (porcentajeSobrante > 0) {
                add(RuletaOpcion.getDefaultOption(porcentajeSobrante))
            }
        }

        opciones = opcionesConDefault
        actualizarOrdenOpciones()
    }

    fun setEnableRandomOptions(enable: Boolean) {
        enableRandomOptions = enable
        actualizarOrdenOpciones()
    }

    fun isRandomOptionsEnabled(): Boolean = enableRandomOptions

    private fun actualizarOrdenOpciones() {
        opcionesOrdenadas = if (enableRandomOptions) opcionesConDefault.shuffled() else opcionesConDefault
        invalidate()
    }

    fun setVelocidadAnimacion(velocidad: Int) {
        velocidadAnimacion = velocidad.coerceIn(1, 50)
    }

    fun getVelocidadAnimacion(): Int = velocidadAnimacion

    fun setMinDuracionAnimacion(duracionMs: Long) {
        duracionAnimacion = duracionMs.coerceIn(1000, 30000)
    }

    // --------------------------
    // Lógica de giro de la ruleta
    // --------------------------

    fun girar(callback: ((RuletaOpcion) -> Unit)? = null) {
        if (isGiroActivo) return
        isGiroActivo = true

        rotationAnimator?.cancel()

        val rotacionesBase = 360f * velocidadAnimacion
        val rotacionesExtra = Random.nextFloat() * 360f * 2
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
                    isGiroActivo = false
                    val opcionSeleccionada = obtenerOpcionSeleccionada()
                    callback?.invoke(opcionSeleccionada)
                }
            })
            start()
        }
    }

    private fun obtenerOpcionSeleccionada(): RuletaOpcion {
        val anguloNormalizado = (360 - (rotation % 360)) % 360
        var anguloAcumulado = 0f

        opcionesOrdenadas.forEach { opcion ->
            val anguloSegmento = (opcion.probabilidad ?: 0f) * 360f / 100f
            if (anguloNormalizado >= anguloAcumulado && anguloNormalizado < anguloAcumulado + anguloSegmento) {
                return opcion
            }
            anguloAcumulado += anguloSegmento
        }

        return opcionesOrdenadas.firstOrNull() ?: RuletaOpcion("Error", "#FF0000", 100f)
    }

    // --------------------------
    // Dibujo de la ruleta
    // --------------------------

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centro = width.coerceAtMost(height) / 2f
        val radio = centro * 0.95f
        var anguloInicio = 0f

        opcionesOrdenadas.forEach { opcion ->
            val anguloSegmento = (opcion.probabilidad ?: 0f) * 360f / 100f

            // Dibujar el segmento de la ruleta
            paint.color = opcion.colorFondoInt
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

            // Dibujar el texto en el segmento
            val anguloTexto = Math.toRadians(anguloInicio + anguloSegmento / 2.0)
            val x = (centro + radio * 0.6 * cos(anguloTexto)).toFloat()
            val y = (centro + radio * 0.6 * sin(anguloTexto)).toFloat()

            textPaint.apply {
                color = opcion.colorTextoInt
                textSize = opcion.tamañoTexto
            }
            canvas.rotate(90f + anguloInicio + anguloSegmento / 2, x, y)
            canvas.save()
            canvas.rotate(-90f, x + 20, y)
            canvas.drawText("${opcion.texto} (${opcion.probabilidad}%)", x, y, textPaint)
            canvas.restore()
            canvas.rotate(-(90f + anguloInicio + anguloSegmento / 2), x, y)

            anguloInicio += anguloSegmento
        }
    }
}