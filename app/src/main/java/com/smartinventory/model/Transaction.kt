package com.smartinventory.model

import com.google.firebase.Timestamp

data class Transaction(
    var transactionId: String? = null,
    var waktuTransaksi: Timestamp? = null,
    var totalPenjualan: Double? = null,
    var detailBarang: List<TransactionDetail>? = null
)
