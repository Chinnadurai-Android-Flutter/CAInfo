package com.example.profile.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.core.profile.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val PERMISSION_REQUEST_LOCATION = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        customerButton.setOnClickListener {
            LoginActivity.isLogin = false
            checkLocationPermission("customer");
        }
        adminButton.setOnClickListener {
            LoginActivity.isLogin = false
            checkLocationPermission("admin");
        }
    }

    fun checkLocationPermission(role:String) {
        try {
            // Check location permission
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestLocationPermission()
            } else {
                onPermissionGranted(PERMISSION_REQUEST_LOCATION, role)
            }
        } catch (e: Exception) {
            Log.e("", e.localizedMessage.toString())
        }
    }


    fun onPermissionGranted(requestCode: Int, role:String) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {

           if(role == "customer")
           {
               val intent = Intent(applicationContext, LoginActivity::class.java)
               startActivity(intent)
           }
           else
           {
               val intent = Intent(applicationContext, AdminHomeScreen::class.java)
               startActivity(intent)
           }
        }
    }

    fun onPermissionDenied(requestCode: Int) {

    }

    private fun requestLocationPermission() {

        ActivityCompat.requestPermissions(
            this, arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_REQUEST_LOCATION
        )
    }
}