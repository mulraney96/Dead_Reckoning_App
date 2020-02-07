package com.example.dead_reckoning_app

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_sensor.*

private lateinit var sensorManager: SensorManager

class SensorActivity : AppCompatActivity(), SensorEventListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!=null){
            Log.i("magnetometer", "present")
        }
        else{
            Log.i("magnetometer", "not present")
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!=null){
            Log.i("gyro", "present")
        }
        else{
            Log.i("gyro", "not present")
        }


    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event!!.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            textView.text = "Gyro\n\nX= ${event.values[0]}\n\nY = ${event.values[1]}\n\nZ = ${event.values[2]}"
            Log.i("gyro", "X= ${event.values[0]}\\n\\nY = ${event.values[1]}\\n\\nZ = ${event.values[2]}")
        }

        if(event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR){
            textView2.text = "MF\n\nX = ${event.values[0]}\n\nY = ${event.values[1]}\n\nZ = ${event.values[2]}"
            Log.i("mf","X= ${event.values[0]}\\n\\nY = ${event.values[1]}\\n\\nZ = ${event.values[2]}" )
        }
    }
}
