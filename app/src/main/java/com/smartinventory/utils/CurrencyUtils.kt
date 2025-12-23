package com.smartinventory.utils

import java.text.NumberFormat
import java.util.Locale

// Fungsi Ekstensi untuk Double
fun Double.toRupiah(): String {
    val localeID = Locale("id", "ID") // Locale Indonesia
    val numberFormat = NumberFormat.getCurrencyInstance(localeID)

    // Opsi: Hilangkan 2 angka desimal (,00) di belakang agar lebih rapi
    numberFormat.maximumFractionDigits = 0

    return numberFormat.format(this)
}

// Fungsi Ekstensi untuk Int (jaga-jaga jika ada variabel Int)
fun Int.toRupiah(): String {
    return this.toDouble().toRupiah()
}
