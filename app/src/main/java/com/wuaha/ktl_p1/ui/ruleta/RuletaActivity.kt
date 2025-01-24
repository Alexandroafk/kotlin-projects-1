package com.wuaha.ktl_p1.ui.ruleta

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wuaha.ktl_p1.R
import com.wuaha.ktl_p1.ui.ruleta.data.RuletaOpcion
import com.wuaha.ktl_p1.ui.ruleta.views.CentroRuletaView
import com.wuaha.ktl_p1.ui.ruleta.views.RuletaView

class RuletaActivity : AppCompatActivity() {

    private lateinit var ruletaView: RuletaView
    private lateinit var centroRuletaView: CentroRuletaView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruleta)

        ruletaView = findViewById(R.id.ruleta_view_xml)
        centroRuletaView = findViewById(R.id.centro_ruleta_view_xml)

        val opciones = listOf(
            RuletaOpcion("Opción A", Color.RED, 3f, 40f, Color.BLACK),
            RuletaOpcion("Opción B", Color.BLUE,20f, 40f),
            RuletaOpcion("Opción C", Color.GREEN,40f, 40f),
            RuletaOpcion("Opción D", Color.YELLOW,0f, 40f), // Si se pasa 0f como probabilidad, se
            RuletaOpcion("Opción E", Color.MAGENTA, )                             // asigna una probabilidad automatica.
        )

        ruletaView.apply {
            setOpciones(opciones)
            setMinDuracionAnimacion(4000)
        }

        centroRuletaView.onClick = {
            if (!ruletaView.isGiroActivo) {
                ruletaView.girar { opcion ->
                    Toast.makeText(this, opcion.texto, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Espere el resultado.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}