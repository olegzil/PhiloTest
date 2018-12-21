package com.philo.interview.utilities

import android.util.Log
import com.philo.interview.DataProviders.*
import com.philo.interview.Server.NetworkServiceInitializer
import com.philo.interview.Server.RetrofitNetworkService
import com.philo.interview.constants.results
import io.reactivex.Single
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

const val TAG = "PHILO"
fun printLog(msg: String) {
//    if (Log.isLoggable(TAG, Log.DEBUG))
    Log.d(TAG, "=-=-=-=-=-=-= $msg")
}

fun populateSpiciesList(jsonList: List<String>): List<StarWarsSpecies>? {
    var retVal: List<StarWarsSpecies>? = null
    val workArea = mutableListOf<StarWarsSpecies>()
    try {
        jsonList.forEach { jsonString ->
            val jsonObject = JSONObject(jsonString)
            val jsonArray = jsonObject.getJSONArray(results)
            for (index in 0 until jsonArray.length()) {
                workArea.add(
                    StarWarsSpecies(
                        jsonArray.getJSONObject(index).optString("name", "not found"),
                        jsonArray.getJSONObject(index).optString("classification", "not found"),
                        jsonArray.getJSONObject(index).optString("designation", "not found"),
                        jsonArray.getJSONObject(index).optInt("height", -1),
                        jsonArray.getJSONObject(index).optString("skinColor", "not found"),
                        jsonArray.getJSONObject(index).optString("hairColor", "not found"),
                        jsonArray.getJSONObject(index).optString("eyeColor", "not found"),
                        jsonArray.getJSONObject(index).optInt("lifespan", -1),
                        jsonArray.getJSONObject(index).optString("homeworldUrl", "not found"),
                        jsonArray.getJSONObject(index).optString("language", "not found")
                    )
                )
            }
        }
        retVal = workArea
    } catch (e: JSONException) {
        Timber.e(e)
    } finally {
        return retVal
    }
}

fun <R> fetchJsonDataFromServerList(
    url: String,
    dataGenerator: (List<String>) -> List<R>?
): Single<List<R>>? {
    return RetrofitNetworkService(NetworkServiceInitializer(url))
        .getApi()?.fetchData("")?.let<Single<String>, Single<List<R>>?> { single ->
            single.flatMap {
                RetrofitNetworkService(NetworkServiceInitializer(url)).getApi()
                    ?.fetchData(url)
            }
                .flatMap { jsonString ->
                    val arrayOfItems = mutableListOf<String>()
                    var nextPage: String = JSONObject(jsonString).optString("next", "*")
                    while (nextPage != "*" && nextPage != "null") {
                        RetrofitNetworkService(NetworkServiceInitializer(nextPage)).getApi()
                            ?.fetchData(nextPage)
                            ?.blockingGet()
                            ?.let { receivedString ->
                                nextPage = JSONObject(receivedString).optString("next", null)
                                if (nextPage.length > "next".length) {
                                    arrayOfItems.add(receivedString)
                                    nextPage = nextPage.split("&dummy")[0]
                                }
                            } ?: run {
                            nextPage = "*"
                        }
                    }
                    Single.just(arrayOfItems)
                }
                .flatMap { jsonCharacters ->
                    val characters = dataGenerator(jsonCharacters)
                    if (characters != null) {
                        Single.just(characters)
                    } else {
                        Single.never()
                    }
                }
        }
}

fun <R> fetchJsonDataFromServerItem(url: String, dataGenerator: (String) -> R?): Single<R>? {
    return RetrofitNetworkService(NetworkServiceInitializer(url))
        .getApi()?.fetchData("")?.run {
            flatMap {
                RetrofitNetworkService(NetworkServiceInitializer(url)).getApi()
                    ?.fetchData(url)
            }
                .flatMap { jsonCharacters ->
                    val characters = dataGenerator(jsonCharacters)
                    if (characters != null)
                        Single.just(characters)
                    else
                        Single.never()
                }
        }
}

fun populateVehicleList(jsonList: List<String>): List<StarWarsVehicles>? {
    var retVal: List<StarWarsVehicles>? = null
    val workArea = mutableListOf<StarWarsVehicles>()
    try {
        jsonList.forEach { jsonString ->
            val jsonObject = JSONObject(jsonString)
            val jsonArray = jsonObject.getJSONArray(results)
            for (index in 0 until jsonArray.length()) {
                workArea.add(
                    StarWarsVehicles(
                        jsonArray.getJSONObject(index).optString("name", "not found"),
                        jsonArray.getJSONObject(index).optString("model", "not found"),
                        jsonArray.getJSONObject(index).optString("cost", "not found"),
                        jsonArray.getJSONObject(index).optInt("length", -1),
                        jsonArray.getJSONObject(index).optInt("passengers", -1),
                        jsonArray.getJSONObject(index).optString("consumables", "not found"),
                        jsonArray.getJSONObject(index).optString("vclass", "not found")
                    )
                )
            }
        }
        retVal = workArea
    } catch (e: JSONException) {
        Timber.e(e)
    } finally {
        return retVal
    }
}

fun populatePlanetList(jsonList: List<String>): List<StarWarsPlanet>? {
    var retVal: List<StarWarsPlanet>? = null
    val workArea = mutableListOf<StarWarsPlanet>()
    try {
        jsonList.forEach { jsonString ->
            val jsonObject = JSONObject(jsonString)
            val jsonArray = jsonObject.getJSONArray(results)
            for (index in 0 until jsonArray.length()) {
                workArea.add(
                    StarWarsPlanet(
                        jsonArray.getJSONObject(index).optString("name", "not found"),
                        jsonArray.getJSONObject(index).optString("rotationPeriod", "not found"),
                        jsonArray.getJSONObject(index).optInt("orbitalPperiod", -1),
                        jsonArray.getJSONObject(index).optInt("diameter", -1),
                        jsonArray.getJSONObject(index).optString("climate", "not found"),
                        jsonArray.getJSONObject(index).optString("gravity", "not found"),
                        jsonArray.getJSONObject(index).optString("terrain", "not found"),
                        jsonArray.getJSONObject(index).optString("water", "not found"),
                        jsonArray.getJSONObject(index).optInt("population", -1)
                    )
                )
            }
        }
        retVal = workArea
    } catch (e: JSONException) {
        Timber.e(e)
    } finally {
        return retVal
    }
}

fun populatePersonData(jsonArray: List<String>): List<StarWarsPerson>? {
    var retVal: List<StarWarsPerson>? = null
    val workArea = mutableListOf<StarWarsPerson>()
    try {
        jsonArray.forEach { jsonString ->
            val jsonObject = JSONObject(jsonString)
            val resultsArray = jsonObject.getJSONArray(results)
            for (index in 0 until resultsArray.length()) {
                workArea.add(
                    StarWarsPerson(
                        resultsArray.getJSONObject(index).optString("name", "not found"),
                        resultsArray.getJSONObject(index).optString("height", "0"),
                        resultsArray.getJSONObject(index).optString("mass", "0"),
                        resultsArray.getJSONObject(index).optString("hair_color", "not found"),
                        resultsArray.getJSONObject(index).optString("skin_color", "not found"),
                        resultsArray.getJSONObject(index).optString("eye_color", "not found"),
                        resultsArray.getJSONObject(index).optString("birth_year", "not found"),
                        resultsArray.getJSONObject(index).optString("gender", "not found"),
                        resultsArray.getJSONObject(index).optString("homeworldUrl", "not found")
                    )
                )
            }
        }
        retVal = workArea
    } catch (e: JSONException) {
        Timber.e(e)
    } finally {
        return retVal
    }
}

fun populateStarshipData(jsonList: List<String>): List<StarWarsStarships>? {
    var retVal: List<StarWarsStarships>? = null
    val workArea = mutableListOf<StarWarsStarships>()
    try {
        jsonList.forEach { jsonString ->
            val jsonObject = JSONObject(jsonString)
            val jsonArray = jsonObject.getJSONArray(results)
            for (index in 0 until jsonArray.length()) {
                workArea.add(
                    StarWarsStarships(
                        jsonArray.getJSONObject(index).optString("name", "not found"),
                        jsonArray.getJSONObject(index).optString("model", "not found"),
                        jsonArray.getJSONObject(index).optString("manufacturer", "not found"),
                        jsonArray.getJSONObject(index).optInt("cost", -1),
                        jsonArray.getJSONObject(index).optDouble("length", 0.0),
                        jsonArray.getJSONObject(index).optInt("airSpeed", -1),
                        jsonArray.getJSONObject(index).optInt("crew", -1),
                        jsonArray.getJSONObject(index).optInt("passengers", -1),
                        jsonArray.getJSONObject(index).optInt("cargoCapacity", -1),
                        jsonArray.getJSONObject(index).optString("consumables", "not found"),
                        jsonArray.getJSONObject(index).optInt("hyperdriveRating", -1),
                        jsonArray.getJSONObject(index).optInt("MGLT", -1),
                        jsonArray.getJSONObject(index).optString("sclass", "not found")
                    )
                )
            }
        }
        retVal = workArea
    } catch (e: JSONException) {
        Timber.e(e)
    } finally {
        return retVal
    }
}

fun populateDirecotry(jsonString: String): StarWarsDirectoryItem? {
    var retVal: StarWarsDirectoryItem? = null
    try {
        val jsonObject = JSONObject(jsonString)
        retVal = StarWarsDirectoryItem(
            jsonObject.optString("people", "not found"),
            jsonObject.optString("planets", "not found"),
            jsonObject.optString("films", "not found"),
            jsonObject.optString("species", "not found"),
            jsonObject.optString("vehicles", "not found"),
            jsonObject.optString("starships", "not found")
        )
    } catch (e: JSONException) {
        Timber.e(e)
    } finally {
        return retVal
    }
}
