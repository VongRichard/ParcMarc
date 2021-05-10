package com.example.parcmarc

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class ParkRepository(private val parkDao: ParkDao,
                     private val parkImageDao: ParkImageDao) {
    val parks: Flow<List<Park>> = parkDao.getAll()
    val numParks: Flow<Int> = parkDao.getCount()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(park: Park): Long {
        return parkDao.insert(park)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun findParkImagesByParkId(parkId: Long): List<ParkImage> {
        return parkImageDao.findParkImagesByParkId(parkId)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(parkImage: ParkImage): Long {
        return parkImageDao.insert(parkImage)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(park: Park) {
        parkDao.update(park)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(parkImage: ParkImage) {
        return parkImageDao.update(parkImage)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(park: Park) {
        parkDao.delete(park)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(parkImage: ParkImage) {
        parkImageDao.delete(parkImage)
    }
}