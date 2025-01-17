package com.wuaha.ktl_p1.ui.timer

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.wuaha.ktl_p1.R
import com.wuaha.ktl_p1.ui.timer.views.TimerView

class TimerActivity : AppCompatActivity() {

    private lateinit var timerView: TimerView
    private lateinit var hoursInput: EditText
    private lateinit var minutesInput: EditText
    private lateinit var increaseMinutesButton: Button
    private lateinit var increaseHoursButton: Button
    private lateinit var toggleShowTextButton: Button
    private lateinit var changeColorButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        timerView = findViewById(R.id.timer_view_xml)
        hoursInput = findViewById(R.id.hours_input)
        minutesInput = findViewById(R.id.minutes_input)
        increaseMinutesButton = findViewById(R.id.increase_minutes_button)
        increaseHoursButton = findViewById(R.id.increase_hours_button)
        toggleShowTextButton = findViewById(R.id.toggle_show_text_button)
        changeColorButton = findViewById(R.id.change_color_button)

        setupButtons()
    }

    private fun setupButtons() {
        // Botón para actualizar la hora
        findViewById<Button>(R.id.update_time_button).setOnClickListener {
            val newHours = hoursInput.text.toString().toIntOrNull() ?: 0
            val newMinutes = minutesInput.text.toString().toIntOrNull() ?: 0
            timerView.updateTime(newHours, newMinutes)
        }

        // Botón para aumentar minutos
        increaseMinutesButton.setOnClickListener {
            timerView.addMinutes(15)
        }

        // Botón para aumentar horas
        increaseHoursButton.setOnClickListener {
            timerView.addHours(1)
        }

        // Botón para mostrar/Ocultar texto adjunto
        toggleShowTextButton.setOnClickListener {
            timerView.toggleShowText()
        }

        // Botón para cambiar colores
        changeColorButton.setOnClickListener {
            // Cambiar los colores al azar, puedes ajustar a colores predeterminados
            val randomBorderColor = (0xFF000000.toInt()..0xFFFFFF00.toInt()).random()
            val randomBackgroundColor = (0xFF000000.toInt()..0xFFFFFF00.toInt()).random()
            val randomTextColor = (0xFF000000.toInt()..0xFFFFFF00.toInt()).random()
            timerView.setColors(randomBorderColor, randomBackgroundColor, randomTextColor)
        }
    }
}
