package com.philo.interview.Server

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkApiInterface {
    @GET("/")
    fun fetchData(endPoint:String ): Single<String>

    @GET("?")
    fun fetchNextPage(@Query("lat") latitude:Float,
                        @Query("lng") longitude:Float,
                        @Query("limit") limit: Int,
                        @Query("page") page:Int): Single<String>
}