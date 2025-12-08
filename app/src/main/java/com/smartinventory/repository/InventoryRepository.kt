package com.smartinventory.repository

import com.smartinventory.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.smartinventory.model.Transaction


class InventoryRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val PRODUCTS_COLLECTION = "products"

    // TAMBAHKAN COLLECTION INI:
    private val TRANSACTIONS_COLLECTION = "transactions"

    // Asumsi: Setiap user punya produknya sendiri (untuk multi-user/multi-toko)
    private val userId = auth.currentUser?.uid

    // --- FUNGSI 1: CREATE (Tambah Barang Baru) ---
    fun addProduct(product: Product, callback: (isSuccess: Boolean, error: String?) -> Unit) {
        // ID produk diisi oleh Firestore saat ditambahkan (.add)
        db.collection(PRODUCTS_COLLECTION)
            .add(product)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    // --- FUNGANGSI 2: READ (Ambil Daftar Barang + Search) ---
    // Menggunakan suspend function untuk Coroutines
    suspend fun getAllProducts(query: String? = null): List<Product> {
        return try {
            var dbQuery = db.collection(PRODUCTS_COLLECTION).limit(100) // Batasi 100 barang

            // Implementasi SEARCH (S)
            if (!query.isNullOrBlank()) {
                val endQuery = query + "\uf8ff" // Teknik Firestore untuk 'starts with'
                dbQuery = dbQuery.whereGreaterThanOrEqualTo("namaBarang", query)
                    .whereLessThanOrEqualTo("namaBarang", endQuery)
            }

            val snapshot = dbQuery.get().await() // 'await()' membuat fungsi ini synchronous

            // Mapping DocumentSnapshot ke Data Class Product
            snapshot.documents.mapNotNull { document ->
                document.toObject(Product::class.java)?.apply {
                    productId = document.id // Set ID dari Firestore Document
                }
            }
        } catch (e: Exception) {
            // Log error
            emptyList()
        }
    }

    // --- FUNGSI 3: UPDATE (Ubah Detail Barang) ---
    fun updateProduct(productId: String, updates: Map<String, Any>, callback: (isSuccess: Boolean) -> Unit) {
        db.collection(PRODUCTS_COLLECTION)
            .document(productId)
            .update(updates)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    // --- FUNGSI 4: DELETE (Hapus Barang) ---
    fun deleteProduct(productId: String, callback: (isSuccess: Boolean) -> Unit) {
        db.collection(PRODUCTS_COLLECTION)
            .document(productId)
            .delete()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    // --- FUNGSI 5: SIMPAN TRANSAKSI (KASIR) ---
    fun createTransaction(transaction: Transaction, callback: (isSuccess: Boolean) -> Unit) {
        // 1. Simpan data transaksi ke collection 'transactions'
        db.collection(TRANSACTIONS_COLLECTION)
            .add(transaction)
            .addOnSuccessListener {
                // Jika sukses simpan transaksi, panggil callback true
                callback(true)

                // TODO (Nanti): Di sini kita bisa tambahkan logika potong stok otomatis (Batch Write)
                // Tapi untuk sekarang, simpan transaksi dulu sudah cukup.
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}