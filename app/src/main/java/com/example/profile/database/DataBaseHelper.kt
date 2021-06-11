package com.example.profile.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.example.profile.datamodel.Model


val DATABASENAME = "MY DATABASE"
val TABLENAME = "Users"
val COL_NAME = "name"
val COL_ID = "id"
val COL_EMAIL = "email"
val COL_PHONE = "phone"
val COL_ADDRESS = "address"
val COL_IMAGEPATH = "imagepath"
val COL_LATLNG = "latlng"
val COL_PASSWORD = "password"

class DataBaseHelper(var context: Context) : SQLiteOpenHelper(
    context, DATABASENAME, null,
    1
) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
            "CREATE TABLE " + TABLENAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_IMAGEPATH + " VARCHAR(256)," + COL_NAME + " VARCHAR(256)," + COL_EMAIL + " VARCHAR(256)," + COL_PASSWORD + " VARCHAR(256)," + COL_PHONE + " VARCHAR(256)," + COL_ADDRESS + " VARCHAR(256)," + COL_LATLNG + " VARCHAR(256))"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //onCreate(db);
    }

    fun insertData(user: Model.User) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_NAME, user.name)
        contentValues.put(COL_EMAIL, user.email)
        contentValues.put(COL_ADDRESS, user.address)
        contentValues.put(COL_PHONE, user.mobile)
        contentValues.put(COL_IMAGEPATH, user.imagePath)
        contentValues.put(COL_LATLNG, user.latlng)
        contentValues.put(COL_PASSWORD, user.password)
        val result = database.insert(TABLENAME, null, contentValues)
        if (result == (0).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }

    fun readData(): MutableList<Model.User> {
        val list: MutableList<Model.User> = ArrayList()
        val db = this.readableDatabase
        val query = "Select * from $TABLENAME"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val user = Model.User()
                user.name = result.getString(result.getColumnIndex(COL_NAME))
                user.id = result.getInt(result.getColumnIndex(COL_ID))
                user.email = result.getString(result.getColumnIndex(COL_EMAIL))
                user.password = result.getString(result.getColumnIndex(COL_PASSWORD))
                user.mobile = result.getString(result.getColumnIndex(COL_PHONE))
                user.imagePath = result.getString(result.getColumnIndex(COL_IMAGEPATH))
                user.latlng = result.getString(result.getColumnIndex(COL_LATLNG))
                user.address = result.getString(result.getColumnIndex(COL_ADDRESS))
                list.add(user)
            } while (result.moveToNext())
        }
        return list
    }
    fun updateProfile(user: Model.User) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_NAME, user.name)
        contentValues.put(COL_EMAIL, user.email)
        contentValues.put(COL_ADDRESS, user.address)
        contentValues.put(COL_PHONE, user.mobile)
        contentValues.put(COL_IMAGEPATH, user.imagePath)
        contentValues.put(COL_LATLNG, user.latlng)
        contentValues.put(COL_PASSWORD, user.password)
        val result = db.update(TABLENAME, contentValues, COL_ID + "=?", arrayOf(user.id.toString())).toLong()
        db.close()
        if (result == (0).toLong()) {
            Toast.makeText(context, "Profile Update Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Profile Update Successfully", Toast.LENGTH_SHORT).show()
        }
    }
}