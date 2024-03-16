package com.mrapps.shakedetector

import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast

class MainApp : Application() {

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometerSensor: Sensor
    private lateinit var shakeDetector: ShakeDetector
    private lateinit var shakeDetectorReceiver: BroadcastReceiver


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!

        val settings: SharedPreferences = getSharedPreferences(MainActivity.PREFS_NAME, 0)
        val shakeEnabled = settings.getBoolean("shakeEnabled", false)

        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        shakeDetector = ShakeDetector(object : ShakeDetector.OnShakeListener {
            override fun onShake() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            200,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    vibrator.vibrate(200)
                }
                // Detected shake, do anything you want

                Toast.makeText(this@MainApp, "Shake Detected", Toast.LENGTH_SHORT).show()


            }
        })

        if (shakeEnabled) {
            sensorManager.registerListener(
                shakeDetector,
                accelerometerSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        shakeDetectorReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val enabled = intent.getBooleanExtra("enabled", false)
                if (enabled) {
                    sensorManager.registerListener(
                        shakeDetector,
                        accelerometerSensor,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )
                } else {
                    sensorManager.unregisterListener(shakeDetector)
                }
            }
        }

        registerReceiver(
            shakeDetectorReceiver,
            IntentFilter(MainActivity.ACTION_SHAKE_DETECTOR)
        )
    }

    override fun onTerminate() {
        unregisterReceiver(shakeDetectorReceiver)
        sensorManager.unregisterListener(shakeDetector)
        super.onTerminate()
    }


}
