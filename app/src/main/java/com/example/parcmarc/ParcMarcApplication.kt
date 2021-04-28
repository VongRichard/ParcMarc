package com.example.parcmarc

import android.app.Application

class ParcMarcApplication: Application() {
    val database by lazy { ParkDatabase.getDatabase(this) }
    val repository by lazy { ParkRepository(database.parkDao()) }
}