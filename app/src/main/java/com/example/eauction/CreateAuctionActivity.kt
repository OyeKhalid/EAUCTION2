package com.example.eauction

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.eauction.databinding.ActivityCreateAuctionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class CreateAuctionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateAuctionBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var progressDialog: ProgressDialog
    private var imageUrl: String? = null

    private var startDate: Long = 0L
    private var endDate: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAuctionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        setupProgressDialog()

        val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                progressDialog.setMessage("Uploading Image...")
                progressDialog.show()
                uploadImageToStorage(it)
                Glide.with(this).load(it).into(binding.imageView)
            }
        }

        binding.uploadImageButton.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        binding.startDateTextView.setOnClickListener {
            showDatePicker { date ->
                startDate = date
                binding.startDateTextView.text = formatDate(date)
            }
        }

        binding.endDateTextView.setOnClickListener {
            showDatePicker { date ->
                endDate = date
                binding.endDateTextView.text = formatDate(date)
            }
        }

        binding.saveButton.setOnClickListener {
            val title = binding.titleEt.text.toString()
            val description = binding.descriptionEt.text.toString()
            val startingBid = binding.startingBidEt.text.toString().toDoubleOrNull()

            if (title.isNotEmpty() && description.isNotEmpty() && startDate != 0L && endDate != 0L && startingBid != null && imageUrl != null) {
                if (startDate >= System.currentTimeMillis()) {
                    val auctionData = hashMapOf(
                        "title" to title,
                        "description" to description,
                        "startDate" to startDate,
                        "endDate" to endDate,
                        "startingBid" to startingBid,
                        "currentBid" to startingBid,
                        "sellerId" to firebaseAuth.currentUser?.uid,
                        "imageUrl" to imageUrl,
                        "timestamp" to System.currentTimeMillis()
                    )

                    progressDialog.setMessage("Saving Auction...")
                    progressDialog.show()
                    firestore.collection("auctions").add(auctionData).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val auctionId = it.result?.id
                            if (auctionId != null) {
                                firestore.collection("auctions").document(auctionId).update("id", auctionId)
                                    .addOnSuccessListener {
                                        progressDialog.dismiss()
                                        Toast.makeText(this, "Auction created successfully", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        progressDialog.dismiss()
                                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                progressDialog.dismiss()
                                Toast.makeText(this, "Error: Auction ID is null", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Error: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Start date cannot be below current date", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageToStorage(uri: Uri) {
        val storageRef = storage.reference.child("images/${UUID.randomUUID()}")
        storageRef.putFile(uri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                imageUrl = downloadUri.toString()
                progressDialog.dismiss()
                Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(this, "Image upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCancelable(false)
    }

    private fun showDatePicker(onDateSet: (Long) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSet(calendar.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun formatDate(dateInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis
        return "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
    }
}
