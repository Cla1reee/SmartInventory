package com.smartinventory.model

import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Transaction(
    var id: String = "",

    var productId: String = "",
    var namaBarang: String = "",
    var jumlahBeli: Int = 0,
    var totalHarga: Double = 0.0,

    @get:PropertyName("storeId")
    var storeId: String = "",

    @ServerTimestamp
    var tanggal: Date? = null
)