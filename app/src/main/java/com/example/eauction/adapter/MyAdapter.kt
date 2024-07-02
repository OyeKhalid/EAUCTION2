package com.example.eauction.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.eauction.MainActivity
import com.example.eauction.R
import com.example.eauction.model.Item
import com.google.firebase.appcheck.internal.util.Logger.TAG
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class MyAdapter(private val itemList : ArrayList<Item>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {
        val item : Item = itemList[position]

        holder.product_title.text = item.title
        holder.product_desc.text = item.description
        holder.product_price.text = item.price.toString()
        holder.endDate.text = item.endDate
        Picasso.get()
            .load(item.imageUrl)
            .into(holder.image, object : Callback {
                override fun onSuccess() {
                    Log.d(TAG, "Image loaded successfully for ${item.imageUrl}")
                }

                override fun onError(e: Exception?) {
                    Log.e(TAG, "Error loading image for ${item.imageUrl}", e)
                }
            })
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val product_title : TextView = itemView.findViewById(R.id.titleProduct)
        val product_desc : TextView = itemView.findViewById(R.id.descProduct)
        val product_price : TextView = itemView.findViewById(R.id.priceProduct)
        val endDate : TextView = itemView.findViewById(R.id.endDate)
        val image : ImageView = itemView.findViewById(R.id.imageProduct)

    }

}

