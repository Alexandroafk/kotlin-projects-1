package com.wuaha.ktl_p1.ui.ruleta

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.colorChooser
import com.afollestad.materialdialogs.customview.customView
import com.wuaha.ktl_p1.R
import com.wuaha.ktl_p1.ui.ruleta.data.RuletaOpcion
import com.wuaha.ktl_p1.ui.ruleta.helper.EditOptionDialog
import com.wuaha.ktl_p1.ui.ruleta.views.CentroRuletaView
import com.wuaha.ktl_p1.ui.ruleta.views.RuletaView

class RuletaActivity : AppCompatActivity() {

    // Vistas
    private lateinit var ruletaView: RuletaView
    private lateinit var centroRuletaView: CentroRuletaView
    private lateinit var engranajeIcono: ImageView

    // Opciones iniciales
    private val opciones = mutableListOf(
        RuletaOpcion("Opción 1", "#fc3f3f", 3f, 0f, "#000000"),
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
        configurarEngranajeCentral()
        actualizarRuleta()
    }

    // --------------------------
    // Inicialización de vistas
    // --------------------------

    private fun inicializarVistas() {
        ruletaView = findViewById(R.id.ruleta_view_xml)
        centroRuletaView = findViewById(R.id.centro_ruleta_view_xml)
        engranajeIcono = findViewById(R.id.ruleta_settings_ic_img_xml)
    }

    // --------------------------
    // Configuraciónes
    // --------------------------

    private fun configurarRuleta() {
        ruletaView.apply {
            setOpciones(opciones)
            setMinDuracionAnimacion(4000)
            setVelocidadAnimacion(3)
        }
    }

    private fun configurarBotonCentral() {
        centroRuletaView.onClick = {
            if (!ruletaView.isGiroActivo) {
                ruletaView.girar { opcion ->
                    Toast.makeText(this, opcion.texto, Toast.LENGTH_SHORT).show()
                    // Aqui colocas la llamada al historial donde añades la opcion que salio
                    // La variable es "opcion"
                }
            } else {
                // Mensaje cuando se presiona muchas veces seguidas el boton.
                Toast.makeText(this, "Espere el resultado.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarEngranajeCentral() {
        engranajeIcono.setOnClickListener {
            showEditOptionDialog(1)
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
    // Funcion para abrir la ventana de editar una opción
    // --------------------------
    private fun showEditOptionDialog(opcionIndex: Int) {
        val dialog = EditOptionDialog(
            context = this,
            currentOptionIndex = opcionIndex, // Opción a editar (Se pasa como argumento)
            allOptions = opciones, // Lista completa de opciones
            ruletaView = ruletaView, // Vista de la ruleta
            title = "Editar Opción (${opciones[opcionIndex].texto})", // Título del dialog
            onUpdate = {
                // 2. Callback para actualizaciones
                actualizarRuleta()
                Toast.makeText(this@RuletaActivity, "Ruleta actualizada!", Toast.LENGTH_SHORT).show()
            }
        )

        // 3. Mostrar el diálogo
        dialog.show()
    }
}