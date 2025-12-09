package com.smartinventory.repository

import com.smartinventory.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
    fun addProduct(product: Product, onResult: (Boolean, String?) -> Unit) {
        // 1. Ambil User ID (Barang ini milik toko siapa?)
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            onResult(false, "User tidak terdeteksi")
            return
        }

        // 2. Set 'storeId' agar barang tidak tertukar dengan toko lain
        product.storeId = currentUserId

        // 3. Buat Dokumen Baru di Firebase (Generate ID Otomatis)
        val newDocRef = db.collection(PRODUCTS_COLLECTION).document()

        // 4. Masukkan ID otomatis itu ke dalam objek Product (supaya tersimpan)
        product.productId = newDocRef.id // Pastikan di Product.kt namanya 'productId'

        // 5. Simpan ke Database
        newDocRef.set(product)
            .addOnSuccessListener {
                onResult(true, null) // Sukses
            }
            .addOnFailureListener { e ->
                onResult(false, e.message) // Gagal
            }
    }

    // --- FUNGANGSI 2: READ (Ambil Daftar Barang + Search) ---
    // Menggunakan suspend function untuk Coroutines
    suspend fun getAllProducts(query: String? = null): List<Product> {
        val currentUserId = auth.currentUser?.uid ?: return emptyList() // Cek login dulu

        return try {
            // 1. Mulai dengan filter pemilik toko (Wajib)
            var dbQuery = db.collection(PRODUCTS_COLLECTION)
                .whereEqualTo("storeId", currentUserId)
                .limit(100)

            // 2. Jika ada search, tambahkan filter nama
            if (!query.isNullOrBlank()) {
                val endQuery = query + "\uf8ff"
                dbQuery = dbQuery
                    .whereGreaterThanOrEqualTo("namaBarang", query)
                    .whereLessThanOrEqualTo("namaBarang", endQuery)
                // Note: Firestore butuh Index "storeId + namaBarang" untuk ini
            }

            val snapshot = dbQuery.get().await()
            snapshot.documents.mapNotNull { document ->
                document.toObject(Product::class.java)?.apply {
                    productId = document.id
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("RepoProduct", "Error: ${e.message}")
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
    suspend fun createTransaction(transaction: Transaction): Boolean {
        val productRef = db.collection(PRODUCTS_COLLECTION).document(transaction.productId)
        val transactionRef = db.collection(TRANSACTIONS_COLLECTION).document()

        return try {
            db.runTransaction { transactionDb ->
                val snapshot = transactionDb.get(productRef)
                val stokSaatIni = snapshot.getLong("stok") ?: 0

                if (stokSaatIni < transaction.jumlahBeli) {
                    throw Exception("Stok Habis") // Ini akan melempar ke catch
                }

                val stokBaru = stokSaatIni - transaction.jumlahBeli
                transactionDb.update(productRef, "stok", stokBaru)

                transaction.id = transactionRef.id
                transactionDb.set(transactionRef, transaction)
            }.await() // await() mengubah callback menjadi synchronous

            true // Jika sampai sini berarti sukses
        } catch (e: Exception) {
            android.util.Log.e("Transaksi", "Gagal: ${e.message}")
            false // Jika error
        }
    }
    // --- FUNGSI 6: AMBIL RIWAYAT TRANSAKSI ---
    suspend fun getAllTransactions(): List<Transaction> {
        return try {
            val currentUserId = auth.currentUser?.uid

            // Ambil data dari collection 'transactions'
            // Diurutkan berdasarkan tanggal (Terbaru di atas)
            val snapshot = db.collection(TRANSACTIONS_COLLECTION)
                .whereEqualTo("storeId", currentUserId)
                .orderBy("tanggal", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Transaction::class.java)?.apply {
                    id = doc.id
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("RepoHistory", "Gagal ambil history: ${e.message}")
            emptyList()
        }
    }
}