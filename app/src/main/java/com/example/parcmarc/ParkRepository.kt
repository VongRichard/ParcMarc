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
    suspend fun update(park: Park, oldImages: List<ParkImage>, newFiles: List<File>) {
        val newImages: MutableList<ParkImage> = mutableListOf()
        for (nImage in newFiles) newImages.add(ParkImage(park.id, nImage.absolutePath))
        for (oImage in oldImages) if (oImage !in newImages) {
            parkImageDao.delete(oImage)
            File(oImage.imageURI).delete()
        }
        for (nImage in newImages) if (nImage !in oldImages) parkImageDao.insert(nImage)
        parkDao.update(park)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(park: Park) {
        parkDao.delete(park)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(parkWithParkImages: ParkWithParkImages) {
        val files: MutableList<File> = mutableListOf()
        for (parkImage in parkWithParkImages.images) {
            files.add(File(parkImage.imageURI))
            parkDao.delete(parkImage)
        }
        parkDao.delete(parkWithParkImages.park)
        for (file in files) {
            file.delete()
        }
    }
}