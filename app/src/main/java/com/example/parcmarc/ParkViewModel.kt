package com.example.parcmarc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ParkViewModel(private val parkRepository: ParkRepository): ViewModel() {
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