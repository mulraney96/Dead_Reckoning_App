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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.Viewport
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_dead_reckoning.*
import kotlin.math.pow

//class Plot2Activity : AppCompatActivity() , SensorEventListener {

    /* private lateinit var sensorManager: SensorManager

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
    var xaxis = 0.0f
    var graphAcc: LineChart? = null
    var graphVel: LineChart? = null
    var graphDist: LineChart?= null

    var counter: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) //locks in portrait mode
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dead_reckoning)

        graphAcc = findViewById(R.id.graphAcc)
        graphVel = findViewById(R.id.graphVel)
        graphDist = findViewById(R.id.graphDist)


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
                if(counter > 20) {
                    dT = ((event.timestamp - timestamp) * (1.0f / 1000000000.0f))

                    var accX = event.values[2] - OffsetValues.getYOffset()
                    if (Math.abs(event.values[2]) < 0.09) {
                        accX = 0.0f
                    }


                    val acc = Entry(xaxis, accX)
                    xAccelerationValues.add(acc)

                    velocityOld[0] = velocityNew[0]
                    velocityNew[0] = getVelocity(velocityNew[0], accX, dT)
                    if (accX == 0.0f) {
                        velocityNew[0] = 0.0f
                    }
                    val vel = Entry(xaxis, velocityNew[0])
                    xVelocityValues.add(vel)

                    distance[0] = getDistance(velocityNew[0], dT)
                    //X += distance[0]
                    X = (X + (velocityOld[0]*dT) + (0.5f*accX*dT*dT))
                    coordinate_text.text = "X: ${X}"
                    val dist = Entry(xaxis, X)
                    xDistanceValues.add(dist)

                    xaxis += 0.2f

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
                }
            }
            Log.i("time difference", "${dT}")
            Log.i("Velocity calculated", "${velocityNew[0]}, ${velocityNew[1]}, ${velocityNew[2]}")
            Log.i("distance calculated", "${distance[0]}, ${distance[1]}, ${distance[2]}")
            Log.i("coor after", "${X}, ${Y}, ${Z}")
            Log.i("coor before", "${X}, ${Y}, ${Z}")
            timestamp = event.timestamp
            graphAcc!!.invalidate()
            graphVel!!.invalidate()
            graphDist!!.invalidate()
        }


    }

    private fun getVelocity(vOld: Float, acc: Float, dT: Float):Float {
        var vNew: Float = vOld+(acc*dT)
        return vNew
    }

    private fun getDistance(V: Float, dT: Float):Float = (V*dT)
}
*/
//}

