package com.telkomsel.hometestmusicplayer.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.telkomsel.hometestmusicplayer.data.model.MusicModel
import com.telkomsel.hometestmusicplayer.data.repository.MusicRepository

class MusicViewModel : ViewModel() {

    var repository = MusicRepository()
    var musicsLiveData = MutableLiveData<MusicModel>()
    var error = MutableLiveData<Throwable>()

    fun searchMusic(paramSearch: String, media: String) {

        repository.searchMusic(paramSearch, media, {
            //if response search music success
            musicsLiveData.postValue(it)
        } , {
            error.postValue(it)
        })
    }


    fun getMusic() : LiveData<MusicModel> {
        return musicsLiveData
    }

    override fun onCleared() {
        super.onCleared()
        repository.onDestroy()
    }
}