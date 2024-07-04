package com.example.eauction

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.eauction.persistence.PreferencesManager

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val preferencesManager = PreferencesManager(this)

        Handler(Looper.getMainLooper()).postDelayed({
            val userData = preferencesManager.getUserData()
            val mainIntent: Intent = if (userData["email"] != null) {
                Intent(this@SplashActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashActivity, LoginActivity::class.java)
            }
            this@SplashActivity.startActivity(mainIntent)
            this@SplashActivity.finish()
        }, 2000)
    }
}
