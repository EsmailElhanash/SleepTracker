package com.example.sleeptracker.background

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.amplifyframework.core.Amplify
import com.example.sleeptracker.models.SleepPeriod
import kotlinx.coroutines.*
import kotlin.math.sqrt

class MySensorsManager(private val sleepPeriod: SleepPeriod) : SensorEventListener {
    private var magnitudePrevious = 0.0
    private var currentAccelerometerReading: Double = 0.0
    private var mSensorManager: SensorManager? = null
    private var accelerometerSensor: Sensor? = null

    fun startSensors(context: Context) {
        mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (mSensorManager != null) {
            registerSensors()
        }
    }


    override fun onSensorChanged(sensorEvent: SensorEvent) {
        if (sensorEvent.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val xAcceleration = sensorEvent.values[0]
            val yAcceleration = sensorEvent.values[1]
            val zAcceleration = sensorEvent.values[2]
            val magnitude =
                sqrt((xAcceleration * xAcceleration + yAcceleration * yAcceleration + zAcceleration * zAcceleration).toDouble())
            val magnitudeDelta = magnitude - magnitudePrevious
            magnitudePrevious = magnitude

            if (magnitudeDelta > 1.0) {
                currentAccelerometerReading = magnitudeDelta
                val uid = Amplify.Auth.currentUser?.userId
                if (uid != null) {
                    sleepPeriod.saveAccelerometerReading(
                        currentAccelerometerReading
                    )
                    sleepPeriod.saveStepTime()
                }
                CoroutineScope(Dispatchers.Main).launch {
                    pauseAccelerometerSensor()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun registerSensors() {
        registerAccelerometerSensorListener()
    }


    fun unRegisterSensors(appContext: Context) {
        try {
            unRegisterAccelerometerSensor(appContext)
        } catch (e: Exception) { }
    }

    private fun unRegisterAccelerometerSensor(context: Context? = null) {
        if (mSensorManager != null)
            mSensorManager!!.unregisterListener(this, accelerometerSensor)
        else {
            mSensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            mSensorManager?.unregisterListener(this, accelerometerSensor)
        }
    }

    private suspend fun pauseAccelerometerSensor() {
        withContext(Dispatchers.IO) {
            unRegisterAccelerometerSensor()
            delay(200)
            registerAccelerometerSensorListener()
        }

    }

    private fun registerAccelerometerSensorListener() {
        if (mSensorManager != null) {
            accelerometerSensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            mSensorManager!!.registerListener(
                this,
                accelerometerSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

}