package com.smartinventory.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.smartinventory.model.Product
import com.smartinventory.model.Transaction
import com.smartinventory.model.TransactionItem

class CashierViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // State untuk Keranjang Belanja
    private val _cartItems = MutableLiveData<ArrayList<TransactionItem>>(ArrayList())
    val cartItems: LiveData<ArrayList<TransactionItem>> = _cartItems

    // State untuk Total Harga (Agar UI update otomatis)
    private val _totalTransaction = MutableLiveData<Double>(0.0)
    val totalTransaction: LiveData<Double> = _totalTransaction

    // State Loading & Status
    private val _checkoutStatus = MutableLiveData<Result<String>>()
    val checkoutStatus: LiveData<Result<String>> = _checkoutStatus

    // Fungsi 1: Tambah Barang ke Keranjang
    // Memastikan Qty tidak melebihi stok yang tersedia (REQ-POS-004)
    fun addToCart(product: Product) {
        val currentList = _cartItems.value ?: ArrayList()

        // Cek apakah barang sudah ada di keranjang?
        val existingItem = currentList.find { it.productId == product.productId }

        if (existingItem != null) {
            // Jika sudah ada, tambah qty (Cek stok dulu!)
            if (existingItem.qty + 1 <= product.stock) {
                existingItem.qty += 1
                existingItem.subtotal = existingItem.price * existingItem.qty
            } else {
                _checkoutStatus.value = Result.failure(Exception("Stok tidak cukup!"))
                return
            }
        } else {
            // Jika belum ada, buat item baru
            if (product.stock > 0) {
                val newItem = TransactionItem(
                    productId = product.productId,
                    productName = product.productName,
                    qty = 1,
                    price = product.price.toDouble(),
                    subtotal = product.price.toDouble()
                )
                currentList.add(newItem)
            } else {
                _checkoutStatus.value = Result.failure(Exception("Stok Habis!"))
                return
            }
        }

        _cartItems.value = currentList // Trigger update UI
        calculateTotal()
    }

    // Fungsi 2: Update Cart (Dipanggil dari Adapter saat tombol +/- ditekan)
    fun refreshCartCalculations() {
        calculateTotal()
        _cartItems.value = _cartItems.value // Trigger observer
    }

    // Fungsi 3: Hitung Total
    private fun calculateTotal() {
        val total = _cartItems.value?.sumOf { it.subtotal } ?: 0.0
        _totalTransaction.value = total
    }

    // Fungsi 4: CHECKOUT (Kritis: Batch Write)
    // Sesuai SRS REQ-POS-005
    fun processCheckout() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _checkoutStatus.value = Result.failure(Exception("User tidak login"))
            return
        }

        val itemsToBuy = _cartItems.value ?: return
        if (itemsToBuy.isEmpty()) {
            _checkoutStatus.value = Result.failure(Exception("Keranjang kosong"))
            return
        }

        val batch = db.batch()
        val transactionRef = db.collection("transactions").document()

        // A. Siapkan Data Transaksi Induk
        val transactionData = Transaction(
            transactionId = transactionRef.id,
            userId = currentUser.uid,
            totalAmount = _totalTransaction.value ?: 0.0,
            transactionDate = null, // ServerTimestamp akan handle ini
            items = itemsToBuy.toList() // Simpan Array Items
        )

        // B. Masukkan Operasi Simpan Transaksi ke Batch
        batch.set(transactionRef, transactionData)

        // C. Masukkan Operasi Kurangi Stok ke Batch (Looping item)
        for (item in itemsToBuy) {
            val productRef = db.collection("products").document(item.productId)
            // Kurangi stok secara atomik di server
            batch.update(productRef, "stock", FieldValue.increment(-item.qty.toLong()))
        }

        // D. Eksekusi Batch (Commit)
        batch.commit()
            .addOnSuccessListener {
                _cartItems.value?.clear() // Kosongkan keranjang
                calculateTotal()
                _checkoutStatus.value = Result.success("Transaksi Berhasil!")
            }
            .addOnFailureListener { e ->
                _checkoutStatus.value = Result.failure(e)
            }
    }
}