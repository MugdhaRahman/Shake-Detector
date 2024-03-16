package com.mrapps.shakedetector

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import kotlin.math.abs

class ShakeDetector(private val listener: OnShakeListener) : SensorEventListener {
    private companion object {
        private const val SHAKE_THRESHOLD = 1500
        private const val SHAKE_COUNT = 2 // number of shakes needed
        private const val SHAKE_INTERVAL = 1000 // time window for detecting shakes in milliseconds
    }

    private var lastUpdateTime: Long = 0
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private var shakeCount = 0
    private var shakeResetTime: Long = 0

    override fun onSensorChanged(event: SensorEvent) {
        val sensor = event.sensor
        if (sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val currentTime = System.currentTimeMillis()
            if ((currentTime - lastUpdateTime) > 100) {
                val diffTime = (currentTime - lastUpdateTime)
                lastUpdateTime = currentTime

                val speed = abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000

                if (speed > SHAKE_THRESHOLD) {
                    if (currentTime - shakeResetTime > SHAKE_INTERVAL) {
                        shakeCount = 0
                    }

                    shakeCount++

                    if (shakeCount >= SHAKE_COUNT) {
                        listener.onShake()
                    }

                    shakeResetTime = currentTime
                }

                lastX = x
                lastY = y
                lastZ = z
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Ignored for this example
    }

    interface OnShakeListener {
        fun onShake()
    }
}
