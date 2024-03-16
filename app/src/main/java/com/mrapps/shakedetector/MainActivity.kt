package com.mrapps.shakedetector

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mrapps.shakedetector.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        const val PREFS_NAME: String = "ShakeSetting"
        const val ACTION_SHAKE_DETECTOR = "ACTION_SHAKE_DETECTOR"
    }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        toolShake()


    }

    private fun toolShake() {
        val settings = getSharedPreferences(PREFS_NAME, 0)
        val shakeEnabled = settings.getBoolean("shakeEnabled", false)
        binding.shakeSwitch.isChecked = shakeEnabled
        binding.shakeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = settings.edit()
            editor.putBoolean("shakeEnabled", isChecked)
            editor.apply()
            val intent = Intent(ACTION_SHAKE_DETECTOR)
            intent.putExtra("enabled", isChecked)
            sendBroadcast(intent)
        }
    }
}