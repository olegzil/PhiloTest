package com.philo.interview.Server

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkApiInterface {
    @GET(" ")
    fun fetchData(@Query("dummy") endPoint:String ): Single<String>

    @GET("?")
    fun search(@Query("search") searchText:String ): Single<String>
}