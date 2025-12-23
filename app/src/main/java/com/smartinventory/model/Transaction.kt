package com.smartinventory.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// CLASS 1: Untuk menampung detail barang (Baris-baris dalam Tabel Kasir)
data class TransactionItem(
    @get:PropertyName("productId")
    @set:PropertyName("productId")
    var productId: String = "",

    @get:PropertyName("productName")
    @set:PropertyName("productName")
    var productName: String = "",

    @get:PropertyName("qty")
    @set:PropertyName("qty")
    var qty: Int = 0,

    @get:PropertyName("price")
    @set:PropertyName("price")
    var price: Double = 0.0,

    @get:PropertyName("subtotal")
    @set:PropertyName("subtotal")
    var subtotal: Double = 0.0
)

// CLASS 2: Untuk menampung Kepala Transaksi (Totalan)
data class Transaction(
    var transactionId: String = "",

    @get:PropertyName("userId")
    @set:PropertyName("userId")
    var userId: String = "",

    @get:PropertyName("totalAmount")
    @set:PropertyName("totalAmount")
    var totalAmount: Double = 0.0,

    @ServerTimestamp
    @get:PropertyName("transactionDate")
    @set:PropertyName("transactionDate")
    var transactionDate: Date? = null,

    // INI KUNCINYA: List/Array untuk menampung BANYAK barang
    @get:PropertyName("items")
    @set:PropertyName("items")
    var items: List<TransactionItem> = emptyList()
)