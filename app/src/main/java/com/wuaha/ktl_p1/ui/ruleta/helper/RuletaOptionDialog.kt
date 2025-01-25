package com.wuaha.ktl_p1.ui.ruleta.helper

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.text.Editable
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.colorChooser
import com.afollestad.materialdialogs.customview.customView
import com.wuaha.ktl_p1.R
import com.wuaha.ktl_p1.ui.ruleta.data.RuletaOpcion
import com.wuaha.ktl_p1.ui.ruleta.views.RuletaView

class RuletaOptionDialog(
    private val context: Context,
    private val currentOption: RuletaOpcion,
    private val allOptions: MutableList<RuletaOpcion>,
    private val ruletaView: RuletaView,
    private val title: String,
    private val isCreateMode: Boolean,
    private val onUpdate: () -> Unit,
    private val isAutoContrastTextColorEnabled: Boolean = true
) {
    private lateinit var nombreInput: EditText
    private lateinit var colorInput: EditText
    private lateinit var probabilidadInput: EditText
    private lateinit var btnColor: Button
    private var contrasteOpcionColorTexto: String = currentOption.colorTexto
    private lateinit var tvSobrante: TextView

    //private lateinit var currentOption: RuletaOpcion
    private lateinit var otrasOpciones: List<RuletaOpcion>
    private var sumaOtras: Double = 0.0
    private var maxPermitido: Float = 0f
    private var porcentajeSobrante: Float = 0f

    fun show() {
        // currentOption = allOptions[currentOptionIndex]
        otrasOpciones = allOptions.filter { it != currentOption && it.habilitada }
        sumaOtras = otrasOpciones.sumOf { it.probabilidad?.toDouble() ?: 0.0 }
        maxPermitido = (100.0 - sumaOtras).toFloat()
        porcentajeSobrante = 100f - (currentOption.probabilidad ?: 0f) - sumaOtras.toFloat()

        MaterialDialog(context).show {
            title(text = title)
            customView(R.layout.dialog_edit_options, scrollable = true)

            nombreInput = view.findViewById(R.id.edit_option_name)
            colorInput = view.findViewById(R.id.edit_option_color)
            probabilidadInput = view.findViewById(R.id.edit_option_probabilidad)
            btnColor = view.findViewById(R.id.btn_select_color)
            tvSobrante = view.findViewById(R.id.tv_porcentaje_sobrante)

            setupInitialValues()
            setupProbabilityInput()
            setupColorPicker()
            actualizarUI()

            if (!isCreateMode) {
                neutralButton(
                    text = if (currentOption.habilitada) "Deshabilitar" else "Habilitar"
                ) {
                    val originalState = currentOption.habilitada
                    currentOption.habilitada = !originalState
                    if (!saveChanges()) {
                        currentOption.habilitada = originalState // Revertir si falla
                    }
                }
            }

            positiveButton(text = "Guardar") {
                if (saveChanges()) {
                    dismiss()
                }
            }
            negativeButton(text = "Cancelar") {
                if (isCreateMode) allOptions.remove(currentOption)
                dismiss()
            }
        }
    }

    private fun setupInitialValues() {
        nombreInput.setText(currentOption.texto)
        colorInput.setText(currentOption.colorFondo.ifEmpty { "#000000" })
        probabilidadInput.setText(currentOption.probabilidad?.toString() ?: maxPermitido.toString())

        try {
            val initialColor = Color.parseColor(currentOption.colorFondo)
            btnColor.apply {
                text = currentOption.colorFondo.uppercase()
                backgroundTintList = ColorStateList.valueOf(initialColor)
            }
        } catch (e: Exception) {
            btnColor.apply {
                text = "#000000"
                backgroundTintList = ColorStateList.valueOf(Color.BLACK)
            }
        }
    }

    private fun setupProbabilityInput() {
        probabilidadInput.filters = arrayOf(
            InputFilter { source, start, end, dest, dstart, dend ->
                val nuevoTexto = dest.toString().replaceRange(dstart, dend, source.subSequence(start, end).toString())
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

        probabilidadInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val nuevoValor = s?.toString()?.toFloatOrNull() ?: 0f
                actualizarUI(nuevoValor)
                probabilidadInput.error = if (nuevoValor > maxPermitido || porcentajeSobrante < 0) {
                    "Máximo permitido: ${"%.2f".format(maxPermitido)}%"
                } else {
                    null
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupColorPicker() {
        btnColor.setOnClickListener {
            val currentColor = try {
                Color.parseColor(colorInput.text.toString())
            } catch (e: Exception) {
                Color.BLACK
            }

            MaterialDialog(context).show {
                title(text = "Seleccionar color")
                colorChooser(
                    colors = intArrayOf(
                        Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
                        Color.CYAN, Color.MAGENTA, Color.BLACK,
                        Color.parseColor("#FFA500"), Color.parseColor("#808080")
                    ),
                    initialSelection = currentColor,
                    allowCustomArgb = true
                ) { _, color ->
                    val hexColor = String.format("#%06X", 0xFFFFFF and color)
                    colorInput.setText(hexColor)
                    btnColor.apply {
                        text = hexColor.uppercase()
                        backgroundTintList = ColorStateList.valueOf(color)
                    }
                    if (isAutoContrastTextColorEnabled) {
                        contrasteOpcionColorTexto = getContrastColor(color)
                    }
                }
                positiveButton(text = "Seleccionar")
            }
        }
    }

    private fun getContrastColor(color: Int): String {
        val luminance = 0.299 * Color.red(color) +
                0.587 * Color.green(color) +
                0.114 * Color.blue(color)
        return if (luminance > 186) "#000000" else "#ffffff"
    }

    private fun actualizarUI(nuevoValor: Float? = null) {
        val valorActual = nuevoValor ?: probabilidadInput.text.toString().toFloatOrNull() ?: 0f
        porcentajeSobrante = 100f - valorActual - sumaOtras.toFloat()

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

    private fun saveChanges(): Boolean {
        // 1. Validaciones iniciales
        if (probabilidadInput.error != null || porcentajeSobrante < 0) {
            Toast.makeText(context, "Ajusta la probabilidad antes de guardar", Toast.LENGTH_LONG).show()
            return false
        }

        // 2. Validaciones de color
        val colorHex = colorInput.text.toString().takeIf { it.isNotEmpty() } ?: "#000000"
        if (!colorHex.matches(Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$"))) {
            Toast.makeText(context, "Color inválido", Toast.LENGTH_LONG).show()
            return false
        }

        // 3. Validaciones de probabilidad
        val probabilidadTexto = probabilidadInput.text.toString()
        val nuevaProbabilidad = probabilidadTexto.replace(',', '.').toFloatOrNull() ?: 0f
        if (nuevaProbabilidad < 0f || nuevaProbabilidad > 100f) {
            Toast.makeText(context, "Probabilidad inválida", Toast.LENGTH_LONG).show()
            return false
        }

        // 4. Aplicar cambios
        currentOption.apply {
            texto = nombreInput.text.toString()
            colorFondo = colorHex
            probabilidad = nuevaProbabilidad
            colorTexto = contrasteOpcionColorTexto
        }

        if (isCreateMode) {
            allOptions.add(currentOption)
        }

        // 5. Validación final de probabilidades
        if (!RuletaOpcion.validarProbabilidades(allOptions.filter { it.habilitada })) {
            if (isCreateMode) allOptions.remove(currentOption)
            Toast.makeText(context, "Error: Suma de probabilidades excede 100%", Toast.LENGTH_LONG).show()
            return false
        }

        // 6. Actualizar y notificar
        if (isCreateMode) allOptions.add(currentOption)
        ruletaView.setOpciones(allOptions.filter { it.habilitada })
        onUpdate()
        return true
    }
}