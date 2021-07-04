package com.telkomsel.hometestmusicplayer.data.network


import com.telkomsel.hometestmusicplayer.data.model.MusicModel
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {


    @GET("search")
    fun searchMusic(@Query("term") searchParam: String, @Query("media") media: String) : Observable<MusicModel>


    @GET("search")
    fun getMusicFromTest(@Query("term") searchParam: String, @Query("media") media: String) : Call<MusicModel>
}