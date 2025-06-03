package com.example.snapcash.data

data class Category(
    val id: String,
    val nama: String,
    val isPengeluaran: Boolean,
    val userId: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)