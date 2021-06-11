package com.example.profile.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.core.profile.R

import com.example.profile.database.DataBaseHelper
import com.example.profile.datamodel.Model
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginActivity : AppCompatActivity() {

    companion object {
        var userModel = Model.User()
        var isLogin : Boolean = false
    }

    private var userList = ArrayList<Model.User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val db = DataBaseHelper(applicationContext)
        regButton.setOnClickListener {
            val intent = Intent(applicationContext, RegistrationActivity::class.java)
            startActivity(intent)
            finish()
        }
        loginButton.setOnClickListener {
            if (loginValidation() == true) {
                var isValidUser = false
                val data = db.readData()
                userList.addAll(data)
                for (item in 0 until userList.size) {
                    if (username.text.toString() == userList[item].name && password.text.toString() == userList[item].password) {
                        isValidUser = true
                        userModel = userList[item]
                        isLogin = true
                        val intent = Intent(applicationContext, MapActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(
                            applicationContext,
                            "Logined Successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        break
                    }
                }
                if (!isValidUser) {
                    Toast.makeText(
                        applicationContext,
                        "Incorrect username and password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else
            {
                Toast.makeText(
                    applicationContext,
                    "Incorrect username and password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loginValidation(): Boolean? {
        var isValid: Boolean? = true
        when {
            username.text.toString().isEmpty() -> {
                username.error = "This field is requried"
                isValid = false;
            }
        }

        when {
            password.text.toString().isEmpty() -> {
                password.error = "This field is requried"
                isValid = false;
            }
        }
        return isValid
    }
}