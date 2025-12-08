package com.smartinventory.model

import com.google.firebase.Timestamp

data class Product(
    var productId: String? = null,
    var namaBarang: String? = null,
    var kodeBarcode: String? = null,
    var hargaBeli: Double? = null,
    var hargaJual: Double? = null,
    var stokSaatIni: Int? = null,
    var satuan: String? = null,
    var tglTerakhirUpdate: Timestamp? = null
)
