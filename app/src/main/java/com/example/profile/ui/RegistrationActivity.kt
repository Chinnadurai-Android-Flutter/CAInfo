package com.example.profile.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.core.profile.R
import com.example.profile.database.DataBaseHelper
import com.example.profile.utility.GPSUtils
import com.example.profile.datamodel.Model
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_registration.*
import java.io.IOException


class RegistrationActivity : AppCompatActivity() {
    var isProfileSelect: Boolean = false
    val UPDATE_LOCATION_CODE = 110
    var selectImage: String = ""
    private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            var selectedImageUri = data?.data
            selectedImageUri?.let {
                selectImage = selectedImageUri.toString()
                isProfileSelect = true;
                profileImage.visibility = View.VISIBLE
                Glide.with(applicationContext)
                    .load(selectedImageUri)
                    .into(profileImage)
                contentText.visibility = View.GONE
                sImgButton.text = "Change Image"
            }
        } else if (resultCode == UPDATE_LOCATION_CODE) {
            latitude = data?.extras?.getDouble("Latitude", 0.0) ?: 0.0
            longitude = data?.extras?.getDouble("Longitude", 0.0) ?: 0.0
            getAdddressFromLocation(LatLng(latitude, longitude))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        val db = DataBaseHelper(applicationContext)
        setLocation()
        regButton.setOnClickListener {
            if (regValidation()) {
                val user: Model.User = Model.User(
                    name = regUsername.text.toString(),
                    mobile = regPhone.text.toString(),
                    password = regPassword.text.toString(),
                    address = currentLocationTextview.text.toString(),
                    email = regEmail.text.toString(),
                    imagePath = selectImage,
                    latlng = LatLng(latitude, longitude).toString()
                )
               if(LoginActivity.isLogin) {
                   user.id = LoginActivity.userModel.id
                   db.updateProfile(user)
               }
                else
                   db.insertData(user)
                LoginActivity.isLogin = false
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        sImgButton.setOnClickListener {
            selectImageInAlbum()
        }
        getLocationTextView.setOnClickListener {
            val intent = Intent(applicationContext, MapActivity::class.java)
            startActivityForResult(intent, UPDATE_LOCATION_CODE)
        }
        if(LoginActivity.isLogin)
        {
            setEditProfileScreen();
        }
    }
    fun setEditProfileScreen(){
        var latLngStr = LoginActivity.userModel.latlng?.replace("lat/lng: ", " ")
        latLngStr = latLngStr?.replace("(", " ")
        latLngStr = latLngStr?.replace(")", " ")
        val latlong = latLngStr?.trim()?.split(",")?.toTypedArray()
        latitude = latlong?.get(0).toString().trim().toDouble()
        longitude = latlong?.get(1).toString().trim().toDouble()
        regButton.text = getString(R.string.update)
        val user = LoginActivity.userModel
        regUsername.setText(user.name)
        regPhone.setText(user.mobile)
        regEmail.setText(user.email)
        regPassword.setText(user.password)
        currentLocationTextview.text = user.address
        applicationContext?.let {
            selectImage = user.imagePath.toString()
            isProfileSelect = true;
            profileImage.visibility = View.VISIBLE
            Glide.with(applicationContext)
                .load(Uri.parse(user.imagePath))
                .into(profileImage)
            contentText.visibility = View.GONE
            sImgButton.text = "Change Image"
        }
        regUsername.setText(user.name)
        regUsername.setText(user.name)
    }
    private fun setLocation() {
        val gpsTracker = GPSUtils(this)
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.latitude
            longitude = gpsTracker.longitude
            val position = LatLng(gpsTracker.latitude, gpsTracker.longitude)
            getAdddressFromLocation(position)
        } else {
            gpsTracker.showSettingsAlert()
        }
    }

    private fun getAdddressFromLocation(position: LatLng?) {
        var addressList: List<Address>? = null
        val geocoder = Geocoder(this)
        try {
            addressList = geocoder.getFromLocation(position?.latitude!!, position?.longitude!!, 1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (addressList?.size ?: 0 > 0) {
            val address = addressList!![0]
            address.let {
                currentLocationTextview?.text = address.getAddressLine(0)
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    private fun regValidation(): Boolean {
        var isValid: Boolean? = false
        when {
            regUsername.text.toString().isEmpty() -> {
                regUsername.error = "This field is requried"
                isValid = false;
            }
            regUsername.text.toString().length < 3 -> {
                regUsername.error = "Username must contain 3 letter"
                isValid = false;
            }
            regUsername.text.toString().length > 3 -> {
                isValid = true;
            }
        }
        when {
            regPhone.text.toString().isEmpty() -> {
                regPhone.error = "This field is requried"
                isValid = false;
            }
            regPhone.text.toString().length < 10 -> {
                regPhone.error = "Phone Number must contain 10 number"
                isValid = false;
            }
            regPhone.text.toString().length == 10 -> {
                isValid = true;
            }
        }
        when {
            currentLocationTextview.text.toString().isEmpty() -> {
                currentLocationTextview.error = "This field is requried"
                isValid = false;
            }
            currentLocationTextview.text.toString().length > 5 -> {
                isValid = true;
            }
        }
        when {
            regPassword.text.toString().isEmpty() -> {
                regPassword.error = "This field is requried"
                isValid = false;
            }
            regPassword.text.toString().length < 5 -> {
                regPassword.error = "Password must contain more than 5 letter"
                isValid = false;
            }
            regPassword.text.toString().length > 5 -> {
                isValid = true;
            }
        }
        when {
            regEmail.text.toString().isEmpty() -> {
                regEmail.error = "This field is requried"
                isValid = false;
            }
        }
        if (isValidEmail(regEmail.text)) {
            isValid = true;
        } else {
            regEmail.error = "Incorrect Email Address Format"
        }
        isValid = if (isProfileSelect) {
            true
        } else {
            Toast.makeText(applicationContext, "Please set profile Image", Toast.LENGTH_SHORT)
            false
        }
        return isValid
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

}