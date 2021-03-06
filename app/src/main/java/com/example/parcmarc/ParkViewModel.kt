package com.example.parcmarc

import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import java.io.File

class ParkViewModel(private val parkRepository: ParkRepository): ViewModel() {
    val parks: LiveData<List<ParkWithParkImages>> = parkRepository.parks.asLiveData()
    val numParks: LiveData<Int> = parkRepository.numParks.asLiveData()

    private var _tempImages = MutableLiveData<MutableList<File>>(arrayListOf())
    private var _numTempImages = MutableLiveData<Int>(tempImages.value!!.size)

    val tempImages: MutableLiveData<MutableList<File>>
        get() = _tempImages

    val numTempImages: LiveData<Int>
        get() = _numTempImages

    fun clearTempImages() {
        _tempImages.value?.clear()
        _tempImages.notifyObserver()
        _numTempImages.value = _tempImages.value!!.size
    }

    fun addTempImage(file: File) {
        _tempImages.value?.add(file)
        _tempImages.notifyObserver()
        _numTempImages.value = _tempImages.value!!.size
    }

    fun setTempImages(parkImages: List<ParkImage>) {
        _tempImages.value?.clear()
        for (parkImage in parkImages) {
            _tempImages.value?.add(File(parkImage.imageURI))
        }
        _tempImages.notifyObserver()
        _numTempImages.value = _tempImages.value!!.size
    }

    fun removeAndDeleteTempImage(file: File) {
        _tempImages.value?.remove(file)
        file.delete()
        _tempImages.notifyObserver()
        _numTempImages.value = _tempImages.value!!.size
    }

    fun removeTempImage(file: File) {
        _tempImages.value?.remove(file)
        _tempImages.notifyObserver()
        _numTempImages.value = _tempImages.value!!.size
    }

    private var _tempLocation = MutableLiveData(LatLng(-43.5214188,172.5838315))

    val tempLocation: LiveData<LatLng>
        get() = _tempLocation

    fun setLocation(location: LatLng) {
        _tempLocation.value = location
        _tempLocation.notifyObserver()
    }

    private var _tempDuration: MutableLiveData<Triple<Int, Int, Int>?> = MutableLiveData(null)

    val tempDuration: LiveData<Triple<Int, Int, Int>?>
        get() = _tempDuration

    fun setDuration(duration: Triple<Int, Int, Int>?) {
        _tempDuration.value = duration
        _tempDuration.notifyObserver()
    }

    fun clearCreateEditTemps() {
        clearTempImages()
        setDuration(null)
        setLocation(LatLng(0.0, 0.0))
    }

    fun addPark(park: Park, images: List<File>) = runBlocking {
        withContext(Dispatchers.Default) {
            val id: Long = parkRepository.insert(park)
            for (image in images) {
                parkRepository.insert(ParkImage(id, image.absolutePath))
            }
            id
        }
    }

    fun updatePark(park: Park, oldImages: List<ParkImage>, newImages: List<File>) = viewModelScope.launch {
        parkRepository.update(park, oldImages, newImages)
    }

    fun removePark(parkWithParkImages: ParkWithParkImages) = viewModelScope.launch {
        parkRepository.delete(parkWithParkImages)
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
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