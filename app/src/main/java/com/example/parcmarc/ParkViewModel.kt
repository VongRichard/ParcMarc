package com.example.parcmarc

import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class ParkViewModel(private val parkRepository: ParkRepository): ViewModel() {
    val parks: LiveData<List<Park>> = parkRepository.parks.asLiveData()
    val numParks: LiveData<Int> = parkRepository.numParks.asLiveData()

    fun addPark(park: Park, images: List<File>) = viewModelScope.launch {
        val id: Long = parkRepository.insert(park)
        for (image in images) {
            parkRepository.insert(ParkImage(id, image.absolutePath))
        }
    }

    suspend fun findParkImagesByParkId(parkId: Long): List<ParkImage> = parkRepository.findParkImagesByParkId(parkId)

    fun updatePark(park: Park) = viewModelScope.launch {
        parkRepository.update(park)
    }

    fun removePark(park: Park) = viewModelScope.launch {
        parkRepository.delete(park)
    }
}

class ParkViewModelFactory(private val repository: ParkRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParkViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ParkViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}