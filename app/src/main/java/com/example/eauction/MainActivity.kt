package com.example.eauction

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eauction.adapter.MyAdapter
import com.example.eauction.databinding.ActivityLoginBinding
import com.example.eauction.databinding.ActivityMainBinding
import com.example.eauction.model.Item
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject

class MainActivity : AppCompatActivity() {

//    private lateinit var rv : RecyclerView
//    private lateinit var myAdapter : MyAdapter
//    private lateinit var itemArrayList: ArrayList<Item>

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        val userId = firebaseAuth.currentUser!!.uid
        val ref = firebaseFirestore.collection("products").document()
        ref.get()
            .addOnSuccessListener {
                if(it != null){
                    val title = it.data?.get("title")?.toString()
                    val desc = it.data?.get("description")?.toString()
                    val price = it.data?.get("price")?.toString()
                    val endate = it.data?.get("endDate")?.toString()

                    binding.title.setText(title)
                    binding.desc.setText(desc)
                    binding.price.setText(price)
                    binding.date.setText(endate)

                    Toast.makeText(this,"DATA IS FETCHED SUCCESSFULLY",Toast.LENGTH_SHORT).show()

                }

            }
            .addOnFailureListener {

                Toast.makeText(this, "Data Fetching Failed", Toast.LENGTH_SHORT).show()

            }


//        val floatbutton : FloatingActionButton = findViewById(R.id.floating_add)
//
//        floatbutton.setOnClickListener{
//            startActivity(Intent(this,AddProduct::class.java))
//        }

//        rv = findViewById(R.id.recyclerView)
//        rv.layoutManager = LinearLayoutManager(this)
//        rv.setHasFixedSize(true)
//
//        itemArrayList = arrayListOf()
//        myAdapter = MyAdapter(itemArrayList)
//        rv.adapter = myAdapter
//
//        EventChangeListner()
//
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    private fun EventChangeListner() {
//        db = FirebaseFirestore.getInstance()
//        db.collection("products")
//            .get()w
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    val item = document.toObject(Item::class.java)
//                    itemArrayList.add(item)
//                }
//                myAdapter.notifyDataSetChanged()
//            }
//            .addOnFailureListener { exception ->
//                // Handle the error
//            }
    }
}