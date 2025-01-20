package com.wuaha.ktl_p1.ui.ruleta.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import com.wuaha.ktl_p1.ui.ruleta.data.RuletaOpcion

class RuletaView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val ruletaInterior = InteriorRuletaView(context)
    private val botonCentral = CentroRuletaView(context)

    init {
        addView(ruletaInterior)
        addView(botonCentral)

        botonCentral.onClick = {
            ruletaInterior.girar()
        }

        // Centrar el botón
        botonCentral.layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
        }
    }

    fun setOpciones(nuevasOpciones: List<RuletaOpcion>) = ruletaInterior.setOpciones(nuevasOpciones)
    fun setAnguloSeleccionado(angulo: Float) = ruletaInterior.setAnguloSeleccionado(angulo)
    fun setVelocidadAnimacion(velocidad: Int) = ruletaInterior.setVelocidadAnimacion(velocidad)
    fun setDuracionAnimacion(duracionMs: Long) = ruletaInterior.setDuracionAnimacion(duracionMs)
    fun girar(callback: ((RuletaOpcion) -> Unit)? = null) = ruletaInterior.girar(callback)

    fun setBotonTexto(texto: String) {
        botonCentral.texto = texto
        botonCentral.invalidate()
    }

    fun setBotonRadio(radio: Float) {
        botonCentral.radioBoton = radio
        botonCentral.invalidate()
    }

    fun setBotonEstilo(
        colorFondo: Int = Color.parseColor("#b2f3bb"),
        colorBorde: Int = Color.BLACK,
        colorTexto: Int = Color.BLACK,
        tamañoTexto: Float = 40f
    ) {
        botonCentral.apply {
            this.colorFondo = colorFondo
            this.colorBorde = colorBorde
            this.colorTexto = colorTexto
            this.tamañoTexto = tamañoTexto
            invalidate()
        }
    }
}