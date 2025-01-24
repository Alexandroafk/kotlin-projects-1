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
import com.wuaha.ktl_p1.ui.ruleta.helper.EditOptionsDialog
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
    // Popup dialog configuracion
    // --------------------------
    private fun showEditOptionDialog(opcionIndex: Int) {
        val opcion = opciones[opcionIndex]
        val otrasOpciones = opciones.filter { it != opcion && it.habilitada }

        MaterialDialog(this).show {
            title(text = "Editar Opción")
            customView(R.layout.dialog_edit_options, scrollable = true)

            val nombreInput = view.findViewById<EditText>(R.id.edit_option_name)
            val colorInput = view.findViewById<EditText>(R.id.edit_option_color)
            val probabilidadInput = view.findViewById<EditText>(R.id.edit_option_probabilidad)
            val btnColor = view.findViewById<Button>(R.id.btn_select_color)
            val tvSobrante = view.findViewById<TextView>(R.id.tv_porcentaje_sobrante)
            // Calcular valores iniciales
            val sumaOtras = otrasOpciones.sumOf { it.probabilidad?.toDouble() ?: 0.0 }
            val maxPermitido = (100.0 - sumaOtras).toFloat()
            var porcentajeSobrante = 100f - (opcion.probabilidad ?: 0f) - sumaOtras.toFloat()

            // Función para actualizar la UI
            fun actualizarUI(nuevoValor: Float? = null) {
                val valorActual = nuevoValor ?: probabilidadInput.text.toString().toFloatOrNull() ?: 0f
                porcentajeSobrante = 100f - valorActual - sumaOtras.toFloat()

                // Formatear texto de sobrante
                val texto = when {
                    porcentajeSobrante < 0 -> "Excediste el 100%!"
                    else -> "Porcentaje sobrante: ${"%.2f".format(porcentajeSobrante)}%"
                }

                val color = if (porcentajeSobrante < 0) Color.RED else Color.GRAY
                val spannable = SpannableString(texto).apply {
                    if (porcentajeSobrante >= 0) {
                        setSpan(
                            StyleSpan(Typeface.BOLD),
                            texto.indexOf(":") + 2,
                            texto.length - 1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }

                tvSobrante.setTextColor(color)
                tvSobrante.text = spannable
            }

            // Configurar input filters
            probabilidadInput.filters = arrayOf<InputFilter>(
                InputFilter { source, start, end, dest, dstart, dend ->
                    val nuevoTexto = dest.toString().replaceRange(dstart, dend, source.subSequence(start, end))
                    when {
                        nuevoTexto.isEmpty() -> null
                        nuevoTexto.matches(Regex("^\\d{0,3}(\\.\\d{0,2})?$")) -> null
                        else -> ""
                    }
                },
                InputFilter { source, _, _, dest, dstart, dend ->
                    val nuevoValor = (dest.toString().replaceRange(dstart, dend, source.toString())).toFloatOrNull() ?: 0f
                    if (nuevoValor > maxPermitido) "" else null
                }
            )

            // Listener para cambios en tiempo real
            probabilidadInput.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val nuevoValor = s.toString().toFloatOrNull()
                    actualizarUI(nuevoValor)

                    // Mostrar error si excede
                    if (nuevoValor != null && (nuevoValor > maxPermitido || porcentajeSobrante < 0)) {
                        probabilidadInput.error = "Máximo permitido: ${"%.2f".format(maxPermitido)}%"
                    } else {
                        probabilidadInput.error = null
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            // Configuración inicial del botón de color
            try {
                val initialColor = Color.parseColor(opcion.colorFondo)
                btnColor.apply {
                    text = opcion.colorFondo.uppercase()
                    backgroundTintList = ColorStateList.valueOf(initialColor)
                    setTextColor(Color.BLACK)
                }
            } catch (e: Exception) {
                btnColor.apply {
                    text = "#000000"
                    backgroundTintList = ColorStateList.valueOf(Color.BLACK)
                    setTextColor(Color.BLACK)
                }
            }

            // Valores iniciales
            nombreInput.setText(opcion.texto)
            colorInput.setText(opcion.colorFondo.ifEmpty { "#000000" })
            probabilidadInput.setText(opcion.probabilidad?.toString())
            actualizarUI(opcion.probabilidad)

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
                            setTextColor(Color.BLACK)
                        }
                    }
                    positiveButton(text = "Seleccionar")
                }
            }

            positiveButton(text = "Guardar") {
                try {
                    // Validar probabilidad 2
                    if (probabilidadInput.error != null || porcentajeSobrante < 0) {
                        Toast.makeText(
                            this@RuletaActivity,
                            "Ajusta la probabilidad antes de guardar",
                            Toast.LENGTH_LONG
                        ).show()
                        return@positiveButton
                    }

                    // Validar color
                    val colorHex = colorInput.text.toString().takeIf { it.isNotEmpty() } ?: "#000000"
                    if (!colorHex.matches(Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$"))) {
                        Toast.makeText(
                            this@RuletaActivity,
                            "Color inválido. Usa formato hexadecimal (ej: #FF0000)",
                            Toast.LENGTH_LONG
                        ).show()
                        return@positiveButton
                    }

                    // Validar probabilidad
                    val probabilidadTexto = probabilidadInput.text.toString()
                    val nuevaProbabilidad = when {
                        probabilidadTexto.isBlank() -> 0f
                        probabilidadTexto.matches(Regex("^\\d+([.,]\\d+)?$")) -> {
                            probabilidadTexto.replace(',', '.').toFloat()
                        }
                        else -> {
                            Toast.makeText(
                                this@RuletaActivity,
                                "Error: Usa números con punto decimal (ej: 25.5)",
                                Toast.LENGTH_LONG
                            ).show()
                            return@positiveButton
                        }
                    }

                    if (nuevaProbabilidad < 0f || nuevaProbabilidad > 100f) {
                        Toast.makeText(
                            this@RuletaActivity,
                            "La probabilidad debe estar entre 0 y 100",
                            Toast.LENGTH_LONG
                        ).show()
                        return@positiveButton
                    }

                    // Crear y actualizar opción
                    opciones[0] = RuletaOpcion(
                        texto = nombreInput.text.toString(),
                        colorFondo = colorHex,
                        probabilidad = nuevaProbabilidad,
                    )

                    // Forzar recálculo del sobrante
                    ruletaView.setOpciones(opciones.filter { it.habilitada })

                    if (RuletaOpcion.validarProbabilidades(opciones)) {
                        ruletaView.setOpciones(opciones)
                        Toast.makeText(this@RuletaActivity, "Opción actualizada", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            this@RuletaActivity,
                            "Error: La suma total no puede superar 100%",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(
                        this@RuletaActivity,
                        "Error inesperado: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            negativeButton(text = "Cancelar")
        }
    }
}