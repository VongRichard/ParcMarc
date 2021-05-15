package com.example.parcmarc

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize
import java.time.Duration
import java.time.Duration.between
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.*

@Parcelize
@Entity(tableName = "park")
class Park(
    @ColumnInfo var name: String,
    @ColumnInfo var location: LatLng,
    @ColumnInfo var endDate: Date?,
    @PrimaryKey(autoGenerate = true) var id: Long = 0
) : Parcelable {

    override fun toString() = name

//    fun timeLeft(): Duration? {
//        if (endDate == null) {
//            return null
//        }
//        val timeRemaining = endDate!!.time - Date().time
//        return Duration.ofMillis(timeRemaining)
//    }
}