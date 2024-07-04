package com.example.eauction

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eauction.persistence.PreferencesManager
import com.example.eauction.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: ActivityLoginBinding
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        preferencesManager = PreferencesManager(this)

        setupProgressDialog()

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                progressDialog.show()
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        user?.let {
                            firestore.collection("users").document(user.uid).get()
                                .addOnSuccessListener { document ->
                                    progressDialog.dismiss()
                                    if (document != null) {
                                        val firstName = document.getString("firstName") ?: ""
                                        val lastName = document.getString("lastName") ?: ""
                                        val phone = document.getString("phone") ?: ""
                                        val userType = document.getString("userType") ?: ""

                                        preferencesManager.saveUserData(
                                            firstName,
                                            lastName,
                                            email,
                                            phone,
                                            userType
                                        )

                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(this, "No such user", Toast.LENGTH_SHORT).show()
                                    }
                                }.addOnFailureListener { exception ->
                                    progressDialog.dismiss()
                                    Toast.makeText(
                                        this,
                                        "Error getting documents: $exception",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.d("E_AuctionE_Auction_Log_Log", exception.message.toString())
                                }
                        }
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        Log.d("E_AuctionE_Auction_Log_Log", it.exception.toString())
                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Logging In")
        progressDialog.setMessage("Please wait while we log you in...")
        progressDialog.setCancelable(false)
    }
}
