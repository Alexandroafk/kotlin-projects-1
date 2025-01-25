package com.wuaha.ktl_p1.ui.ruleta

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.wuaha.ktl_p1.R
import com.wuaha.ktl_p1.ui.ruleta.data.RuletaOpcion
import com.wuaha.ktl_p1.ui.ruleta.helper.RuletaOptionDialog
import com.wuaha.ktl_p1.ui.ruleta.views.CentroRuletaView
import com.wuaha.ktl_p1.ui.ruleta.views.RuletaView

class RuletaActivity : AppCompatActivity() {

    // Vistas
    private lateinit var ruletaView: RuletaView
    private lateinit var centroRuletaView: CentroRuletaView
    private lateinit var engranajeIcono: ImageView
    private lateinit var agregarNuevaOpcionIcon: ImageView
    private lateinit var spinnerOpciones: Spinner
    private lateinit var toggleDeshabilitadas: CheckBox

    // Opciones iniciales
    private val opciones = mutableListOf(
        RuletaOpcion("Opción 1", "#fc3f3f", 3f, 0f, "#000000"),
        RuletaOpcion("Opción 2", "#FF00FF", 10f, 0f, "#000000"),
        RuletaOpcion("Opción 3", "#8bdbe0", 10f, 0f, "#000000"),
        RuletaOpcion("Opción 4", "#7a7dbf", 10f, 0f, "#000000"),
        RuletaOpcion("Opción 5", "#c9dbad", 10f, 0f, "#000000"),
        RuletaOpcion("Opción 6", "#f5e49a", 10f, 0f, "#000000", false)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruleta)

        inicializarVistas()
        configurarRuleta()
        configurarBotonCentral()
        configurarEngranajeCentral()
        configurarBotonAgregarOpcion()
        actualizarRuleta()
    }

    // --------------------------
    // Inicialización de vistas
    // --------------------------

    private fun inicializarVistas() {
        ruletaView = findViewById(R.id.ruleta_view_xml)
        centroRuletaView = findViewById(R.id.centro_ruleta_view_xml)
        engranajeIcono = findViewById(R.id.ruleta_settings_ic_img_xml)
        agregarNuevaOpcionIcon = findViewById(R.id.ruleta_add_option_ic_img_xml)
        spinnerOpciones = findViewById(R.id.spinner_opciones)
        toggleDeshabilitadas = findViewById(R.id.toggle_deshabilitadas)
    }

    // --------------------------
    // Configuraciónes
    // --------------------------

    private fun configurarRuleta() {
        ruletaView.apply {
            setOpciones(opciones.filter { it.habilitada })
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

    private fun configurarBotonAgregarOpcion() {
        agregarNuevaOpcionIcon.setOnClickListener {
            showCreateOptionDialog()
        }
    }

    private fun configurarSpinner() {
        val listaSpinner = mutableListOf<Any>().apply {
            add("Editar una opción")
            addAll(opciones.filter { it.habilitada })
            if (toggleDeshabilitadas.isChecked) {
                addAll(opciones.filter { !it.habilitada })
            }
        }

        // 1. Definir colores desde el tema
        val colorOnSurface = ContextCompat.getColor(this, R.color.on_surface)
        val colorDisabled = ContextCompat.getColor(this, R.color.disabled_color)

        val adapter = object : ArrayAdapter<Any>(
            this,
            R.layout.simple_spinner_item,
            listaSpinner
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)

                when (val item = getItem(position)) {
                    is RuletaOpcion -> {
                        textView.text = item.texto
                        textView.setTextColor(if (item.habilitada) colorOnSurface else colorDisabled)
                    }
                    else -> {
                        textView.text = item.toString()
                        textView.setTextColor(colorOnSurface)
                    }
                }
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                    ?: layoutInflater.inflate(R.layout.spinner_dropdown_item, parent, false)

                val textView = view.findViewById<TextView>(android.R.id.text1)
                when (val item = getItem(position)) {
                    is RuletaOpcion -> {
                        textView.text = item.texto
                        textView.setTextColor(if (item.habilitada) colorOnSurface else colorDisabled)
                    }
                    else -> textView.setTextColor(colorOnSurface)
                }
                return view
            }
        }

        // 2. Asignar layout para dropdown
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        // 3. Configurar spinner
        spinnerOpciones.adapter = adapter
        spinnerOpciones.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) return
                val selected = parent?.getItemAtPosition(position) as? RuletaOpcion
                selected?.let { opciones.indexOf(it).takeIf { it != -1 }?.let { showEditOptionDialog(it) } }
                parent?.setSelection(0)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        toggleDeshabilitadas.setOnCheckedChangeListener { _, _ -> configurarSpinner() }
    }

    // --------------------------
    // Actualización de la ruleta
    // --------------------------

    private fun actualizarRuleta() {
        val opcionesHabilitadas = opciones.filter { it.habilitada }
        ruletaView.setOpciones(opcionesHabilitadas)
        ruletaView.invalidate()
        configurarSpinner()
    }

    // --------------------------
    // Funciones para crear y editar opciones
    // --------------------------
    private fun showCreateOptionDialog() {
        val sumaActual = opciones.filter { it.habilitada }.sumByDouble { it.probabilidad?.toDouble() ?: 0.0 }
        val porcentajeSobrante = (100.0 - sumaActual).toFloat()

        val newOption = RuletaOpcion(
            texto = "Nueva opción",
            colorFondo = "#808080",
            probabilidad = porcentajeSobrante,
            habilitada = true
        )

        val dialog = RuletaOptionDialog(
            context = this,
            currentOption = newOption,
            allOptions = opciones,
            ruletaView = ruletaView,
            title = "Crear nueva opción",
            isCreateMode = true,
            onUpdate = {
                actualizarRuleta()
                Toast.makeText(this, "Opción creada!", Toast.LENGTH_SHORT).show()
            }
        )
        dialog.show()
    }

    private fun showEditOptionDialog(opcionIndex: Int) {
        val dialog = RuletaOptionDialog(
            context = this,
            currentOption = opciones[opcionIndex], // Opción a editar (Se pasa como argumento)
            allOptions = opciones, // Lista completa de opciones
            ruletaView = ruletaView, // Vista de la ruleta
            title = "Editar Opción (${opciones[opcionIndex].texto})", // Título del dialog
            isCreateMode = false,
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