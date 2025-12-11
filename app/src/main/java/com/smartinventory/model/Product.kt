package com.smartinventory.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.firebase.firestore.PropertyName
// Di Repo saya pakai Date/ServerTimestamp logic, tapi untuk aman kita pakai struktur standar.

@Parcelize
data class Product(
    // ID Dokumen
    var productId: String = "",

    // SESUAI SRS: productName (Bukan namaBarang)
    @get:PropertyName("productName")
    @set:PropertyName("productName")
    var productName: String = "",

    // SESUAI SRS: price (Bukan harga)
    @get:PropertyName("price")
    @set:PropertyName("price")
    var price: Double = 0.0,

    // SESUAI SRS: stock (Bukan stok)
    @get:PropertyName("stock")
    @set:PropertyName("stock")
    var stock: Int = 0,

    // SESUAI SRS: userId (Bukan storeId) -- INI KUNCI ISOLASI DATA
    @get:PropertyName("userId")
    @set:PropertyName("userId")
    var userId: String = "",

    // Optional: Product Code (SKU) sesuai SRS
    // @get:PropertyName("productCode")
    // @set:PropertyName("productCode")
    // var productCode: String = "",

    // Optional: Supplier (Tidak ada di SRS tapi boleh disimpan)
    // var supplier: String = ""

) : Parcelable