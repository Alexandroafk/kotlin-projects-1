package com.wuaha.ktl_p1.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.wuaha.ktl_p1.R
import com.wuaha.ktl_p1.ui.ruleta.RuletaActivity
import com.wuaha.ktl_p1.ui.timer.TimerActivity

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonActivity1 = findViewById<Button>(R.id.go_ruleta_button)
        val buttonActivity2 = findViewById<Button>(R.id.go_timer_button)
        val buttonActivity3 = findViewById<Button>(R.id.locationTv)
        val buttonActivity4 = findViewById<Button>(R.id.buttonActivity4)


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        buttonActivity1.setOnClickListener {
            val intent = Intent(this, RuletaActivity::class.java)
            startActivity(intent)
        }

        buttonActivity2.setOnClickListener {
            val intent = Intent(this, TimerActivity::class.java)
            startActivity(intent)
        }

        buttonActivity3.setOnClickListener {
            openGoogleMaps()
        }
    }

    private fun openGoogleMaps() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val gmmIntentUri = Uri.parse("geo:${location.latitude},${location.longitude}?q=${location.latitude},${location.longitude}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")

                try {
                    startActivity(mapIntent)
                } catch (e: Exception) {
                    showToast("Google Maps no está disponible en este dispositivo")
                }
            } else {
                showToast("No se pudo obtener la ubicación actual")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
