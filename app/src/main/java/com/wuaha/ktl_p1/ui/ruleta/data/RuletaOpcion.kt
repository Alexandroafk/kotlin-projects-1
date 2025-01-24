package com.wuaha.ktl_p1.ui.ruleta.data

import android.graphics.Color
import androidx.annotation.ColorInt

data class RuletaOpcion(
    var texto: String = "Vacio",
    var colorFondo: String = "#808080", // Gris por defecto (hex)
    var probabilidad: Float? = null,
    var tamañoTexto: Float = 50f,
    var colorTexto: String = "#000000", // Negro por defecto (hex)
    var habilitada: Boolean = true
) {
    @ColorInt val colorFondoInt: Int = parseColorHex(colorFondo, Color.GRAY)
    @ColorInt val colorTextoInt: Int = parseColorHex(colorTexto, Color.BLACK)

    init {
        if (probabilidad == 0f) probabilidad = null
        if (tamañoTexto == 0f) tamañoTexto = 50f
    }

    private fun parseColorHex(hex: String, default: Int): Int {
        return try {
            Color.parseColor(hex)
        } catch (e: IllegalArgumentException) {
            default
        }
    }

    companion object {
        fun validarProbabilidades(opciones: List<RuletaOpcion>): Boolean {
            val sumaProbabilidades = opciones.fold(0f) { acc, opcion ->
                acc + (opcion.probabilidad ?: 0f)
            }

            if (sumaProbabilidades > 100f) return false

            val opcionesSinProbabilidad = opciones.count { it.probabilidad == null }
            if (opcionesSinProbabilidad > 0) {
                val probabilidadRestante = 100f - sumaProbabilidades
                val probabilidadPorOpcion = probabilidadRestante / opcionesSinProbabilidad
                opciones.forEach { opcion ->
                    if (opcion.probabilidad == null) {
                        opcion.probabilidad = probabilidadPorOpcion
                    }
                }
            }

            return true
        }

        fun getDefaultOption(probabilidadSobrante: Float = 0f): RuletaOpcion {
            return RuletaOpcion(
                texto = "Vacío",
                colorFondo = "#989e98",
                probabilidad = probabilidadSobrante,
                tamañoTexto = 50f,
                colorTexto = "#000000"
            )
        }
    }
}