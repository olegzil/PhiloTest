package com.philo.interview.utilities

import android.util.Log
import com.philo.interview.DataProviders.StarWarsDirectoryItem
import com.philo.interview.DataProviders.StarWarsPerson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

const val TAG = "PHILO"
fun printLog(msg: String) {
//    if (Log.isLoggable(TAG, Log.DEBUG))
    Log.d(TAG, "=-=-=-=-=-=-= $msg")
}


fun populatePersonData(jsonString: String): List<StarWarsPerson>? {
    var retVal: List<StarWarsPerson>? = null
    try {
        val objectArray = JSONArray(jsonString)
        val workArea = mutableListOf<StarWarsPerson>()
        for (index in 0 until objectArray.length()) {
            workArea.add(
                StarWarsPerson(
                    objectArray.getJSONObject(index).optString("name", "not found"),
                    objectArray.getJSONObject(index).optInt("height", -1),
                    objectArray.getJSONObject(index).optInt("mass", -1),
                    objectArray.getJSONObject(index).optString("hairColor", "not found"),
                    objectArray.getJSONObject(index).optString("eyeColor", "not found"),
                    objectArray.getJSONObject(index).optString("birthYear", "not found"),
                    objectArray.getJSONObject(index).optString("gender", "not found"),
                    objectArray.getJSONObject(index).optString("homeworldUrl", "not found"),
                    objectArray.getJSONObject(index).optString("mass", "not found")
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
