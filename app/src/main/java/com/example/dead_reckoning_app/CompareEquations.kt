package com.example.dead_reckoning_app

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.activity_compare_equations.*
import kotlinx.android.synthetic.main.activity_dead_reckoning.*

class CompareEquations : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    var X: Float = 0.0f
    var Y: Float = 0.0f
    var Z: Float = 0.0f

    var velocityNew: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
    var velocityOld: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
    var distance: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
    var equation1: FloatArray = floatArrayOf(0.0f,0.0f,0.0f)
    var equation2: FloatArray = floatArrayOf(0.0f,0.0f,0.0f)
    var equation3: FloatArray = floatArrayOf(0.0f,0.0f,0.0f)

    var dT: Float = 0.0f
    var timestamp: Long = 0

    var counter: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) //locks in portrait mode
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compare_equations)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val linearAccSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
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
            counter++
            if (timestamp != 0L) {
                if(counter > 100) {
                    dT = ((event.timestamp - timestamp) * (1.0f / 1000000000.0f))

                    var accX = event.values[0] - OffsetValues.getXOffset()
                    var accY = event.values[1] - OffsetValues.getYOffset()
                    var accZ = event.values[2] - OffsetValues.getZOffset()
                    if (Math.abs(event.values[0]) < 0.07) {
                        accX = 0.0f
                    }
                    if (Math.abs(event.values[1]) < 0.07) {
                        accY = 0.0f
                    }
                    if (Math.abs(event.values[2]) < 0.07) {
                        accZ = 0.0f
                    }
                    velocityOld[0] = velocityNew[0]
                    velocityOld[1] = velocityNew[1]
                    velocityOld[2] = velocityNew[2]

                    if (accX == 0.0f) {
                        velocityNew[0] = 0.0f
                        velocityNew[1] = 0.0f
                        velocityNew[2] = 0.0f
                    }
                    else {
                        velocityNew[0] = getVelocity(velocityNew[0], accX, dT)
                        velocityNew[1] = getVelocity(velocityNew[1], accY, dT)
                        velocityNew[2] = getVelocity(velocityNew[2], accZ, dT)
                    }



                    equation1[0] += getDistanceEq1(velocityNew[0], dT)
                    equation1[1] += getDistanceEq1(velocityNew[1], dT)
                    equation1[2] += getDistanceEq1(velocityNew[2], dT)
                    Equation1.text = "X = ${equation1[0]}\n\nY = ${equation1[1]}\n\nZ = ${equation1[2]}"

                    equation2[0] += getDistanceEq2(velocityOld[0], velocityNew[0], dT)
                    equation2[1] += getDistanceEq2(velocityOld[1], velocityNew[1], dT)
                    equation2[2] += getDistanceEq2(velocityOld[2], velocityNew[2], dT)
                    Equation2.text = "X = ${equation2[0]}\n\nY = ${equation2[1]}\n\nZ = ${equation2[2]}"


                    //X += distance[0]
                    X = (X + (velocityOld[0]*dT) + (0.5f*accX*dT*dT))
                    Y = (Y + (velocityOld[1]*dT) + (0.5f*accY*dT*dT))
                    Z = (Z + (velocityOld[2]*dT) + (0.5f*accZ*dT*dT))

                    Equation3.text = "X: ${X}\n\nY = $Y\n\nZ = $Z"
                }
            }

            timestamp = event.timestamp
        }


    }

    private fun getVelocity(vOld: Float, acc: Float, dT: Float):Float {
        var vNew: Float = vOld+(acc*dT)
        return vNew
    }

    private fun getDistanceEq1(V: Float, dT: Float):Float = (V*dT)

    private fun getDistanceEq2(Vold: Float, Vnew:Float, dT: Float): Float = (Vnew - Vold)/(2*dT)

}
