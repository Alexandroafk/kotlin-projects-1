package com.wuaha.ktl_p1.ui.ruleta

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wuaha.ktl_p1.R
import com.wuaha.ktl_p1.ui.ruleta.data.RuletaOpcion
import com.wuaha.ktl_p1.ui.ruleta.views.RuletaView
import kotlin.random.Random

class RuletaActivity : AppCompatActivity() {

    private lateinit var ruletaView: RuletaView
    private lateinit var botonGirarRuleta: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruleta)

        ruletaView = findViewById(R.id.ruleta_view_xml)

        val opciones = listOf(
            RuletaOpcion("Opción 1", Color.RED, 10f),
            RuletaOpcion("Opción 2", Color.BLUE,50f),
            RuletaOpcion("Opción 3", Color.GREEN, 20f, ),
            RuletaOpcion("Opción 4", Color.YELLOW,5f, ),
            RuletaOpcion("Opción 5", Color.MAGENTA,15f, )
        )

        ruletaView.apply {
            setOpciones(opciones)
            // setAnguloSeleccionado(Random.nextFloat() * 360f)
            setVelocidadAnimacion(10)
            setDuracionAnimacion(10000)

            // Opcional: callback para manejar la selección
            girar { opcionSeleccionada ->
                Toast.makeText(this@RuletaActivity,
                    "¡${opcionSeleccionada.texto} seleccionada!",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}