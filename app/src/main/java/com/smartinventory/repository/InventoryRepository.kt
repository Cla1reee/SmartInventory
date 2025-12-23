package com.smartinventory.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import com.smartinventory.model.Product
import com.smartinventory.model.Transaction

class InventoryRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // NAMA COLLECTION (Sesuai SRS)
    private val PRODUCTS_COLLECTION = "products"       // [cite: 508]
    private val TRANSACTIONS_COLLECTION = "transactions" // [cite: 524]

    // --- FUNGSI 1: CREATE (Tambah Barang Baru) ---
    fun addProduct(product: Product, onResult: (Boolean, String?) -> Unit) {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            onResult(false, "User tidak terdeteksi")
            return
        }

        // PERBAIKAN: Set userId sesuai SRS [cite: 517]
        product.userId = currentUserId

        val newDocRef = db.collection(PRODUCTS_COLLECTION).document()

        // Simpan ID dokumen ke dalam field model agar konsisten
        product.productId = newDocRef.id

        newDocRef.set(product)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    // --- FUNGSI 2: READ (Ambil Daftar Barang + Search) ---
    suspend fun getAllProducts(query: String? = null): List<Product> {
        val currentUserId = auth.currentUser?.uid ?: return emptyList()

        return try {
            // PERBAIKAN: Filter berdasarkan userId [cite: 542]
            var dbQuery = db.collection(PRODUCTS_COLLECTION)
                .whereEqualTo("userId", currentUserId)

            // Logika Search Sederhana
            if (!query.isNullOrBlank()) {
                val endQuery = query + "\uf8ff"
                dbQuery = dbQuery
                    .whereGreaterThanOrEqualTo("productName", query)
                    .whereLessThanOrEqualTo("productName", endQuery)
            }

            // Eksekusi Query
            val snapshot = dbQuery.get().await()
            snapshot.documents.mapNotNull { document ->
                // Konversi dokumen ke objek Product
                // @DocumentId di model akan otomatis mengisi field productId
                document.toObject(Product::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // --- FUNGSI 3: UPDATE ---
    fun updateProduct(productId: String, updates: Map<String, Any>, callback: (Boolean) -> Unit) {
        db.collection(PRODUCTS_COLLECTION)
            .document(productId)
            .update(updates)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    // --- FUNGSI 4: DELETE ---
    fun deleteProduct(productId: String, callback: (Boolean) -> Unit) {
        db.collection(PRODUCTS_COLLECTION)
            .document(productId)
            .delete()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    // --- FUNGSI 5: SIMPAN TRANSAKSI (ATOMIC BATCH WRITE) ---
    // PERBAIKAN TOTAL: Disesuaikan untuk Multi-Item sesuai SRS [cite: 326, 530]
    suspend fun createTransaction(transaction: Transaction): Boolean {
        val currentUserId = auth.currentUser?.uid ?: return false
        transaction.userId = currentUserId

        // 1. Siapkan Batch
        val batch = db.batch()

        // 2. Siapkan Referensi Dokumen Transaksi Baru
        val transactionRef = db.collection(TRANSACTIONS_COLLECTION).document()
        transaction.transactionId = transactionRef.id // Set ID yang baru dibuat

        // 3. Masukkan Operasi "Simpan Transaksi" ke Batch
        batch.set(transactionRef, transaction)

        // 4. Masukkan Operasi "Kurangi Stok" untuk SETIAP ITEM ke Batch
        // Kita meloop list 'items' yang ada di dalam objek Transaction
        for (item in transaction.items) {
            val productRef = db.collection(PRODUCTS_COLLECTION).document(item.productId)

            // Kurangi stok (Atomic Increment negatif)
            // Sesuai SRS: Update product stock levels [cite: 328]
            batch.update(productRef, "stock", FieldValue.increment(-item.qty.toLong()))
        }

        // 5. Eksekusi Semua (Commit)
        return try {
            batch.commit().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // --- FUNGSI 6: AMBIL RIWAYAT TRANSAKSI ---
    suspend fun getAllTransactions(): List<Transaction> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: return emptyList()

            db.collection(TRANSACTIONS_COLLECTION)
                .whereEqualTo("userId", currentUserId) // Filter Data Isolation [cite: 356]
                .orderBy("transactionDate", Query.Direction.DESCENDING) // Urutkan terbaru [cite: 358]
                .get()
                .await()
                .documents.mapNotNull { doc ->
                    doc.toObject(Transaction::class.java)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
}