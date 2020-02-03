package com.example.dead_reckoning_app

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class OffsetActivity : AppCompatActivity() , SensorEventListener {

    private var xAccelerationValues = arrayListOf<Float>()
    private var yAccelerationValues = arrayListOf<Float>()
    private var zAccelerationValues = arrayListOf<Float>()

    var accelerationOffset: Boolean = false

    private lateinit var sensorManager: SensorManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offset)

        xAccelerationValues.clear()
        yAccelerationValues.clear()
        zAccelerationValues.clear()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        var rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val linearAccSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
            SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if(xAccelerationValues!!.size <= 50){
            xAccelerationValues.add(event.values[0])
            yAccelerationValues.add(event.values[1])
            zAccelerationValues.add(event.values[2])
            Log.i("accelerometer readings" , "${event.values[0]},${event.values[1]},${event.values[2]} ")
        }
        else{
            accelerationOffset = true
            OffsetValues.setPositionalOffsetValues(calculateOffset(xAccelerationValues),calculateOffset(yAccelerationValues),
                calculateOffset(zAccelerationValues))
            Log.i("Acceleration offset", "${OffsetValues.getXOffset()}, ${OffsetValues.getYOffset()}, ${OffsetValues.getZOffset()}")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun calculateOffset(list: ArrayList<Float>): Float{
        var temp = 0.0f
        for(i in 0..list.lastIndex){
            temp += list[i]
        }
        return temp/list.size
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //To change body of created functions use File | Settings | File Templates.
    }

}
