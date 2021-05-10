package com.example.parcmarc

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import java.util.*

class Converters {
    @TypeConverter
    fun stringToLatLng(value: String): LatLng {
        val valueSplit = value.split(",")
        return LatLng(valueSplit[0].toDouble(), valueSplit[1].toDouble())
    }

    @TypeConverter
    fun latLngToString(latLng: LatLng) = latLng.latitude.toString() + "," + latLng.longitude.toString();

    @TypeConverter
    fun longToDate(value: Long): Date? {
        val zeroLong:Long = 0
        if (value == zeroLong) {
            return null
        }
        return Date(value)
    }

    @TypeConverter
    fun dateToLong(date: Date?): Long {
        if (date == null) {
            return 0
        }
        return date.time.toLong()
    }
}