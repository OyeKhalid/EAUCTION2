package com.example.eauction

import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.eauction.persistence.PreferencesManager
import com.example.eauction.databinding.ActivityAuctionDetailBinding
import com.example.eauction.model.Auction
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AuctionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuctionDetailBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auction: Auction
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuctionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        preferencesManager = PreferencesManager(this)

        val auctionId = intent.getStringExtra("AUCTION_ID")
        if (auctionId != null) {
            loadAuctionDetails(auctionId)
        }

        val userType = preferencesManager.getUserData()["userType"]
        if (userType == "Seller") {
            binding.addBidButton.visibility = Button.GONE
        } else {
            binding.addBidButton.visibility = Button.VISIBLE
            binding.addBidButton.setOnClickListener {
                showBidDialog()
            }
        }
    }

    private fun loadAuctionDetails(auctionId: String) {
        firestore.collection("auctions").document(auctionId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    auction = document.toObject(Auction::class.java)!!
                    displayAuctionDetails()
                    startCountdown(auction.endDate)
                } else {
                    Toast.makeText(this, "No such auction found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting auction details: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayAuctionDetails() {
        binding.titleTextView.text = auction.title
        binding.descriptionTextView.text = auction.description
        binding.currentBidTextView.text = "Current Bid: ${auction.currentBid}"
        binding.startingBidTextView.text = "Starting Bid: ${auction.startingBid}"
        Glide.with(this).load(auction.imageUrl).into(binding.imageView)

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.startDateTextView.text = "Start Date: ${sdf.format(Date(auction.startDate))}"
        binding.endDateTextView.text = "End Date: ${sdf.format(Date(auction.endDate))}"
    }

    private fun showBidDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Place a Bid")

        val input = android.widget.EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        builder.setView(input)

        builder.setPositiveButton("Submit") { dialog, _ ->
            val newBid = input.text.toString().toDoubleOrNull()
            if (newBid != null && newBid > auction.currentBid) {
                updateBid(newBid)
            } else {
                Toast.makeText(this, "Bid must be greater than current bid", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun updateBid(newBid: Double) {
        firestore.collection("auctions").document(auction.id).update("currentBid", newBid)
            .addOnSuccessListener {
                binding.currentBidTextView.text = "Current Bid: $newBid"
                Toast.makeText(this, "Bid updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating bid: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun startCountdown(endDateMillis: Long) {
        val timeLeft = endDateMillis - System.currentTimeMillis()
        object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                binding.timeLeftTextView.text = String.format("%02dh %02dm %02ds", hours, minutes, seconds)
            }

            override fun onFinish() {
                binding.timeLeftTextView.text = "Auction ended"
            }
        }.start()
    }
}
