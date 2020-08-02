package com.locationTraker.ui

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.FirebaseDatabase
import com.google.maps.android.ui.IconGenerator
import com.locationTraker.LocationUtils
import com.locationTraker.R
import com.locationTraker.db.LocationDatabase
import com.locationTraker.utils.DirectionsJSONParser
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


open class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private val TAG = "MapsActivity"

    var mMap: GoogleMap? = null
    private var db: LocationDatabase? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


//        val locationUtils =
//            LocationUtils(this@MapsActivity, object : LocationUtils.OnLocationUpdateListener {
//                override fun onUpdate(latitude: Double, longitude: Double) {
//                    //createMarker(latitude, longitude)
//
//
//                    Toast.makeText(
//                        applicationContext,
//                        "" + latitude + "-- " + longitude,
//                        Toast.LENGTH_LONG
//                    ).show()
//
//                    mMap?.animateCamera(
//                        CameraUpdateFactory.newLatLngZoom(
//                            LatLng(
//                                latitude, longitude
//                            ), 12.0f
//                        )
//                    )
//
//                }
//            })
//        locationUtils.getLastLocation()
    }

    private fun createMarker(latitude: Double, longitude: Double): Marker {
        val iconGenerator = IconGenerator(applicationContext)
        val imageView = ImageView(applicationContext)
        val markerWidth = 100
        val markerHeight = 100
        imageView.layoutParams = ViewGroup.LayoutParams(markerWidth, markerHeight)
        iconGenerator.setContentView(imageView)
//        iconGenerator.setBackground(
//            ContextCompat.getDrawable(
//                this@MapsActivity,
//                R.drawable.transparent_bg
//            )
//        )

        val markerOptions = MarkerOptions()
        markerOptions.position(LatLng(latitude, longitude))

        imageView.setImageResource(R.drawable.ic_marker)

        val bitmap = iconGenerator.makeIcon()
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        val marker: Marker? = mMap?.addMarker(markerOptions)
        return marker!!
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {

        Log.i(TAG, "onMapReady")


        mMap = googleMap


        updateLocationUI()

        db = LocationDatabase(this@MapsActivity)

//        val locationData = getDataFromDb()

        val locationData: ArrayList<LocationData> = ArrayList();

        locationData.add(LocationData("27.6757747","85.2756031"));
        locationData.add(LocationData("27.6763222","85.2756858"));
        locationData.add(LocationData("27.6757912","85.2760778"));
        locationData.add(LocationData("27.6755202","85.2762818"));
        locationData.add(LocationData("27.6751332","85.2765658"));

        if (locationData != null) {

            val points = ArrayList<LatLng>();
            val lineOptions: PolylineOptions? = PolylineOptions();
            val markerOptions: MarkerOptions = MarkerOptions();


            for (locationDatum in locationData) {
                Log.i(TAG, "lat: " + locationDatum.latitude + " long:" + locationDatum.longitude)

                if (locationDatum.latitude.isNotEmpty() && locationDatum.longitude.isNotEmpty()) {
                    createMarker(
                        locationDatum.latitude.toDouble(),
                        locationDatum.longitude.toDouble()
                    )

                    val location = LatLng(
                        locationDatum.latitude.toDouble(),
                        locationDatum.longitude.toDouble()
                    )

                    mMap?.moveCamera(CameraUpdateFactory.newLatLng(location))

                    val position: LatLng = LatLng(
                        locationDatum.latitude.toDouble(),
                        locationDatum.longitude.toDouble()
                    );
                    points.add(position);

                }
            }


            lineOptions?.addAll(points);
            lineOptions?.width(12F);
            lineOptions?.color(Color.RED);
            lineOptions?.geodesic(true);

            mMap?.addPolyline(lineOptions);

            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("users")


        /*// Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
             override fun onDataChange(dataSnapshot: DataSnapshot) {
                 // This method is called once with the initial value and again
                 // whenever data at this location is updated.
//                    val value =
//                        dataSnapshot.getValue(String::class.java)!!
//                    Log.d(TAG, "Value is: $value")
             }

             override fun onCancelled(error: DatabaseError) {
                 // Failed to read value
                 Log.w(TAG, "Failed to read value.", error.toException())
             }
         })
         */


        }
    }


    fun updateLocationUI() {
        try {
            mMap?.isMyLocationEnabled = true
            mMap?.uiSettings?.isMyLocationButtonEnabled = true

        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getDataFromDb(): ArrayList<LocationData>? {
        val data = ArrayList<LocationData>()
        val cursor = db!!.allLog
        cursor.moveToFirst()



        for (i in 0 until cursor.count) {

            val longitude = cursor.getString(cursor.getColumnIndex(LocationDatabase.LONGITUDE))
            val latitude = cursor.getString(cursor.getColumnIndex(LocationDatabase.LATITUDE))

            var origin: LatLng? = null
            var dest: LatLng? = null

            if (origin != null && dest != null) {

                if (i == 0) {
                    origin = LatLng(latitude.toDouble(), longitude.toDouble())
                }

                if (i == cursor.count - 1) {
                    dest = LatLng(latitude.toDouble(), longitude.toDouble())
                }

                val url = getDirectionsUrl(origin!!, dest!!)

                data.add(LocationData(longitude, latitude))


                // Getting URL to the Google Directions API

                val downloadTask = DownloadTask(mMap!!)

                // Start downloading json data from Google Directions API
                downloadTask.execute(url)
            }

            cursor.moveToNext()


        }
        return data
    }

    internal class LocationData(
        var latitude: String,
        var longitude: String
    )

    internal class LocationDataDouble(
        var latitude: Double,
        var longitude: Double

    )


//    private class DownloadTask : AsyncTask<String?, Void?, String>() {
//        protected override fun doInBackground(vararg url: String): String {
//            var data = ""
//            try {
//                data = downloadUrl(url[0])
//            } catch (e: Exception) {
//                Log.d("Background Task", e.toString())
//            }
//            return data
//        }
//
//        override fun onPostExecute(result: String) {
//            super.onPostExecute(result)
//            val parserTask: com.journaldev.maproutebetweenmarkers.MapsActivity.ParserTask =
//                com.journaldev.maproutebetweenmarkers.MapsActivity.ParserTask()
//            parserTask.execute(result)
//        }
//    }


    class DownloadTask(val mMap: GoogleMap) : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg url: String?): String {
            var data = ""
            try {
                data = this.downloadUrl(url[0]!!)!!
            } catch (e: Exception) {
                Log.d("Background Task", e.toString())
            }
            return data
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val parserTask = ParserTask(mMap)
            parserTask.execute(result)
        }


        /**
         * A method to download json data from url
         */
        @Throws(IOException::class)
        fun downloadUrl(strUrl: String): String? {
            var data = ""
            var iStream: InputStream? = null
            var urlConnection: HttpURLConnection? = null
            try {
                val url = URL(strUrl)
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection!!.connect()
                iStream = urlConnection!!.inputStream
                val br =
                    BufferedReader(InputStreamReader(iStream))
                val sb = StringBuffer()
                var line: String? = ""
                while (br.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                data = sb.toString()
                br.close()
            } catch (e: java.lang.Exception) {
                Log.d("Exception", e.toString())
            } finally {
                iStream!!.close()
                urlConnection!!.disconnect()
            }
            return data
        }

    }


    /**
     * A class to parse the Google Places in JSON format
     */
    class ParserTask(val mMap: GoogleMap) :
        AsyncTask<String?, Int?, List<List<HashMap<String, String>>>?>() {


        // Parsing the data in non-ui thread
        override fun doInBackground(vararg jsonData: String?): List<List<HashMap<String, String>>>? {
            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? =
                null
            try {
                jObject = JSONObject(jsonData[0])
                val parser = DirectionsJSONParser()
                routes = parser.parse(jObject)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return routes
        }


        override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {
            var points: ArrayList<LatLng>? = null
            var lineOptions: PolylineOptions? = null
            val markerOptions = MarkerOptions()
            for (i in result!!.indices) {
                points = ArrayList<LatLng>()
                lineOptions = PolylineOptions()
                val path =
                    result[i]
                for (j in path.indices) {
                    val point = path[j]
                    val lat = point["lat"]!!.toDouble()
                    val lng = point["lng"]!!.toDouble()
                    val position =
                        LatLng(lat, lng)
                    points.add(position)
                }
                lineOptions.addAll(points)
                lineOptions.width(12f)
                lineOptions.color(Color.RED)
                lineOptions.geodesic(true)
            }

// Drawing polyline in the Google Map for the i-th route

            mMap.addPolyline(lineOptions)
        }


    }


    open fun getDirectionsUrl(
        origin: LatLng,
        dest: LatLng
    ): String? {

        // Origin of route
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude

        // Destination of route
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude

        // Sensor enabled
        val sensor = "sensor=false"
        val mode = "mode=driving"
        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$sensor&$mode"

        // Output format
        val output = "json"


        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    }

    override fun onMapLoaded() {

        Log.i(TAG, "onMapLoaded")


    }


}
