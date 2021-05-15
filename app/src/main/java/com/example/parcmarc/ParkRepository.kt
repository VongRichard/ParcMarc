package com.example.parcmarc

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import java.io.File

class ParkRepository(private val parkDao: ParkDao,
                     private val parkImageDao: ParkImageDao) {
    val parks: Flow<List<ParkWithParkImages>> = parkDao.getAll()
    val numParks: Flow<Int> = parkDao.getCount()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(park: Park): Long {
        return parkDao.insert(park)
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
    suspend fun delete(parkWithParkImages: ParkWithParkImages) {
        for (parkImage in parkWithParkImages.images) {
            File(parkImage.imageURI).delete()
            parkDao.delete(parkImage)
        }
        parkDao.delete(parkWithParkImages.park)
    }
}