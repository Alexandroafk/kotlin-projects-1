package com.wuaha.ktl_p1.ui.ruleta.data

import android.graphics.Color

data class RuletaOpcion(
    val texto: String,
    val colorFondo: Int,
    var probabilidad: Float? = null,
    val tamañoTexto: Float = 60f,
    val colorTexto: Int = Color.BLACK
) {
    init {
        if (probabilidad != null && probabilidad == 0f) {
            probabilidad = null
        }
    }

    companion object {
        fun validarProbabilidades(opciones: List<RuletaOpcion>): Boolean {
            val probabilidadesDefinidas = opciones.mapNotNull { it.probabilidad }
            val sumaProbabilidades = probabilidadesDefinidas.sum()

            // Verificar suma máxima
            if (sumaProbabilidades > 100f) {
                println("Error: La suma de probabilidades excede 100%")
                return false
            }

            // Calcular probabilidad para opciones no definidas (null o 0f convertido a null)
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
    }
}