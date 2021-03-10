package com.harnet.protocolbuffer.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.harnet.protocolbuffer.ProtoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    //create repository
    private val repository = ProtoRepository(application)

    val mFirstName = repository.readProto.asLiveData()

    fun updateFirstName(newFirstName: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.updatePersonFirstName(newFirstName)
    }

}