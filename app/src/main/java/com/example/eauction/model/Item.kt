package com.example.eauction.model

data class Item(
    val title: String ?= null,
    val description: String ?= null,
    val price: Double  ?= null,
    val startDate: String ?= null,
    val endDate: String ?= null,
    //val imageUrl: String = ""
)
