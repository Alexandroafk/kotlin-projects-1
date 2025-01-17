package com.wuaha.ktl_p1.ui.ruleta

import android.animation.ObjectAnimator
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wuaha.ktl_p1.R
import com.wuaha.ktl_p1.ui.ruleta.views.RuletaView
import kotlin.random.Random

class RuletaActivity : AppCompatActivity() {

    private lateinit var ruletaView: RuletaView
    private lateinit var botonGirarRuleta: ImageView
    private val options = mutableListOf("Manzana", "Pera", "Uva", "Durazno", "Sandia")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruleta)

        ruletaView = findViewById(R.id.ruleta_view_xml)
        botonGirarRuleta = findViewById(R.id.boton_girar_ruleta)
        ruletaView.setOptions(options)

        botonGirarRuleta.setOnClickListener {
            val anguloRandom = Random.nextInt(360) + 720
            val animator = ObjectAnimator.ofFloat(ruletaView, "rotation", 0f, anguloRandom.toFloat())
            animator.duration = 3000
            animator.start()

            animator.addUpdateListener { animacion ->
                if (animacion.animatedFraction >= 1f) {
                    val anguloFinal = (anguloRandom % 360).toFloat()
                    val selectedIndex = ruletaView.getSelectedIndex(anguloFinal)
                    val selectedOption = options[selectedIndex]
                    Toast.makeText(this, "¡Opción seleccionada: $selectedOption!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}