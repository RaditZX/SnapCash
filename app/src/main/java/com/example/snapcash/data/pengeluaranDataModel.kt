package com.example.snapcash.data

data class Barang(
    val nama: String,
    val kategori: String,
    val jumlah: Int,
    val harga: Double
)

data class Tambahanbiaya(
    val namabiaya: String,
    val jumlahbiaya: Double
)
