package com.wuaha.ktl_p1.ui.ruleta

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wuaha.ktl_p1.R
import com.wuaha.ktl_p1.ui.ruleta.data.RuletaOpcion
import com.wuaha.ktl_p1.ui.ruleta.views.RuletaView

class RuletaActivity : AppCompatActivity() {

    private lateinit var ruletaView: RuletaView
    private lateinit var botonGirarRuleta: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruleta)

        ruletaView = findViewById(R.id.ruleta_view_xml)

        val opciones = listOf(
            RuletaOpcion("Opción 1", Color.RED, 5f, 40f, Color.BLACK),
            RuletaOpcion("Opción 2", Color.BLUE,10f, 40f),
            RuletaOpcion("Opción 3", Color.GREEN, 15f, 40f),
            RuletaOpcion("Opción 4", Color.YELLOW, 3f, 40f),
            RuletaOpcion("Opción 5", Color.MAGENTA, )
        )

        ruletaView.apply {
            setOpciones(opciones)

            // TODO falta arreglar
            // setVelocidadAnimacion(1)
            setMinDuracionAnimacion(2000)

//            // Opcional: callback para manejar la selección
//            girar { opcionSeleccionada ->
//                Toast.makeText(this@RuletaActivity,
//                    "¡${opcionSeleccionada.texto} seleccionada!",
//                    Toast.LENGTH_SHORT).show()
//            }
        }
    }
}