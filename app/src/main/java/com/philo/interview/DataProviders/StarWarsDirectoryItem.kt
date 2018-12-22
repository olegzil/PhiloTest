package com.philo.interview.DataProviders

data class StarWarsSpecies(
    val name: String,
    val classification: String,
    val designation: String,
    val height: Int,
    val skinColor: String,
    val hairColor: String,
    val eyeColor: String,
    val lifespan: Int,
    val homeworldUrl: String,
    val language: String
)

data class StarWarsVehicles(
    val name: String,
    val model: String,
    val cost: String,
    val length: Int,
    val passengers: Int,
    val consumables: String,
    val vclass: String
)

data class StarWarsPerson(
    val name: String,
    val height: String,
    val mass: String,
    val hairColor: String,
    val skinColor: String,
    val eyeColor: String,
    val birthYear: String,
    val gender: String,
    val homeworldUrl: String
)

data class StarWarsPlanet(
    val name: String,
    val rotationPeriod: String,
    val orbitalPperiod: Int,
    val diameter: Int,
    val climate: String,
    val gravity: String,
    val terrain: String,
    val water: String,
    val population: Int
)

data class StarWarsStarships(
    val name: String,
    val model: String,
    val manufacturer: String,
    val cost: Int,
    val length: Double,
    val airSpeed: Int,
    val crew: Int,
    val passengers: Int,
    val cargoCapacity: Int,
    val consumables: String,
    val hyperdriveRating: Int,
    val MGLT: Int,
    val sclass: String
)

data class StarWarsEpisode(
    val name: String,
    val episodeId: String,
    val openingCrawl: String,
    val director: String,
    val producer: String,
    val releaseDate: String
)

data class StarWarsDirectoryItem(
    val peopleUrl: String,
    val planetsUrl: String,
    val filmsUrl: String,
    val speciesUrl: String,
    val vehiclesUrl: String,
    val starshipsUrl: String
)
