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
var file: String = ""

var xErrorBias = 0.0001467f
var yErrorBias = 0.00036689f
var zErrorBias = -0.0000013218f

var xDrift: Float = 0.0f
var yDrift: Float = 0.0f
var zDrift: Float = 0.0f


class ThisBetterFuckingWork : AppCompatActivity(), SensorEventListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_this_better_fucking_work)
        file = OffsetValues.getFileName()
        Toast.makeText(this, "Saving to $file", Toast.LENGTH_SHORT).show()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume(){
        super.onResume()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
            SensorManager.SENSOR_DELAY_UI
        )
        positionText.text = "X = 0\nY = 0\nZ = 0"
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        release = false
        counter = 0
        oldVelocityValues = floatArrayOf(0.0f, 0.0f, 0.0f)
        accelerationValues = floatArrayOf(0.0f, 0.0f, 0.0f)
        newVelocityValues = floatArrayOf(0.0f, 0.0f, 0.0f)
        distanceValues = floatArrayOf(0.0f, 0.0f, 0.0f)
        positionValues = floatArrayOf(0.0f, 0.0f, 0.0f)
        timestamp = 0L

        xAcc.clear()
        yAcc.clear()
        zAcc.clear()

        dT = 0.0f
        dtList.clear()
        xAxis = 0.0f

        xVel.clear()
        yVel.clear()
        zVel.clear()

        xDist.clear()
        yDist.clear()
        zDist.clear()

        xPos.clear()
        yPos.clear()
        zPos.clear()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event!!.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION){
            if(counter>100) {
                test_Text.text = "Release Now"
                if (release) {

                    dT = ((event.timestamp - timestamp) * (1.0f / 1000000000.0f))
                    Log.i("dT", "$dT")
                    xAxis += dT

                    xDrift = xErrorBias * (xAxis* xAxis)/2
                    yDrift = yErrorBias * (xAxis* xAxis)/2
                    zDrift = zErrorBias * (xAxis* xAxis)/2

                    dtList.add(xAxis)


                        accelerationValues[0] = event.values[0]
                        accelerationValues[1] = event.values[1]
                        accelerationValues[2] = event.values[2]

                    /*for (i in 0..2) {
                        var temp: Float = OffsetValues.getXOffset()
                        if (i == 1)
                            temp = OffsetValues.getYOffset()
                        if (i == 2)
                            temp = OffsetValues.getZOffset()
                        accelerationValues[i] = event.values[i] - temp
                    }*/
                    for(i in 0..2){
                        if(Math.abs(accelerationValues[i])<=0.1){
                            accelerationValues[i] = 0.0f
                        }
                    }
//C:\Users\Joseph\Documents\College Work\4th year\FYP\Matlab_Work\PositionFiles\finalPosTests
                    xAcc.add(accelerationValues[0])
                    yAcc.add(accelerationValues[1])
                    zAcc.add(accelerationValues[2])

//                    Log.i(
//                        "Acceleration Values",
//                        "${accelerationValues[0]}, ${accelerationValues[1]}, ${accelerationValues[2]}"
//                    )

//                    Log.i(
//                        "Old Velocity Values",
//                        "${oldVelocityValues[0]}, ${oldVelocityValues[1]}, ${oldVelocityValues[2]}"
//                    )

                    for (i in 0..2) {
                        if(Math.abs(accelerationValues[i])<0.1){
                            newVelocityValues[i] = 0.0f
                        }
                        else
                            newVelocityValues[i] = oldVelocityValues[i] + (accelerationValues[i] * dT)
                    }
                    for(i in 0..2){
                        if((newVelocityValues[i] - oldVelocityValues[i]) < 0.05)
                            newVelocityValues[i] = 0.0f
                    }
//                    Log.i(
//                        "New Velocity Values",
//                        "${newVelocityValues[0]}, ${newVelocityValues[1]}, ${newVelocityValues[2]}"
//                    )
                    xVel.add(newVelocityValues[0])
                    yVel.add(newVelocityValues[1])
                    zVel.add(newVelocityValues[2])

                    /*for (i in 0..2) {
                        if ((oldVelocityValues[i] - newVelocityValues[i]) < 0.009) {
                            newVelocityValues[i] = 0.0f
                        }
                    }*/

                    for (i in 0..2) {
                        distanceValues[i] = (newVelocityValues[i] * dT)
                    }
                    xDist.add(distanceValues[0])
                    yDist.add(distanceValues[1])
                    zDist.add(distanceValues[2])
//                    Log.i(
//                        "Distance Values",
//                        "${distanceValues[0]}, ${distanceValues[1]}, ${distanceValues[2]}"
//                    )

                    for (i in 0..2) {
                        positionValues[i] += (distanceValues[i])
                    }
                    xPos.add(positionValues[0])
                    yPos.add(positionValues[1])
                    zPos.add(positionValues[2])
                    Log.i(
                        "Position",
                        "${positionValues[0]}, ${positionValues[1]}, ${positionValues[2]}"
                    )
                    positionText.text = "X = ${positionValues[0]}\nY = ${positionValues[1]}\nZ = ${positionValues[2]}"


                    for (i in 0..2) {
                        oldVelocityValues[i] = newVelocityValues[i]
                    }
                }
            }
            counter++
            timestamp = event.timestamp
        }
    }


    override fun onTouchEvent(e: MotionEvent): Boolean {

        when (e.action) {
            MotionEvent.ACTION_UP -> {
                release = true
                Log.i("Release", "$release")
            }
            MotionEvent.ACTION_DOWN -> {
                test_Text.text = "Not Ready"
            }
        }

        return true
    }

    fun saveList(view: View){
        sensorManager.unregisterListener(this)

        var FileName: String  = OffsetValues.getFileName()
        var xFileName: String  = "x".plus(FileName)
        var yFileName: String  = "y".plus(FileName)
        var zFileName: String  = "z".plus(FileName)


        var Xentry = "0,0,0,0,0\n"
        var Yentry = "0,0,0,0,0\n"
        var Zentry = "0,0,0,0,0\n"

        var size = zAcc.size-1
        for(i in 0..size){
            Xentry = Xentry.plus("${dtList[i]},${xAcc[i]},${xVel[i]},${xDist[i]},${xPos[i]}\n")
            Yentry = Yentry.plus("${dtList[i]},${yAcc[i]},${yVel[i]},${yDist[i]},${yPos[i]}\n")
            Zentry = Zentry.plus("${dtList[i]},${zAcc[i]},${zVel[i]},${zDist[i]},${zPos[i]}\n")
        }
        try{
            var out: FileOutputStream = openFileOutput(zFileName, Context.MODE_APPEND)
            out.write(Zentry.toByteArray())
            Toast.makeText(this, "Saved to $filesDir/$zFileName", Toast.LENGTH_SHORT).show()
            out.close()

            var outX: FileOutputStream = openFileOutput(xFileName, Context.MODE_APPEND)
            outX.write(Xentry.toByteArray())
            Toast.makeText(this, "Saved to $filesDir/$xFileName", Toast.LENGTH_SHORT).show()
            outX.close()

            var outY: FileOutputStream = openFileOutput(yFileName, Context.MODE_APPEND)
            outY.write(Yentry.toByteArray())
            Toast.makeText(this, "Saved to $filesDir/$yFileName", Toast.LENGTH_SHORT).show()
            outY.close()
        }
        catch(e: Exception){
            e.printStackTrace()
        }

    }

}
