package com.wuaha.ktl_p1.ui.ruleta

import android.os.Bundle
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout
import com.wuaha.ktl_p1.R
import com.wuaha.ktl_p1.ui.ruleta.data.RuletaOpcion
import com.wuaha.ktl_p1.ui.ruleta.views.CentroRuletaView
import com.wuaha.ktl_p1.ui.ruleta.views.RuletaView

class RuletaActivity : AppCompatActivity() {

    // Vistas
    private lateinit var ruletaView: RuletaView
    private lateinit var centroRuletaView: CentroRuletaView
    private lateinit var gridControles: GridLayout

    // Estado de la ruleta
    private var currentRow = 0
    private val opciones = mutableListOf(
        RuletaOpcion("Opción 1", "#fc3f3f", 3f, 40f, "#000000"),
        RuletaOpcion("Opción 2", "#FF00FF", 10f, 0f, "#000000"),
        RuletaOpcion("Opción 3", "#8bdbe0", 10f, 0f, "#000000"),
        RuletaOpcion("Opción 4", "#7a7dbf", 10f, 0f, "#000000"),
        RuletaOpcion("Opción 5", "#c9dbad", 10f, 0f, "#000000"),
        RuletaOpcion("Opción 6", "#f5e49a", 10f, 0f, "#000000")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruleta)

        inicializarVistas()
        configurarRuleta()
        configurarBotonCentral()
        actualizarRuleta()
    }

    // --------------------------
    // Inicialización de vistas
    // --------------------------

    private fun inicializarVistas() {
        ruletaView = findViewById(R.id.ruleta_view_xml)
        centroRuletaView = findViewById(R.id.centro_ruleta_view_xml)
        // gridControles = findViewById(R.id.panel_control)
    }

    // --------------------------
    // Configuración de la ruleta
    // --------------------------

    private fun configurarRuleta() {
        ruletaView.apply {
            setOpciones(opciones)
            setMinDuracionAnimacion(4000)
            setVelocidadAnimacion(3)
        }
    }

    // --------------------------
    // Configuración del botón central
    // --------------------------

    private fun configurarBotonCentral() {
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

    // --------------------------
    // Actualización de la ruleta
    // --------------------------

    private fun actualizarRuleta() {
        val opcionesHabilitadas = opciones.filter { it.habilitada }
        ruletaView.setOpciones(opcionesHabilitadas)
    }



























    // --------------------------
    // Controles de la interfaz (Prueba)
    // --------------------------

    private fun agregarControles(opciones: List<RuletaOpcion>) {
        agregarSwitch("Orden Aleatorio", ruletaView.isRandomOptionsEnabled()) {
            ruletaView.setEnableRandomOptions(it)
        }

        agregarSeekBar("Velocidad", 1, 10, ruletaView.getVelocidadAnimacion()) {
            ruletaView.setVelocidadAnimacion(it)
        }

        opciones.forEachIndexed { index, opcion ->
            agregarSwitch("Opción ${index + 1}", opcion.habilitada) { isChecked ->
                opcion.habilitada = isChecked
                actualizarRuleta()
            }
        }
    }

    private fun agregarSwitch(texto: String, estadoInicial: Boolean, callback: (Boolean) -> Unit) {
        val textView = TextView(this).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                rowSpec = GridLayout.spec(currentRow)
                columnSpec = GridLayout.spec(0)
            }
            this.text = texto
        }

        val switch = Switch(this).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                rowSpec = GridLayout.spec(currentRow)
                columnSpec = GridLayout.spec(1)
            }
            isChecked = estadoInicial
            setOnCheckedChangeListener { _, isChecked -> callback(isChecked) }
        }

        gridControles.addView(textView)
        gridControles.addView(switch)
        currentRow++
    }

    private fun agregarSeekBar(texto: String, min: Int, max: Int, valorInicial: Int, callback: (Int) -> Unit) {
        val textView = TextView(this).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                rowSpec = GridLayout.spec(currentRow)
                columnSpec = GridLayout.spec(0)
            }
            this.text = texto
        }

        val seekBar = SeekBar(this).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                rowSpec = GridLayout.spec(currentRow)
                columnSpec = GridLayout.spec(1)
            }
            this.max = max
            progress = valorInicial
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    callback(progress.coerceIn(min, max))
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        gridControles.addView(textView)
        gridControles.addView(seekBar)
        currentRow++
    }
}