package com.example.eauction.model

import com.google.firebase.Timestamp

data class Item(
    val title: String ?= null,
    val description: String ?= null,
    val price: Double  ?= null,
    val endDate: String ?= null,
    val imageUrl: String ?= null
)

data class Auction(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val startingBid: Double = 0.0,
    val currentBid: Double = 0.0,
    val sellerId: String = "",
    val imageUrl: String = "",
    val timestamp: Long = 0L
)
