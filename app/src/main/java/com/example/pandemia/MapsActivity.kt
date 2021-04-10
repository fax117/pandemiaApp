 package com.example.pandemia

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    //private val url = "https://gist.githubusercontent.com/fax117/396a3d23e27c401273673b2e48002cda/raw/4311df593b292ee2f7dd3ae4757b2563b4065340/db.json"
    private val url = "https://disease.sh/v3/covid-19/countries"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        cargaDatos()
        getContries()
        getContinents()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, tz|he user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        /*val sydney = LatLng(-34.0, 151.0)
        val mxCity = LatLng(19.432608, -99.1332209)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.addMarker(MarkerOptions().position(mxCity).title("Marker in Mexico City"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mxCity))*/
    }

    fun viewData(view: View){

        mMap.clear()
        /*for (pais in data ){
            mMap.addMarker(
                    MarkerOptions()
                            .position(LatLng(pais.latitude, pais.longitude))
                            .title(pais.nombre)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                            //.icon(BitmapDescriptorFactory.fromAsset("arrow.xml"))
            )
        }*/
        for (pais in paisesGson ){
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(pais.countryInfo.lat?:0.0, pais.countryInfo.long?:0.0))
                    .title(pais.nombre)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                //.icon(BitmapDescriptorFactory.fromAsset("arrow.xml"))
            )
        }
    }

    fun showContinents(view: View){
        mMap.clear()
        for (contient in continentsGson ){
            mMap.addMarker(
                    MarkerOptions()
                            .position(LatLng(contient.continentInfo.lat?:0.0, contient.continentInfo.long?:0.0))
                            .title("${contient.nombre},  Tests: ${contient.tests}")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                    //.icon(BitmapDescriptorFactory.fromAsset("arrow.xml"))
            )
        }
    }

    fun showDeaths(view: View){
        mMap.clear()
        val sortedDeaths = data.sortedByDescending { it.defunciones }
        //Log.i("sortedList", "Total lista: ${sortedDeaths.size}, ${sortedDeaths.size-10}")
        val top = sortedDeaths.dropLast(sortedDeaths.size - 10)

        for (pais in top){
            mMap.addMarker(
                    MarkerOptions()
                            .position(LatLng(pais.latitude, pais.longitude))
                            .title("${pais.nombre}, muertes: ${pais.defunciones}")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_foreground))

            )
        }
    }

    fun showCases(view: View){
        mMap.clear()
        val sortedCases = data.sortedByDescending { it.casos }
        //Log.i("sortedList", "Total lista: ${sortedCases.size}, ${sortedCases.size-10}")
        val top = sortedCases.dropLast(sortedCases.size - 10)

        for (pais in top){
            mMap.addMarker(
                    MarkerOptions()
                            .position(LatLng(pais.latitude, pais.longitude))
                            .title("${pais.nombre}, casos: ${pais.casos}")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow))

            )
        }
    }

    fun showTests(view: View){
        mMap.clear()
        val sortedTests = data.sortedByDescending { it.tests }
        //Log.i("sortedList", "Total lista: ${sortedTests.size}, ${sortedTests.size-10}")
        val top = sortedTests.dropLast(sortedTests.size - 10)

        for (pais in top){
            mMap.addMarker(
                    MarkerOptions()
                            .position(LatLng(pais.latitude, pais.longitude))
                            .title("${pais.nombre}, tests: ${pais.tests}")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_foreground))

            )
        }
    }

    private val data = mutableListOf<Pais>()

    // Volley
    fun cargaDatos(){
        val requestQueue = Volley.newRequestQueue(this)
        val peticion = JsonArrayRequest(Request.Method.GET, url, null, Response.Listener {

            val jsonArray = it

            for (i in 0 until jsonArray.length()){
                val pais = jsonArray.getJSONObject(i)
                val nombre = pais.getString("country")
                val countryInfoData = pais.getJSONObject("countryInfo")
                val latitude = countryInfoData.getDouble("lat")
                val longitude = countryInfoData.getDouble("long")
                val casos = pais.getDouble("cases")
                val recuperdos = pais.getDouble("recovered")
                val defunciones = pais.getDouble("deaths")
                val tests = pais.getDouble("tests")

                val paisObject = Pais(nombre,latitude, longitude, casos, recuperdos, defunciones, tests)
                data.add(paisObject)
            }
        }, Response.ErrorListener {

        })
        requestQueue.add(peticion)
    }

    private fun getRetrofit():Retrofit{
        return Retrofit.Builder()
                .baseUrl("https://disease.sh/v3/covid-19/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    private lateinit var paisesGson: ArrayList<PaisJson>
    private fun getContries(){
        val call = getRetrofit().create(APIService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val response = call.getCountries()
            runOnUiThread {
                paisesGson = response.body() as ArrayList<PaisJson>
                if (response.isSuccessful){
                    Toast.makeText(applicationContext, "Datos obtenidos", Toast.LENGTH_LONG).show()
                } else{
                    Toast.makeText(applicationContext, "Error!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private lateinit var continentsGson: ArrayList<ContinenteGson>
    private fun getContinents(){
        val call = getRetrofit().create(APIService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val response = call.getContinents()
            runOnUiThread {
                continentsGson = response.body() as ArrayList<ContinenteGson>
                if (response.isSuccessful){
                    Toast.makeText(applicationContext, "Continentes obtenidos", Toast.LENGTH_LONG).show()
                } else{
                    Toast.makeText(applicationContext, "Error!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}