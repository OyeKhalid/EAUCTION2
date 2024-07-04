package com.example.eauction

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eauction.persistence.PreferencesManager
import com.example.eauction.adapter.AuctionAdapter
import com.example.eauction.databinding.ActivityMainBinding
import com.example.eauction.model.Auction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        preferencesManager = PreferencesManager(this)

        val userData = preferencesManager.getUserData()
        val userType = userData["userType"]

        if (userType == "Seller") {
            binding.createButton.visibility = View.VISIBLE
        } else {
            binding.createButton.visibility = View.GONE
        }

        binding.createButton.setOnClickListener {
            val intent = Intent(this, CreateAuctionActivity::class.java)
            startActivity(intent)
        }

        binding.auctionRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.logout.setOnClickListener {
            preferencesManager.clearUserData()
            Intent(this, LoginActivity::class.java).apply {
                startActivity(this)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        val userData = preferencesManager.getUserData()
        val userType = userData["userType"]
        loadAuctions(userType == "Seller")
    }

    private fun loadAuctions(isSeller: Boolean) {
        val auctionList = mutableListOf<Auction>()
        firestore.collection("auctions").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val auction = document.toObject(Auction::class.java)
                auctionList.add(auction)
            }
            val adapter = AuctionAdapter(auctionList, isSeller)
            binding.auctionRecyclerView.adapter = adapter
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error getting documents: $exception", Toast.LENGTH_SHORT).show()
        }
    }
}
