package com.example.pandemia

import com.google.gson.annotations.SerializedName

data class ContinenteGson(
    @SerializedName("continent")
    var nombre: String?,
    @SerializedName("tests")
    var tests: Double,
    var continentInfo: ContinentInfo,
)

data class ContinentInfo(var lat: Double?, var long: Double?)
