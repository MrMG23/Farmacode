package com.bangkit.farmacode.utils

object Formatter {
    fun change(rawData: String): String {
        val arrData = rawData.split(", ").toTypedArray()
        var finalData = ""
        for (datum in arrData) {
            finalData += " $datum \n"
        }
        return finalData
    }
}