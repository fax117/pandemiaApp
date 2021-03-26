package com.example.pandemia

data class Pais(var nombre: String,
                var latitude: Double,
                var longitude: Double,
                var casos: Double,
                var recuperados: Double,
                var defunciones: Double,
                var tests: Double)
/*

{"updated":1616779857618,
    "country":"Afghanistan",
    "countryInfo":
    {"_id":4,
        "iso2":"AF",
        "iso3":"AFG",
        "lat":33,
        "long":65,
        "flag":"https://disease.sh/assets/img/flags/af.png"
    },
    "cases":56290,
    "todayCases":36,
    "deaths":2469,
    "todayDeaths":0,
    "recovered":49994,
    "todayRecovered":57,
    "active":3827,
    "critical":1105,
    "casesPerOneMillion":1423,
    "deathsPerOneMillion":62,
    "tests":339269,
    "testsPerOneMillion":8575,
    "population":39564129,
    "continent":"Asia",
    "oneCasePerPeople":703,
    "oneDeathPerPeople":16024,
    "oneTestPerPeople":117,
    "activePerOneMillion":96.73,
    "recoveredPerOneMillion":1263.62,
    "criticalPerOneMillion":27.93}
   */