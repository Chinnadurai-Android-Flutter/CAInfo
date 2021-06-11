package com.example.profile.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.core.profile.R
import com.example.profile.datamodel.Model
import com.example.profile.istener.OnItemClickListener
import kotlinx.android.synthetic.main.user_list.view.*

class UserListAdapter (private var list: ArrayList<Model.User>, private var onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.user_list, parent, false)
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = list[position]
            holder.itemView.name.text = item.name
            holder.itemView.mobile.text = item.mobile
            holder.itemView.address.text = item.address
            holder.itemView.userCard.setOnClickListener {
                onItemClickListener.onItemSelected(item, position)
            }
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            init {
                itemView.address
                itemView.name
                itemView.mobile
                itemView.userCard
            }
        }
    }