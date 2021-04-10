package com.example.pandemia

import com.google.gson.annotations.SerializedName
import java.util.function.DoubleToLongFunction

data class PaisJson(
    @SerializedName("country")
    var nombre: String?,
    var countryInfo: CountryInfo,
    /*@SerializedName("cases") var casos: Double?,
    @SerializedName("recovered") var recuperados: Double*/
)
data class CountryInfo (var lat: Double?, var long: Double?)
