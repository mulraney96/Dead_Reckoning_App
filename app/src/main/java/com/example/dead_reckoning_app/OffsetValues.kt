package com.example.dead_reckoning_app

object OffsetValues {
    private var positionalOffsetValues: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
    private var FILENAME = "default.csv"


    fun setFileName(file: String){
        this.FILENAME = file
    }

    fun getFileName():String{
        return this.FILENAME
    }
    fun setPositionalOffsetValues(X: Float, Y: Float, Z :Float){
        positionalOffsetValues[0] = X
        positionalOffsetValues[1] = Y
        positionalOffsetValues[2] = Z
    }

    fun getXOffset(): Float{
        return positionalOffsetValues[0]
    }

    fun getYOffset(): Float{
        return positionalOffsetValues[1]
    }

    fun getZOffset(): Float{
        return positionalOffsetValues[2]
    }
}