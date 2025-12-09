package com.smartinventory.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.firebase.Timestamp

@Parcelize // 1. Tambahkan ini
data class Product(
    var productId: String = "",
    var namaBarang: String = "",
    var harga: Double = 0.0,
    var stok: Int = 0,
    var supplier: String = "",
    var storeId: String = "",
    var tglTerakhirUpdate: Timestamp? = null
) : Parcelable // 2. Tambahkan ini
