package com.example.dead_reckoning_app

import android.annotation.SuppressLint
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
import android.view.View
import android.widget.Toast
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.activity_compare_equations.*
import kotlinx.android.synthetic.main.activity_dead_reckoning.*
import java.io.FileOutputStream

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

    var gravity: FloatArray = floatArrayOf(0.0f, 0.0f, 9.8f)
    var manualLinearAcceleration: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)

    var dT: Float = 0.0f
    var timestamp: Long = 0

    var manualdT = 0.0f
    var manualTimestamp = 0L

    var counter: Int = 0

    var compositeSensor = arrayListOf<Float>()
    var manualSensor = arrayListOf<Float>()
    var compositedtList = arrayListOf<Float>()
    var manualdtList = arrayListOf<Float>()
    var compositeTime = 0f
    var manualTime = 0f

    @SuppressLint("SourceLockedOrientationActivity")
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
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            SensorManager.SENSOR_DELAY_GAME
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

                    compositeTime += dT
                    compositedtList.add(compositeTime)

                    var accX = event.values[0] - OffsetValues.getXOffset()
                    var accY = event.values[1] - OffsetValues.getYOffset()
                    var accZ = event.values[2] - OffsetValues.getZOffset()

                    compositeSensor.add(accX)
                    compositeSensor.add(accY)
                    compositeSensor.add(accZ)

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

        if(event.sensor.type == Sensor.TYPE_ACCELEROMETER){
            counter++
            if (manualTimestamp != 0L) {
                if (counter > 100) {
                    manualdT = ((event.timestamp - manualTimestamp) * (1.0f / 1000000000.0f))
                    manualTime += manualdT
                    manualdtList.add(manualTime)
                    val alpha: Float = 0.8f

                    // Isolate the force of gravity with the low-pass filter.
                    gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                    gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
                    gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

                    // Remove the gravity contribution with the high-pass filter.
                    manualLinearAcceleration[0] = event.values[0] - gravity[0]
                    manualLinearAcceleration[1] = event.values[1] - gravity[1]
                    manualLinearAcceleration[2] = event.values[2] - gravity[2]

                    manualSensor.add(manualLinearAcceleration[0])
                    manualSensor.add(manualLinearAcceleration[1])
                    manualSensor.add(manualLinearAcceleration[2])

                }
            }
            manualTimestamp = event.timestamp
        }

    }

    fun saveList(view: View){
        sensorManager.unregisterListener(this)

        var FileName: String  = "Offset.csv"
        var manualFileName: String  = "manual".plus(FileName)
        var compositeFileName: String  = "composite".plus(FileName)


        var compositeEntry = "0,0,0,0\n"
        var manualEntry = "0,0,0,0\n"

        var size = (compositeSensor.size /3)-1
        Log.i("Size" , "$size")
        for(i in 0..size){
            Log.i("i", "$i")
            Log.i("entry 1 ", "${compositedtList[i]},${compositeSensor[i*3]},${compositeSensor[i*3+1]},${compositeSensor[i*3+2]}\n")
            compositeEntry = compositeEntry.plus("${compositedtList[i]},${compositeSensor[i*3]},${compositeSensor[(i*3)+1]},${compositeSensor[(i*3)+2]}\n")
        }
        size = (manualSensor.size / 3) - 1
        for(i in 0..size){
            manualEntry = manualEntry.plus("${manualdtList[i]},${manualSensor[i*3]},${manualSensor[i*3+1]},${manualSensor[i*3+2]}\n")
        }
        try{
            var out: FileOutputStream = openFileOutput(manualFileName, Context.MODE_APPEND)
            out.write(manualEntry.toByteArray())
            Toast.makeText(this, "Saved to $filesDir/$manualFileName", Toast.LENGTH_SHORT).show()
            out.close()

            var outX: FileOutputStream = openFileOutput(compositeFileName, Context.MODE_APPEND)
            outX.write(compositeEntry.toByteArray())
            Toast.makeText(this, "Saved to $filesDir/$compositeFileName", Toast.LENGTH_SHORT).show()
            outX.close()

        }
        catch(e: Exception){
            e.printStackTrace()
        }

    }


    private fun getVelocity(vOld: Float, acc: Float, dT: Float):Float {
        var vNew: Float = vOld+(acc*dT)
        return vNew
    }

    private fun getDistanceEq1(V: Float, dT: Float):Float = (V*dT)

    private fun getDistanceEq2(Vold: Float, Vnew:Float, dT: Float): Float = (Vnew - Vold)/(2*dT)

}
