package com.example.eauction.adapter

import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eauction.AuctionDetailActivity
import com.example.eauction.R
import com.example.eauction.model.Auction
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class AuctionAdapter(private val auctions: List<Auction>, private val isSeller: Boolean) :
    RecyclerView.Adapter<AuctionAdapter.AuctionViewHolder>() {

    class AuctionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val currentBidTextView: TextView = itemView.findViewById(R.id.currentBidTextView)
        val timeLeftTextView: TextView = itemView.findViewById(R.id.timeLeftTextView)
        val addBidButton: Button = itemView.findViewById(R.id.addBidButton)
        val viewDetailsButton: Button = itemView.findViewById(R.id.viewDetailsButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuctionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_auction, parent, false)
        return AuctionViewHolder(view)
    }

    override fun onBindViewHolder(holder: AuctionViewHolder, position: Int) {
        val auction = auctions[position]
        holder.titleTextView.text = auction.title
        holder.descriptionTextView.text = auction.description
        Glide.with(holder.itemView.context).load(auction.imageUrl).into(holder.imageView)
        holder.currentBidTextView.text = "Current Bid: ${auction.currentBid}"
        startCountdown(holder.timeLeftTextView, auction.endDate)

        if (isSeller) {
            holder.addBidButton.visibility = View.GONE
        } else {
            holder.addBidButton.visibility = View.VISIBLE
        }

        holder.viewDetailsButton.setOnClickListener {
            Intent(holder.viewDetailsButton.context, AuctionDetailActivity::class.java).apply {
                this.putExtra("AUCTION_ID", auction.id)
                holder.viewDetailsButton.context.startActivity(this)
            }
        }

        holder.addBidButton.setOnClickListener {
            showBidDialog(holder, auction)
        }
    }

    override fun getItemCount(): Int {
        return auctions.size
    }

    private fun startCountdown(textView: TextView, endDateMillis: Long) {
        val timeLeft = endDateMillis - System.currentTimeMillis()
        object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                textView.text = String.format("%02dh %02dm %02ds", hours, minutes, seconds)
            }

            override fun onFinish() {
                textView.text = "Auction ended"
            }
        }.start()
    }


    private fun showBidDialog(holder: AuctionViewHolder, auction: Auction) {
        val context = holder.itemView.context
        val builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Place a Bid")

        val input = android.widget.EditText(context)
        input.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        builder.setView(input)

        builder.setPositiveButton("Submit") { dialog, _ ->
            val newBid = input.text.toString().toDoubleOrNull()
            if (newBid != null && newBid > auction.currentBid) {
                updateBid(auction.id, newBid, holder.currentBidTextView)
            } else {
                Toast.makeText(context, "Bid must be greater than current bid", Toast.LENGTH_SHORT)
                    .show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun updateBid(auctionId: String, newBid: Double, currentBidTextView: TextView) {
        val firestore = FirebaseFirestore.getInstance()
        Log.d("AuctionAdapter", "Updating bid for auctionId: $auctionId")

        if (auctionId.isEmpty()) {
            Log.e("AuctionAdapter", "Error: auctionId is empty")
            Toast.makeText(
                currentBidTextView.context,
                "Error: Auction ID is invalid",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        firestore.collection("auctions").document(auctionId).update("currentBid", newBid)
            .addOnSuccessListener {
                currentBidTextView.text = "Current Bid: $newBid"
                Toast.makeText(
                    currentBidTextView.context,
                    "Bid updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Log.e("AuctionAdapter", "Error updating bid: ${e.message}")
                Toast.makeText(
                    currentBidTextView.context,
                    "Error updating bid: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

}


