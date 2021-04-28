package com.example.parcmarc

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class ParkRepository(private val parkDao: ParkDao) {
    val parks: Flow<List<Park>> = parkDao.getAll()
    val numParks: Flow<Int> = parkDao.getCount()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(park: Park) {
        parkDao.insert(park)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(park: Park) {
        parkDao.update(park)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(park: Park) {
        parkDao.delete(park)
    }
}