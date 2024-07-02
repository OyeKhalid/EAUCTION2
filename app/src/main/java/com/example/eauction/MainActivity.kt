package com.example.eauction

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eauction.adapter.MyAdapter
import com.example.eauction.databinding.ActivityMainBinding
import com.example.eauction.model.Item
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var myAdapter: MyAdapter
    private lateinit var items: ArrayList<Item>

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        binding.floatingAdd.setOnClickListener {
            startActivity(Intent(this, AddProduct::class.java))
        }

        FirebaseApp.initializeApp(this)

        items = ArrayList()

        rv = findViewById(R.id.recyclerView)
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(this)
        myAdapter = MyAdapter(items)
        rv.adapter = myAdapter

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        progressBar = findViewById(R.id.progressBar)

        // Show the progress bar before loading data
        progressBar.visibility = View.VISIBLE

        // Use executor to fetch data in background
        executor.execute {
            fetchAllDocuments()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_out -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        firebaseAuth.signOut()
        Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun fetchAllDocuments() {
        val db = FirebaseFirestore.getInstance()
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val item = document.toObject<Item>()
                    items.add(item)
                    Log.d(TAG, "Document ID: ${document.id}")
                }

                runOnUiThread {
                    // Hide the progress bar and update RecyclerView on the main thread
                    progressBar.visibility = View.GONE
                    myAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Data Fetched Successfully", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
                runOnUiThread {
                    // Hide the progress bar and show error message on the main thread
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Data Failed To Fetch", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
