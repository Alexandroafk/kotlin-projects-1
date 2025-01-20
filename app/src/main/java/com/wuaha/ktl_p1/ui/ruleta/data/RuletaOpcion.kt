package com.wuaha.ktl_p1.ui.ruleta.data

import android.graphics.Color

data class RuletaOpcion(
    val texto: String,
    val colorFondo: Int,
    var probabilidad: Float? = null,
    val colorTexto: Int = Color.BLACK,
    val tamañoTexto: Float = 40f
) {
    companion object {
        fun validarProbabilidades(opciones: List<RuletaOpcion>): Boolean {
            val sumaProbabilidades = opciones
                .mapNotNull { it.probabilidad }
                .sum()

            if (sumaProbabilidades > 100f) {
                println("Error: La suma de probabilidades excede 100%")
                return false
            }

            val opcionesSinProbabilidad = opciones.count { it.probabilidad == null }
            if (opcionesSinProbabilidad > 0) {
                val probabilidadRestante = 100f - sumaProbabilidades
                val probabilidadPorOpcion = probabilidadRestante / opcionesSinProbabilidad
                opciones.forEach {
                    if (it.probabilidad == null) it.probabilidad = probabilidadPorOpcion
                }
            }
            return true
        }
    }
}