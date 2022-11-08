package com.dtse.cjra.hmsgeocoding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        geocodingButton.setOnClickListener {
            goToGeocodingActivity()
        }

        reverseGeocodingButton.setOnClickListener {
            goToReverseGeocodingActivity()
        }
    }

    private fun goToGeocodingActivity() {
        startActivity(Intent(this, GeocodingActivity::class.java))
    }

    private fun goToReverseGeocodingActivity() {
        startActivity(Intent(this, ReverseGeocodingActivity::class.java))
    }
}