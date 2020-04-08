package com.example.dead_reckoning_app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.view.MotionEventCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.activity_dead_reckoning.*
import java.io.FileOutputStream
import kotlin.math.pow

class DeadReckoningActivity : AppCompatActivity() , SensorEventListener {

    private lateinit var sensorManager: SensorManager

    var X: Float = 0.0f
    var Y: Float = 0.0f
    var Z: Float = 0.0f

    var velocityNew: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
    var velocityOld: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
    var distance: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
    var dT: Float = 0.0f
    var timestamp: Long = 0

    private var xAccelerationValues = arrayListOf<Entry>()
    private var xVelocityValues = arrayListOf<Entry>()
    private var xDistanceValues = arrayListOf<Entry>()
    private var xAccelerationValues2 = arrayListOf<Float>()
    private var dtList = arrayListOf<Float>()

    var xaxis = 0.0f
    var graphAcc: LineChart? = null
    var graphVel: LineChart? = null
    var graphDist: LineChart?= null

    var counter: Int = 0
    private val TOUCH_SCALE_FACTOR: Float = 180.0f / 320f

    private var previousX: Float = 0f
    private var previousY: Float = 0f
    private var release: Boolean = false

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) //locks in portrait mode
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dead_reckoning)

        graphAcc = findViewById(R.id.graphAcc)
        graphVel = findViewById(R.id.graphVel)
        graphDist = findViewById(R.id.graphDist)


        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }


    override fun onTouchEvent(e: MotionEvent): Boolean {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.


        when (e.action) {
            MotionEvent.ACTION_UP -> {
                release = true
                Log.i("Release", "$release")
            }
            MotionEvent.ACTION_DOWN -> {
                test_text.text = "Not Ready"
            }
        }

        return true
    }

    override fun onResume() {
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



    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            if (timestamp != 0L) {
                if (counter >= 100) {
                    test_text.text = "Release"
                    if (release == true) {
                        //get time difference
                        dT = ((event.timestamp - timestamp) * (1.0f / 1000000000.0f))

                        // set acceleration to zero if there isn't a large change
                        var accX = event.values[2] - OffsetValues.getXOffset()
                      Log.i("Sensor Value", "${event.values[2]}")
                      if (Math.abs(accX)<0.5) {
                            accX = 0.0f
                        }


                        //create acceleration graph points
                        val acc = Entry(xaxis, accX)
                        xAccelerationValues.add(acc)

                        //find velocity from acceleration using old velocity
                        velocityNew[0] = getVelocity(velocityNew[0], accX, dT)



                        //reset velocity to zero, an attempt to combat drift
                       if (accX == 0.0f) {
                            velocityNew[0] = 0.0f
                        }

                        // make velocity graph points
                        val vel = Entry(xaxis, velocityNew[0])
                        xVelocityValues.add(vel)

                        // two different formulas to calculate the distance
                        distance[0] = getDistance(velocityNew[0], dT)
                        X += distance[0]
                        //X = (X + (velocityOld[0]*dT) + (0.5f*accX*dT*dT))

                        //make graph points for position
                        val dist = Entry(xaxis, X)
                        xDistanceValues.add(dist)

                        xaxis += dT

                        xAccelerationValues2.add(accX)
                        dtList.add(this.xaxis)

                        val accDataSet = LineDataSet(xAccelerationValues, "Acceleration")
                        accDataSet.setDrawCircles(false)
                        accDataSet.setColor(Color.BLUE)
                        val accLineData = LineData(accDataSet)
                        graphAcc!!.setData(accLineData)

                        val velDataSet = LineDataSet(xVelocityValues, "Velocity")
                        velDataSet.setDrawCircles(false)
                        velDataSet.setColor(Color.RED)
                        val velLineData = LineData(velDataSet)
                        graphVel!!.setData(velLineData)

                        val distDataSet = LineDataSet(xDistanceValues, "Position")
                        distDataSet.setDrawCircles(false)
                        distDataSet.setColor(Color.GREEN)
                        val distLineData = LineData(distDataSet)
                        graphDist!!.setData(distLineData)

                        Log.i("time difference", "${dT}")
                        Log.i(
                            "Velocity calculated",
                            "${velocityNew[0]}, ${velocityNew[1]}, ${velocityNew[2]}"
                        )
                        Log.i(
                            "distance calculated",
                            "${distance[0]}, ${distance[1]}, ${distance[2]}"
                        )
                        Log.i("coor after", "${X}, ${Y}, ${Z}")
                        Log.i("coor before", "${X}, ${Y}, ${Z}")
                    }
                }
                else
                    counter++

            }
            timestamp = event.timestamp
            graphAcc!!.invalidate()
            graphVel!!.invalidate()
            graphDist!!.invalidate()
        }
    }

    fun saveList(view: View){

        val FILENAME = "Noise3.csv"
        var entry = ""
        var size = xAccelerationValues2.size-1
        for(i in 0..size){
            entry = entry.plus("${dtList[i]},${xAccelerationValues2[i]}\n")
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

    private fun getVelocity(vOld: Float, acc: Float, dT: Float):Float {
        var vNew: Float = vOld+(acc*dT)
        return vNew
    }

    private fun getDistance(V: Float, dT: Float):Float = (V*dT)
}

