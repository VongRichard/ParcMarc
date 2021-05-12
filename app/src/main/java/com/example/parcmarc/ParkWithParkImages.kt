package com.example.parcmarc

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParkWithParkImages(
    @Embedded val park: Park,
    @Relation(
        parentColumn = "id",
        entityColumn = "parkId"
    )
    val images: List<ParkImage>
): Parcelable
