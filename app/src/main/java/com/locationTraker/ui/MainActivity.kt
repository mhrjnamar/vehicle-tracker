package com.locationTraker.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.locationTraker.LocationUtils
import com.locationTraker.R
import com.locationTraker.preference.AppPreferences
import com.locationTraker.service.LocationNotifyService
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppPreferences.setSharedPreferences(applicationContext)

        val database = FirebaseDatabase.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val myRef = database.getReference("users/$userId")

        val locationUtils =
            LocationUtils(this@MainActivity, object : LocationUtils.OnLocationUpdateListener {
                override fun onUpdate(latitude: Double, longitude: Double) {
                    // Write a message to the database
                    val latLng: MutableMap<String, String> = HashMap()
                    latLng["lat"] = latitude.toString()
                    latLng["lng"] = longitude.toString()

                    myRef.setValue(latLng)

                }
            })
        locationUtils.getLastLocation()

        btnStart.setOnClickListener {

            AlertDialog.Builder(this@MainActivity)
                .setMessage("Location tracking started!")
                .setIcon(R.drawable.ic_alert)
                .setPositiveButton(
                    "OK"
                ) { _, _ ->
                    ContextCompat.startForegroundService(
                        this@MainActivity,
                        Intent(this@MainActivity, LocationNotifyService::class.java)
                    )
                }.create().show()
        }

        btnStop.setOnClickListener {
            AlertDialog.Builder(this@MainActivity)
                .setMessage("Location tracker will be stopped now")
                .setIcon(R.drawable.ic_alert)
                .setPositiveButton(
                    "OK"
                ) { _, _ ->
                    stopService(intent)
                }.create().show()
        }

        btnViewMap.setOnClickListener {
            AlertDialog.Builder(this@MainActivity)
                .setMessage("View Location on Map")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(
                    "OK"
                ) { _, _ ->
                    startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                }.create().show()
        }

    }


}
