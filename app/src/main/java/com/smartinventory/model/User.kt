package com.smartinventory.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class User(
    // ID Dokumen (Tidak disimpan sebagai field, tapi perlu untuk referensi)
    var uId: String? = null,

    // 1. Email (Sesuai SRS)
    var email: String? = null,

    // 2. Store Name (WAJIB SESUAI SRS: "storeName")
    // @PropertyName memaksa Firestore menyimpan field ini dengan nama "storeName"
    // meskipun variabel Kotlin kita bernama "storeName" (camelCase standar)
    @get:PropertyName("storeName")
    @set:PropertyName("storeName")
    var storeName: String? = null,

    // 3. Role (Sesuai SRS: default "admin")
    var role: String = "admin",

    // 4. Created At (Sesuai SRS: Timestamp)
    // Menggunakan @ServerTimestamp nanti di Repository, atau Date() biasa
    var createdAt: Date? = null,

    // Optional (Tidak ada di SRS tapi boleh ada untuk pelengkap)
    // var noTelp: String? = null
)