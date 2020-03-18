package com.example.dead_reckoning_app

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity(){

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun startLastHope(view: View){
        val intent = Intent(this, ThisBetterFuckingWork::class.java)
        startActivity(intent)
    }

    fun startCalibration(view: View){
        val intent = Intent(this, OffsetActivity::class.java)
        startActivity(intent)
    }

    fun startController(view: View){
        val intent = Intent(this, DeadReckoningActivity::class.java)
        startActivity(intent)
    }

    fun startCompare(view:View){
        val intent = Intent(this, CompareEquations::class.java)
        startActivity(intent)
    }

    fun startSensorReadings(view:View){
        val intent = Intent(this, SensorActivity::class.java)
        startActivity(intent)
    }

}