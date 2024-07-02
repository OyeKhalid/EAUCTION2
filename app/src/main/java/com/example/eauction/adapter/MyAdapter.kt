package com.example.eauction.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eauction.R
import com.example.eauction.model.Item
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
        //val item : Item = itemList[position]
        holder.product_title.text = itemList.get(position).toString()
        holder.product_desc.text = itemList.get(position).toString()
        holder.product_price.text = itemList.get(position).toString()
        holder.endDate.text = itemList.get(position).toString()
//        holder..text = itemList.get(position).toString()

//        holder.product_title.text = item.title
//        holder.product_desc.text = item.description
//        holder.product_price.text = item.price.toString()
//        holder.endDate.text = item.endDate
//        //Picasso.get().load(item.imageUrl).into(holder.image)
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val product_title : TextView = itemView.findViewById(R.id.product_title)
        val product_desc : TextView = itemView.findViewById(R.id.product_desc)
        val product_price : TextView = itemView.findViewById(R.id.product_price)
        val endDate : TextView = itemView.findViewById(R.id.endDate)
        //val image : TextView = itemView.findViewById(R.id.product_image)

    }

}

