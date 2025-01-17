package com.wuaha.ktl_p1.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.wuaha.ktl_p1.R
import com.wuaha.ktl_p1.ui.ruleta.RuletaActivity
import com.wuaha.ktl_p1.ui.timer.TimerActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonActivity1 = findViewById<Button>(R.id.go_ruleta_button)
        val buttonActivity2 = findViewById<Button>(R.id.go_timer_button)
        val buttonActivity3 = findViewById<Button>(R.id.buttonActivity3)
        val buttonActivity4 = findViewById<Button>(R.id.buttonActivity4)

        buttonActivity1.setOnClickListener {
            val intent = Intent(this, RuletaActivity::class.java)
            startActivity(intent)
        }

        buttonActivity2.setOnClickListener {
            val intent = Intent(this, TimerActivity::class.java)
            startActivity(intent)
        }

//        buttonActivity3.setOnClickListener {
//            val intent = Intent(this, Activity3::class.java)
//            startActivity(intent)
//        }

//        buttonActivity4.setOnClickListener {
//            val intent = Intent(this, Activity4::class.java)
//            startActivity(intent)
//        }
    }
}
