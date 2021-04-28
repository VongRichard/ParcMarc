package com.example.parcmarc

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class ParkViewModel(private val parkRepository: ParkRepository): ViewModel() {
    val parks: LiveData<List<Park>> = parkRepository.parks.asLiveData()
    val numParks: LiveData<Int> = parkRepository.numParks.asLiveData()

    fun addPark(park: Park) = viewModelScope.launch {
        parkRepository.insert(park)
    }

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