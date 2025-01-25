package com.wuaha.ktl_p1.ui.ruleta.data

import android.graphics.Color
import androidx.annotation.ColorInt

data class RuletaOpcion(
    var texto: String = "Vacio",
    var colorFondo: String = "#808080", // Gris por defecto (hex)
    var probabilidad: Float? = null,
    var tamañoTexto: Float = 30f,
    var colorTexto: String = "#000000", // Negro por defecto (hex)
    var habilitada: Boolean = true
) {
    @get:ColorInt
    val colorFondoInt: Int
        get() = parseColorHex(colorFondo, Color.GRAY)
    @get:ColorInt
    val colorTextoInt: Int
        get() = parseColorHex(colorTexto, Color.BLACK)

    init {
        if (tamañoTexto == 0f) tamañoTexto = 30f
    }

    private fun parseColorHex(hex: String, default: Int): Int {
        return try {
            if (hex.isEmpty()) return default
            if (!hex.startsWith("#")) return default
            Color.parseColor(hex)
        } catch (e: Exception) {
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

            if (opcionesSinProbabilidad == 0) return sumaProbabilidades <= 100f

            val probabilidadRestante = (100f - sumaProbabilidades).coerceAtLeast(0f)
            val probabilidadPorOpcion = probabilidadRestante / opcionesSinProbabilidad

            opciones.forEach { opcion ->
                if (opcion.probabilidad == null) {
                    opcion.probabilidad = probabilidadPorOpcion
                }
            }

            return true
        }

        fun getDefaultOption(probabilidadSobrante: Float = 0f): RuletaOpcion {
            return RuletaOpcion(
                texto = "Vacío",
                colorFondo = "#989e98",
                probabilidad = probabilidadSobrante,
                tamañoTexto = 30f,
                colorTexto = "#000000"
            )
        }
    }
}