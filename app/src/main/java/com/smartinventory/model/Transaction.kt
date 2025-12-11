package com.smartinventory.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Transaction(
    // ID Transaksi
    var transactionId: String = "",

    // ID Produk yang dibeli (Untuk referensi pengurangan stok)
    var productId: String = "",

    // Nama Barang (Snapshot saat beli, jaga-jaga kalau nama asli berubah)
    @get:PropertyName("productName")
    @set:PropertyName("productName")
    var productName: String = "",

    // SESUAI SRS: qty (Bukan jumlahBeli)
    @get:PropertyName("qty")
    @set:PropertyName("qty")
    var qty: Int = 0,

    // SESUAI SRS: totalAmount (Bukan totalHarga)
    @get:PropertyName("totalAmount")
    @set:PropertyName("totalAmount")
    var totalAmount: Double = 0.0,

    // SESUAI SRS: userId (Bukan storeId) -- WAJIB ADA
    @get:PropertyName("userId")
    @set:PropertyName("userId")
    var userId: String = "",

    // SESUAI SRS: transactionDate (Bukan tanggal)
    @ServerTimestamp
    @get:PropertyName("transactionDate")
    @set:PropertyName("transactionDate")
    var transactionDate: Date? = null
)