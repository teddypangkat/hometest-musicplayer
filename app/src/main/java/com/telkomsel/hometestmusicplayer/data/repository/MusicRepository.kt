package com.telkomsel.hometestmusicplayer.data.repository

import com.telkomsel.hometestmusicplayer.data.model.MusicModel
import com.telkomsel.hometestmusicplayer.data.network.ApiObserver
import com.telkomsel.hometestmusicplayer.data.network.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MusicRepository {

    private val apiService = RetrofitClient.create()
    private val compositeDisposable = CompositeDisposable()

    fun searchMusic(searchParam: String, onResult: (MusicModel)-> Unit, onError: (Throwable) -> Unit) {
            apiService.searchMusic(searchParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ApiObserver<MusicModel>(compositeDisposable) {
                    override fun onApiSuccess(data: MusicModel) {
                        onResult(data)
                    }

                    override fun onApiError(er: Throwable) {
                        onError(er)
                    }
                })
    }
}