package com.smartinventory.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.firebase.firestore.DocumentId // <-- WAJIB IMPORT INI
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
@Parcelize
data class Product(
    // Tambahkan @DocumentId agar Firestore otomatis mengisi ID dokumen ke sini.
    @DocumentId
    var productId: String = "",

    @get:PropertyName("productName")
    @set:PropertyName("productName")
    var productName: String = "",

    @get:PropertyName("price")
    @set:PropertyName("price")
    var price: Double = 0.0,

    @get:PropertyName("stock")
    @set:PropertyName("stock")
    var stock: Int = 0,

    @get:PropertyName("userId")
    @set:PropertyName("userId")
    var userId: String = "",

    @get:PropertyName("productCode")
    @set:PropertyName("productCode")
    var productCode: String = "",

    var supplier: String = ""

) : Parcelable