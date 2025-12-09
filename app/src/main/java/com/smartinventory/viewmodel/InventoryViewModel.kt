package com.smartinventory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartinventory.model.Product
import com.smartinventory.model.Transaction
import com.smartinventory.repository.InventoryRepository
import kotlinx.coroutines.launch

class InventoryViewModel : ViewModel() {

    // Sebaiknya repository ini di-inject, tapi untuk sekarang ini oke.
    private val repository = InventoryRepository()

    // --- LIVE DATA ---

    private val _productList = MutableLiveData<List<Product>>()
    val productList: LiveData<List<Product>> = _productList

    private val _operationStatus = MutableLiveData<Boolean?>()
    val operationStatus: LiveData<Boolean?> = _operationStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage

    private val _transactionList = MutableLiveData<List<Transaction>>()
    val transactionList: LiveData<List<Transaction>> = _transactionList

    // --- HELPER: Reset Pesan Toast ---
    // Panggil ini setelah Toast muncul di UI agar tidak muncul lagi saat rotasi layar
    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun resetOperationStatus() {
        _operationStatus.value = null
    }

    // --- FUNGSI 1: JEMBATAN DARI ADD_ITEM_ACTIVITY ---
    fun addProduct(nama: String, hargaStr: String, stokStr: String, supplier: String) {
        resetOperationStatus() // Pastikan status bersih sebelum mulai

        // 1. IMPROVEMENT: Gunakan trim() untuk menghapus spasi tidak sengaja di awal/akhir
        val cleanNama = nama.trim()
        val cleanHargaStr = hargaStr.trim()
        val cleanStokStr = stokStr.trim()

        // Validasi Input
        if (cleanNama.isEmpty() || cleanHargaStr.isEmpty() || cleanStokStr.isEmpty()) {
            _toastMessage.value = "Nama, Harga, dan Stok wajib diisi!"
            return
        }

        // Konversi Data
        val harga = cleanHargaStr.toDoubleOrNull()
        val stok = cleanStokStr.toIntOrNull()

        // Validasi Angka
        if (harga == null || stok == null) {
            _toastMessage.value = "Format harga atau stok salah (harus angka)"
            return
        }

        val newProduct = Product(
            namaBarang = cleanNama,
            harga = harga,
            stok = stok,
            supplier = supplier.trim()
        )

        saveProductToRepo(newProduct)
    }

    // --- FUNGSI 2: SIMPAN KE REPOSITORY ---
    private fun saveProductToRepo(product: Product) {
        _isLoading.value = true

        repository.addProduct(product) { success, error ->
            _isLoading.postValue(false) // Gunakan postValue di dalam callback

            if (success) {
                _toastMessage.postValue("Barang berhasil ditambahkan!")
                _operationStatus.postValue(true)
                loadProducts() // Auto refresh
            } else {
                _toastMessage.postValue("Gagal menyimpan: $error")
                _operationStatus.postValue(false)
            }
        }
    }

    // --- FUNGSI 3: AMBIL DATA (READ + SEARCH) ---
    fun loadProducts(query: String? = null) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Panggil fungsi suspend repository
                val result = repository.getAllProducts(query)

                _productList.value = result

                if (result.isEmpty() && !query.isNullOrEmpty()) {
                    _toastMessage.value = "Barang '$query' tidak ditemukan"
                }
            } catch (e: Exception) {
                // 2. IMPROVEMENT: Tangkap error jika terjadi crash saat ambil data
                _toastMessage.value = "Terjadi kesalahan memuat data: ${e.message}"
                _productList.value = emptyList()
            } finally {
                // Pastikan loading berhenti, sukses ataupun gagal
                _isLoading.value = false
            }
        }
    }

    // --- FUNGSI 4: HAPUS BARANG ---
    fun deleteProduct(productId: String) {
        _isLoading.value = true
        repository.deleteProduct(productId) { success ->
            _isLoading.postValue(false)
            if (success) {
                _toastMessage.postValue("Barang berhasil dihapus")
                loadProducts()
            } else {
                _toastMessage.postValue("Gagal menghapus barang")
            }
        }
    }

    // --- FUNGSI 5: UPDATE BARANG ---
    fun updateProduct(productId: String, updates: Map<String, Any>) {
        _isLoading.value = true
        repository.updateProduct(productId, updates) { success ->
            _isLoading.postValue(false)
            if (success) {
                _toastMessage.postValue("Data berhasil diperbarui")
                loadProducts()
            } else {
                _toastMessage.postValue("Gagal update data")
            }
        }
    }

    // --- FUNGSI 6: SIMPAN TRANSAKSI (KASIR) ---
    fun saveTransaction(transaction: Transaction) {
        _isLoading.value = true
        resetOperationStatus()

        // KARENA REPOSITORY SUDAH 'SUSPEND', KITA PAKAI COROUTINE:
        viewModelScope.launch {
            try {
                // 1. Panggil langsung (kode akan menunggu di sini sampai selesai)
                val isSuccess = repository.createTransaction(transaction)

                _isLoading.value = false // Tidak perlu postValue karena ini Main Thread

                if (isSuccess) {
                    _toastMessage.value = "Transaksi berhasil disimpan!"
                    _operationStatus.value = true
                    loadProducts() // Update stok
                } else {
                    _toastMessage.value = "Gagal menyimpan transaksi (Cek Logcat)."
                    _operationStatus.value = false
                }
            } catch (e: Exception) {
                // Jaga-jaga jika ada error tak terduga
                _isLoading.value = false
                _toastMessage.value = "Error: ${e.message}"
                _operationStatus.value = false
            }
        }
    }

    // --- FUNGSI 7: LOAD HISTORY ---
    fun loadTransactions() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getAllTransactions()
                _transactionList.value = result

                if (result.isEmpty()) {
                    // Optional: Jangan tampilkan toast kalau cuma kosong (biar tidak mengganggu)
                    // _toastMessage.value = "Belum ada riwayat transaksi"
                }
            } catch (e: Exception) {
                _toastMessage.value = "Gagal memuat history: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}