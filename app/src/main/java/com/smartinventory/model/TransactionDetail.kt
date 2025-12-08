package com.smartinventory.model

data class TransactionDetail(
    var productId: String? = null,
    var namaBarang: String? = null,
    var qtyTerjual: Int? = null,
    var hargaSatuanSaatItu: Double? = null
)
