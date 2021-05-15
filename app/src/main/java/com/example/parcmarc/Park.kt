package com.example.parcmarc

import android.icu.math.BigDecimal
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize
import java.time.Duration
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


    fun timeLeft(): String {
        if (endDate == null) {
            return "Unlimited"
        }
        val timeRemaining = endDate!!.time - Date().time
        val timeLeft =  Duration.ofMillis(timeRemaining)

        return when {
            (timeLeft.toMillis() < 0) -> "Duration exceeded"
            (timeLeft.toMinutes() < 1L) -> "< a minute remaining"
            else -> {
                val hours = timeLeft.toHours(); val minutes = timeLeft.toMinutes() - hours*60
                "${hours}h ${minutes}m remaining"
            }
        }
    }
}