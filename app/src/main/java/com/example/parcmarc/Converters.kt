package com.example.parcmarc

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import java.util.*

class Converters {
    @TypeConverter
    fun stringToLatLng(value: String): LatLng {
        val valueSplit = value.split(",")
        return LatLng(valueSplit[0].toDouble(), valueSplit[2].toDouble())
    }

    @TypeConverter
    fun latLngToString(latLng: LatLng) = latLng.latitude.toString() + "," + latLng.longitude.toString();
}