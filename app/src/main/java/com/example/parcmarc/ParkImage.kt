package com.example.parcmarc

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "park_image")
class ParkImage(
        @ColumnInfo var parkId: Long,
        @ColumnInfo var imageURI: String
) : Parcelable {
    @PrimaryKey(autoGenerate = true) var id: Long = 0

    override fun equals(other: Any?): Boolean {
        if (other is ParkImage) {
            return other.imageURI == this.imageURI
        }
        return false
    }
}
