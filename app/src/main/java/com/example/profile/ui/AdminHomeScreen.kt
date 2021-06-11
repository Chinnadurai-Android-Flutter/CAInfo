package com.example.profile.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.core.profile.R
import com.example.profile.datamodel.Model
import com.example.profile.istener.OnItemClickListener
import com.example.profile.adapter.UserListAdapter
import com.example.profile.database.DataBaseHelper
import kotlinx.android.synthetic.main.activity_admin_home_screen.*
import java.util.ArrayList

class AdminHomeScreen : AppCompatActivity(), OnItemClickListener {
    private var userList = ArrayList<Model.User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home_screen)
        val db = DataBaseHelper(applicationContext)
        val data = db.readData()
        userList.addAll(data)
        if(userList.size>0) {
            norec.visibility= View.GONE
            locationFilter.visibility= View.VISIBLE
            recyclerView.visibility = View.VISIBLE
            val adapter = UserListAdapter(userList, this);
            recyclerView?.layoutManager =
                LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)
            recyclerView?.adapter = adapter
            locationFilter.setOnClickListener {
                val intent = Intent(applicationContext, AdminFilterMapActivity::class.java)
                startActivity(intent)
            }
        }
        else
        {
            norec.visibility= View.VISIBLE
            recyclerView.visibility = View.GONE
            locationFilter.visibility = View.GONE
        }
    }

    override fun onItemSelected(item: Any?, selectedIndex: Int) {
    }
}