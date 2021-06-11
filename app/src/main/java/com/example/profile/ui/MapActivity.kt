package com.example.profile.ui

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.core.profile.R
import com.example.profile.utility.GPSUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_select_address.*
import java.io.IOException


class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener,
    GoogleMap.OnMapClickListener {

    private var mMap: GoogleMap? = null
    private var currentLatitude: Double = 0.toDouble()
    private var currentLongitude: Double = 0.toDouble()
    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()
    val UPDATE_LOCATION_CODE = 110

    override fun onMapClick(p0: LatLng?) {
        mMap?.clear()
        mMap?.addMarker(MarkerOptions().position(p0!!))
        getAdddressFromLocation(p0)
        Log.e("latlan", p0.toString())
    }

    override fun onMarkerDragEnd(p0: Marker?) {
        mMap?.animateCamera(CameraUpdateFactory.newLatLng(p0?.position))
        getAdddressFromLocation(p0?.position)
    }

    override fun onMarkerDragStart(p0: Marker?) {
    }

    override fun onMarkerDrag(p0: Marker?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_select_address)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        selectAddresssTextView?.setOnClickListener {
            submitLocation()
        }
        locationSearch?.setOnClickListener {
            searchLocation()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.setOnMarkerDragListener(this)
        mMap?.setOnMapClickListener(this)
        if (LoginActivity.isLogin) {
            selectAddresssTextView.text = "Edit Profile"
            var latLngStr = LoginActivity.userModel.latlng?.replace("lat/lng: ", " ")
            latLngStr = latLngStr?.replace("(", " ")
            latLngStr = latLngStr?.replace(")", " ")
            val latlong = latLngStr?.trim()?.split(",")?.toTypedArray()
            val latitude = latlong?.get(0).toString().trim().toDouble()
            val longitude = latlong?.get(1).toString().trim().toDouble()
            val DEFAULT_ZOOM = 14f
            val cameraPosition = CameraPosition.Builder().target(LatLng(latitude, longitude)).zoom(DEFAULT_ZOOM).tilt(0F).build()
            mMap?.clear()
            mMap?.addMarker(MarkerOptions().position(LatLng(latitude, longitude)))
            mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            getAdddressFromLocation(LatLng(latitude, longitude))
        } else {
            setLocation()
        }

    }

    private fun setLocation() {
        val gpsTracker = GPSUtils(this)
        if (gpsTracker.canGetLocation()) {
            currentLatitude = gpsTracker.latitude
            currentLongitude = gpsTracker.longitude
            setMarker()
            val position = LatLng(gpsTracker.latitude, gpsTracker.longitude)
            getAdddressFromLocation(position)
        } else {
            gpsTracker.showSettingsAlert()
        }
    }

    fun searchLocation() {
        lateinit var location: String
        location = addressTextView.text.toString()
        var addressList: List<Address>? = null

        if (location == "") {
            Toast.makeText(applicationContext, "provide location", Toast.LENGTH_SHORT).show()
        } else {
            val geoCoder = Geocoder(this)
            try {
                addressList = geoCoder.getFromLocationName(location, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }
           if(addressList?.size!! > 0)
           {
               val address = addressList[0]
               val latLng = LatLng(address.latitude, address.longitude)
               currentLatitude = address.latitude
               currentLongitude = address.longitude
               mMap?.clear()
               val DEFAULT_ZOOM = 14f
               val cameraPosition = CameraPosition.Builder().target(
                   LatLng(
                       currentLatitude,
                       currentLongitude
                   )
               )
                   .zoom(DEFAULT_ZOOM)
                   .tilt(0F)
                   .build()
               mMap?.addMarker(MarkerOptions().position(latLng))
               mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
               getAdddressFromLocation(latLng)
           }
            else
           {
               Toast.makeText(applicationContext, "Location Not Found, Try other location", Toast.LENGTH_SHORT).show()
           }
        }
    }

    private fun setMarker() {
        val DEFAULT_ZOOM = 14f
        val cameraPosition = CameraPosition.Builder().target(
            LatLng(
                currentLatitude,
                currentLongitude
            )
        )
            .zoom(DEFAULT_ZOOM)
            .tilt(0F)
            .build()
        mMap?.addMarker(
            MarkerOptions().position(LatLng(currentLatitude, currentLongitude)).draggable(
                true
            )
        )
        mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        enableFeatures()
    }

    private fun enableFeatures() {
        mMap?.uiSettings?.isZoomControlsEnabled = true
        mMap?.uiSettings?.isIndoorLevelPickerEnabled = true
        mMap?.uiSettings?.isScrollGesturesEnabled = true
        mMap?.uiSettings?.isRotateGesturesEnabled = true
        mMap?.isBuildingsEnabled = true
    }

    private fun getAdddressFromLocation(position: LatLng?) {
        latitude = position?.latitude ?: 0.0
        longitude = position?.longitude ?: 0.0
        var addressList: List<Address>? = null
        val geocoder = Geocoder(this)
        try {
            addressList = geocoder.getFromLocation(position?.latitude!!, position.longitude, 1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (addressList?.size != 0) {
            val address = addressList!![0]
            addressTextView?.setText(address.getAddressLine(0))
            //addressTextView?.text= String.format(getString(R.string.address_format),address.featureName,address.locality,address.adminArea,address.countryName)
        }
    }

    fun submitLocation() {
       if(LoginActivity.isLogin)
       {
           LoginActivity.userModel.address = addressTextView.text.toString()
           LoginActivity.userModel.latlng = LatLng(latitude, longitude).toString()
           val intent = Intent(this, RegistrationActivity::class.java)
           startActivity(intent)
           finish()
       }
        else
       {
           val intent = Intent(this, RegistrationActivity::class.java)
           intent.putExtra("Latitude", latitude)
           intent.putExtra("Longitude", longitude)
           setResult(UPDATE_LOCATION_CODE, intent)
           finish()
       }
    }


}