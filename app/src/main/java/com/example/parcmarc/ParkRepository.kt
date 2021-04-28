package com.example.parcmarc

import androidx.annotation.WorkerThread

class ParkRepository(private val parkDao: ParkDao) {
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