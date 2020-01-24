package com.example.dead_reckoning_app

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileWriter



class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    //private var accelerometer: Sensor? = null
    //var textView: TextView? = null
    var X: Float = 0.0f
    var Y: Float = 0.0f
    var Z: Float = 0.0f

    var dT: Double = 0.0
    var gravity: DoubleArray = doubleArrayOf(0.0,0.0,9.8)
    var linearAcceleration: DoubleArray = doubleArrayOf(0.0,0.0,0.0)
    var velocity: DoubleArray = doubleArrayOf(0.0,0.0,0.0)
    var distance: DoubleArray = doubleArrayOf(0.0,0.0,0.0)


    var timestamp: Long = 0;
    val nano2second: Double = 1.0 / 1000000000.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
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
        val alpha: Float = 0.42f
    if((event!!.values[0]<0.072 || event.values[0]>0.066) &&
        (event.values[1]<0.12 || event.values[1]>0.108) &&
        (event.values[2]<9.79 || event.values[2]>9.784))

        gravity[0] = alpha * gravity[0] + (1 - alpha) * event!!.values[0]
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event!!.values[1]
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event!!.values[1]
        Log.i("reading:", "${event.values[0]}, ${event.values[1]}, ${event.values[2]}")
        //Log.i("gravity:","${gravity[0]}, ${gravity[1]}, ${gravity[2]}")

        linearAcceleration[0] = event.values[0] - gravity[0]
        linearAcceleration[1] = event.values[1] - gravity[1]
        linearAcceleration[2] = event.values[2] - gravity[2]

        Log.i("linear acceleration:","${linearAcceleration[0]}, ${linearAcceleration[1]}, ${linearAcceleration[2]}")

        if (timestamp != 0L) {
             dT = (event.timestamp - timestamp) * nano2second

            //Log.i("dt:","${dT}")

            velocity[0] = getVelocity(velocity[0], linearAcceleration[0], dT)
            velocity[1] = getVelocity(velocity[1], linearAcceleration[1], dT)
            velocity[2] = getVelocity(velocity[2], linearAcceleration[2], dT)

            //Log.i("velocity:","${velocity[0]}, ${velocity[1]}, ${velocity[2]}")

            distance[0] = getDistance(velocity[0], dT)
            distance[1] = getDistance(velocity[1], dT)
            distance[2] = getDistance(velocity[2], dT)

            Log.i("distance:", "${distance[0]}, ${distance[1]}, ${distance[2]}")

            if( (linearAcceleration[0]<0.15 && linearAcceleration[0]>-0.15) &&
                (linearAcceleration[1]<0.15 && linearAcceleration[1]>-0.15) &&
                (linearAcceleration[2]<0.15 && linearAcceleration[2]>-0.15) ) {

                // do nothing
            }
            else{

                X += distance[0].toFloat()
                Y += distance[1].toFloat()
                Z += distance[2].toFloat()
                Log.i("update", "The distance has been updated")

            }

            Log.i("Co-ordinates:", "X:${X}, Y:${Y}, Z:${Z}")

            accelerometer_data.text = "coordinates:\n\nX = ${X}\n\n" +
                    "Y = ${Y}\n\nZ = ${Z}" //reference sensorEvent android developer


            distanceView.text = "distance calculated:\n\nX = ${distance[0]}\n\n" +
                    "Y = ${distance[1]}\n\nZ = ${distance[2]}"

            coordinateText.text = "velocity:\n\nX = ${velocity[0]}\n\n" +
                    "Y = ${velocity[1]}\n\nZ = ${velocity[2]}"

            linAcc.text = "linear Acceleration:\n\nX = ${linearAcceleration[0]}\n\n" +
                    "Y = ${linearAcceleration[1]}\n\nZ = ${linearAcceleration[2]}"

            accdata.text = "Acceleration Reading:\n\nX = ${event.values[0]}\n\n" +
                    "Y = ${event.values[1]}\n\nZ = ${event.values[2]}"

            tddata.text = "dT value: ${dT}"

        }


        timestamp = event.timestamp

    }

    fun getVelocity(vOld: Double, acc: Double, dT: Double):Double {
        var vNew = vOld + (acc*dT)
        return vNew
    }

    fun getDistance(V: Double, dT: Double):Double = V*dT

}

