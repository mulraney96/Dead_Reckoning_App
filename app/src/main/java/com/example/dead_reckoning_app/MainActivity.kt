package com.example.dead_reckoning_app

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_this_better_fucking_work.*

class MainActivity : AppCompatActivity(){

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun saveFileName(view: View){
        val filename = editText.text.toString()
        OffsetValues.setFileName(filename)
        Toast.makeText(this, "Set to $filename", Toast.LENGTH_SHORT).show()
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


}