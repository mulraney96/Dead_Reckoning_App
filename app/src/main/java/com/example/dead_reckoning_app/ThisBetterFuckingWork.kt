package com.example.dead_reckoning_app

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_dead_reckoning.*
import kotlinx.android.synthetic.main.activity_this_better_fucking_work.*
import java.io.FileOutputStream

private lateinit var sensorManager: SensorManager
var counter: Int = 0
var accelerationValues: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
var oldVelocityValues: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
var newVelocityValues: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
var distanceValues: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
var positionValues: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
var dT: Float = 0.0f
var timestamp: Long = 0L

var xAcc = arrayListOf<Float>()
var yAcc = arrayListOf<Float>()
var zAcc = arrayListOf<Float>()

var xVel = arrayListOf<Float>()
var yVel = arrayListOf<Float>()
var zVel = arrayListOf<Float>()

var xDist = arrayListOf<Float>()
var yDist = arrayListOf<Float>()
var zDist = arrayListOf<Float>()

var xPos = arrayListOf<Float>()
var yPos = arrayListOf<Float>()
var zPos = arrayListOf<Float>()

var xAxis = 0.0f
var dtList = arrayListOf<Float>()

var release: Boolean = false

class ThisBetterFuckingWork : AppCompatActivity(), SensorEventListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_this_better_fucking_work)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume(){
        super.onResume()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event!!.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION){
            if(counter>100){
                accelerationText.text = "Release Now"

                dT = ((event.timestamp - timestamp) * (1.0f / 1000000000.0f))
                xAxis += dT
                dtList.add(xAxis)

                for (i in 0..2){
                    var temp: Float = OffsetValues.getXOffset()
                    if(i==1)
                        temp = OffsetValues.getYOffset()
                    if (i==2)
                        temp = OffsetValues.getZOffset()
                    accelerationValues[i] = event.values[i]- temp
                }
                for(i in 0..2){
                    if(Math.abs(accelerationValues[i])<=0.7){
                        accelerationValues[i] = 0.0f
                    }
                }

                xAcc.add(accelerationValues[0])
                yAcc.add(accelerationValues[1])
                zAcc.add(accelerationValues[2])

                Log.i("Acceleration Values", "${accelerationValues[0]}, ${accelerationValues[1]}, ${accelerationValues[2]}")

                Log.i("Old Velocity Values", "${oldVelocityValues[0]}, ${oldVelocityValues[1]}, ${oldVelocityValues[2]}")

                for(i in 0..2){
                    newVelocityValues[i] = oldVelocityValues[i] + (accelerationValues[i]*dT)
                }
                Log.i("New Velocity Values", "${newVelocityValues[0]}, ${newVelocityValues[1]}, ${newVelocityValues[2]}")
                xVel.add(newVelocityValues[0])
                yVel.add(newVelocityValues[1])
                zVel.add(newVelocityValues[2])

                for(i in 0..2){
                    distanceValues[i] = ((newVelocityValues[i]- oldVelocityValues[i])/2)* dT
                }
                xDist.add(distanceValues[0])
                yDist.add(distanceValues[1])
                zDist.add(distanceValues[2])
                Log.i("Distance Values", "${distanceValues[0]}, ${distanceValues[1]}, ${distanceValues[2]}")

                for(i in 0..2){
                    positionValues[i] += distanceValues[i]
                }
                xPos.add(positionValues[0])
                yPos.add(positionValues[1])
                zPos.add(positionValues[2])
                Log.i("Position", "${positionValues[0]}, ${positionValues[1]}, ${positionValues[2]}")


                for(i in 0..2){
                    oldVelocityValues[i] = newVelocityValues[i]
                }
            }
            counter++
            timestamp = event.timestamp
        }
    }

    fun saveList(view: View){

        val FILENAME = "thresh.csv"
        var entry = ""
        var size = xAcc.size-1
        for(i in 0..size){
            entry = entry.plus("${dtList[i]},${zAcc[i]},${zVel[i]},${zDist[i]},${zPos[i]}\n")
        }
        try{
            var out: FileOutputStream = openFileOutput(FILENAME, Context.MODE_APPEND)
            out.write(entry.toByteArray())

            Toast.makeText(this, "Saved to $filesDir/$FILENAME", Toast.LENGTH_LONG).show()
            out.close()
        }
        catch(e: Exception){
            e.printStackTrace()
        }

        sensorManager.unregisterListener(this)
    }

}
