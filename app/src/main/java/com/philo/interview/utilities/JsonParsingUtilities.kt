package com.philo.interview.utilities

import android.util.Log
import com.philo.interview.DataProviders.*
import com.philo.interview.Server.NetworkServiceInitializer
import com.philo.interview.Server.RetrofitNetworkService
import com.philo.interview.constants.results
import io.reactivex.Single
import org.json.JSONException
import org.json.JSONObject

const val TAG = "PHILO"
fun printLog(msg: String) {
//    if (Log.isLoggable(TAG, Log.DEBUG))
    Log.d(TAG, "=-=-=-=-=-=-= $msg")
}

fun populateSpiciesList(jsonString: String): List<StarWarsSpecies>? {
    var retVal: List<StarWarsSpecies>? = null
    try {
        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray(results)
        val workArea = mutableListOf<StarWarsSpecies>()
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
        retVal = workArea
    } catch (e: JSONException) {
        return null
    } finally {
        return retVal
    }
}


fun <R> fetchJsonDataFromServerList(
    url: String,
    dataGenerator: (String) -> List<R>?
): Single<List<R>>? {
    val retrofit = RetrofitNetworkService(NetworkServiceInitializer(url))
        .getApi()?.fetchData("")?.let { single ->
            single.flatMap {
                RetrofitNetworkService(NetworkServiceInitializer(url)).getApi()
                    ?.fetchData(url)
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
    return retrofit
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

fun populateVehicleList(jsonString: String): List<StarWarsVehicles>? {
    var retVal: List<StarWarsVehicles>? = null
    try {
        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray(results)
        val workArea = mutableListOf<StarWarsVehicles>()
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
        retVal = workArea
    } catch (e: JSONException) {
        return null
    } finally {
        return retVal
    }
}

fun populatePlanetList(jsonString: String): List<StarWarsPlanet>? {
    var retVal: List<StarWarsPlanet>? = null
    try {
        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray(results)
        val workArea = mutableListOf<StarWarsPlanet>()
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
        retVal = workArea
    } catch (e: JSONException) {
        return null
    } finally {
        return retVal
    }
}

fun populatePersonData(jsonString: String): List<StarWarsPerson>? {
    var retVal: List<StarWarsPerson>? = null
    try {
        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray(results)
        val workArea = mutableListOf<StarWarsPerson>()
        for (index in 0 until jsonArray.length()) {
            workArea.add(
                StarWarsPerson(
                    jsonArray.getJSONObject(index).optString("name", "not found"),
                    jsonArray.getJSONObject(index).optInt("height", -1),
                    jsonArray.getJSONObject(index).optInt("mass", -1),
                    jsonArray.getJSONObject(index).optString("hairColor", "not found"),
                    jsonArray.getJSONObject(index).optString("eyeColor", "not found"),
                    jsonArray.getJSONObject(index).optString("birthYear", "not found"),
                    jsonArray.getJSONObject(index).optString("gender", "not found"),
                    jsonArray.getJSONObject(index).optString("homeworldUrl", "not found"),
                    jsonArray.getJSONObject(index).optString("mass", "not found")
                )
            )
        }
        retVal = workArea
    } catch (e: JSONException) {
        return null
    } finally {
        return retVal
    }
}

fun populateStarshipData(jsonString: String): List<StarWarsStarships>? {
    var retVal: List<StarWarsStarships>? = null
    try {
        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray(results)
        val workArea = mutableListOf<StarWarsStarships>()
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
        retVal = workArea
    } catch (e: JSONException) {
        return null
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
        return null
    } finally {
        return retVal
    }
}
