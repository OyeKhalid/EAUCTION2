package com.example.eauction

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eauction.databinding.ActivityAddProductBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.util.UUID

class AddProduct : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage
    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.browseImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        binding.Submit.setOnClickListener {
            uploadData()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            binding.productImage.setImageURI(imageUri)
        }
    }

    private fun uploadData() {
        val title = binding.productTitle.text.toString()
        val description = binding.productDesc.text.toString()
        val price = binding.productPrice.text.toString().toDouble()
        val endDate = binding.endDate.text.toString()

        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}")
        storageRef.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    saveDataToFirestore(title, description, price, endDate, imageUrl)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveDataToFirestore(
        title: String,
        description: String,
        price: Double,
        endDate: String,
        imageUrl: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val productMap = hashMapOf(
            "title" to title,
            "description" to description,
            "price" to price,
            "endDate" to endDate,
            "image" to imageUrl
        )

        db.collection("products")
            .add(productMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Data successfully uploaded", Toast.LENGTH_SHORT).show()
                binding.productImage.setImageResource(R.drawable.baseline_image_24)
                binding.productTitle.text.clear()
                binding.productDesc.text.clear()
                binding.productPrice.text.clear()
                binding.endDate.text.clear()
                startActivity(Intent(this,MainActivity::class.java))
            }
            .addOnFailureListener {
                Toast.makeText(this, "Data upload failed", Toast.LENGTH_SHORT).show()
            }
    }

}
