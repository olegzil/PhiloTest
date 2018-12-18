package com.philo.interview.Server

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkApiInterface {
    @GET("?")
    fun fetchRestaurantList(@Query("lat") latitude:Float, @Query("lng") longitude:Float, @Query("limit") limit: Int): Single<String>

    @GET("?")
    fun fetchRLNextPage(@Query("lat") latitude:Float,
                        @Query("lng") longitude:Float,
                        @Query("limit") limit: Int,
                        @Query("page") page:Int): Single<String>
}