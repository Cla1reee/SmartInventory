package com.smartinventory.viewmodel

import com.smartinventory.model.Transaction
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartinventory.model.Product
import com.smartinventory.repository.InventoryRepository
import kotlinx.coroutines.launch


class InventoryViewModel : ViewModel() {

    private val repository = InventoryRepository()

    // LiveData: Daftar Produk (akan diamati oleh Activity)
    private val _productList = MutableLiveData<List<Product>>()
    val productList: LiveData<List<Product>> = _productList

    // LiveData: Status Loading (untuk ProgressBar)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData: Pesan Notifikasi
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    // Fungsi: Ambil Data (READ + SEARCH)
    fun loadProducts(query: String? = null) {
        _isLoading.value = true
        viewModelScope.launch {
            // Panggil fungsi suspend dari Repository
            val result = repository.getAllProducts(query)

            _productList.value = result
            _isLoading.value = false

            if (result.isEmpty() && !query.isNullOrEmpty()) {
                _toastMessage.value = "Barang tidak ditemukan"
            }
        }
    }

    // Fungsi: Tambah Barang (CREATE)
    fun addProduct(product: Product) {
        _isLoading.value = true
        repository.addProduct(product) { success, error ->
            _isLoading.value = false
            if (success) {
                _toastMessage.value = "Barang berhasil ditambahkan!"
                loadProducts() // Refresh data otomatis
            } else {
                _toastMessage.value = "Gagal: $error"
            }
        }
    }

    // Fungsi: Hapus Barang (DELETE)
    fun deleteProduct(productId: String) {
        repository.deleteProduct(productId) { success ->
            if (success) {
                _toastMessage.value = "Barang dihapus"
                loadProducts() // Refresh data
            } else {
                _toastMessage.value = "Gagal menghapus barang"
            }
        }
    }

    // Fungsi: Update Barang (UPDATE)
    fun updateProduct(productId: String, updates: Map<String, Any>) {
        repository.updateProduct(productId, updates) { success ->
            if (success) {
                _toastMessage.value = "Data berhasil diperbarui"
                loadProducts()
            } else {
                _toastMessage.value = "Gagal update data"
            }
        }
    }

    // --- FUNGSI BARU: SIMPAN TRANSAKSI (Dipanggil dari UI Kasir) ---
    fun saveTransaction(transaction: Transaction) {
        _isLoading.value = true

        // Memanggil fungsi di Repository yang tadi "belum terpanggil"
        repository.createTransaction(transaction) { success ->
            _isLoading.value = false
            if (success) {
                _toastMessage.value = "Transaksi berhasil disimpan!"
                // Opsional: Muat ulang produk karena stok mungkin berkurang
                loadProducts()
            } else {
                _toastMessage.value = "Gagal menyimpan transaksi."
            }
        }
    }
}