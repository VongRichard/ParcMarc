package com.example.parcmarc

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "park")
class Park(
    @ColumnInfo var name: String,
    @ColumnInfo var location: LatLng
) {

    @PrimaryKey(autoGenerate = true) var id: Long = 0

    override fun toString() = name
}