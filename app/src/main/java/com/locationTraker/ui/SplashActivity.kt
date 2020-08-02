package com.locationTraker.ui

import android.app.AppComponentFactory
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.locationTraker.R
import com.locationTraker.preference.AppPreferences
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    companion object{
        val TAG = "SplashActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        theme.applyStyle(R.style.BaseTheme_FullScreen,true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splashImage.postDelayed(Runnable { kotlin.run {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        } },2000)

    }

}
