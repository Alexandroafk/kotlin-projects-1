package com.wuaha.ktl_p1.ui.ruleta

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.colorChooser
import com.afollestad.materialdialogs.customview.customView
import com.wuaha.ktl_p1.R
import com.wuaha.ktl_p1.ui.ruleta.data.RuletaOpcion
import com.wuaha.ktl_p1.ui.ruleta.views.CentroRuletaView
import com.wuaha.ktl_p1.ui.ruleta.views.RuletaView

class RuletaActivity : AppCompatActivity() {

    // Vistas
    private lateinit var ruletaView: RuletaView
    private lateinit var centroRuletaView: CentroRuletaView
    private lateinit var engranajeIcono: ImageView

    // Opciones iniciales
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
            showEditOptionsDialog()
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
    // Popup dialog configuracion
    // --------------------------
    private fun showEditOptionsDialog() {
        val opcion = opciones[0] // Edita la opción deseada

        MaterialDialog(this).show {
            title(text = "Editar Opción")
            customView(R.layout.dialog_edit_options, scrollable = true)

            // Configuración de vistas
            val nombreInput = view.findViewById<EditText>(R.id.edit_option_name)
            val colorInput = view.findViewById<EditText>(R.id.edit_option_color)
            val probabilidadInput = view.findViewById<EditText>(R.id.edit_option_probabilidad)
            val btnColor = view.findViewById<Button>(R.id.btn_select_color)

            // Configuración inicial
            try {
                val initialColor = Color.parseColor(opcion.colorFondo)
                btnColor.apply {
                    text = opcion.colorFondo.uppercase()
                    backgroundTintList = ColorStateList.valueOf(initialColor)
                    setTextColor(Color.BLACK) // Texto siempre en negro
                }
            } catch (e: Exception) {
                btnColor.apply {
                    text = "#000000"
                    backgroundTintList = ColorStateList.valueOf(Color.BLACK)
                    setTextColor(Color.BLACK) // Texto siempre en negro
                }
            }

            // Valores iniciales
            nombreInput.setText(opcion.texto)
            probabilidadInput.setText(opcion.probabilidad?.toString())

            // Selector de color
            btnColor.setOnClickListener {
                val currentColor = try {
                    Color.parseColor(opcion.colorFondo)
                } catch (e: Exception) {
                    Color.BLACK
                }

                val defaultColors = intArrayOf(
                    Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
                    Color.CYAN, Color.MAGENTA, Color.BLACK,
                    Color.parseColor("#FFA500"), Color.parseColor("#808080")
                )

                MaterialDialog(this@RuletaActivity).show {
                    title(text = "Seleccionar color")
                    colorChooser(
                        colors = defaultColors,
                        initialSelection = currentColor,
                        allowCustomArgb = true,
                        showAlphaSelector = false
                    ) { _, color ->
                        val hexColor = String.format("#%06X", 0xFFFFFF and color)
                        colorInput.setText(hexColor)

                        btnColor.apply {
                            text = hexColor.uppercase()
                            backgroundTintList = ColorStateList.valueOf(color)
                            setTextColor(Color.BLACK) // Texto siempre en negro
                        }
                    }
                    positiveButton(text = "Seleccionar")
                }
            }

            positiveButton(text = "Guardar") {
                // Actualizar la opción
                opciones[0] = RuletaOpcion(
                    texto = nombreInput.text.toString(),
                    colorFondo = colorInput.text.toString(),
                    probabilidad = probabilidadInput.text.toString().toFloatOrNull() ?: 0f,
                    tamañoTexto = 40f
                )
                ruletaView.setOpciones(opciones)
                Toast.makeText(this@RuletaActivity, "Opción actualizada", Toast.LENGTH_SHORT).show()
            }
            negativeButton(text = "Cancelar")
        }
    }
}