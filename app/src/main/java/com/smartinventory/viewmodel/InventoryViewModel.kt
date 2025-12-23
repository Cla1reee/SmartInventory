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

    // --- HELPER ---
    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun resetOperationStatus() {
        _operationStatus.value = null
    }

    // --- FUNGSI 1: TAMBAH BARANG (DIPERBAIKI) ---
    fun addProduct(nama: String, harga: Double, stok: Int, supplier: String) {
        resetOperationStatus()

        // 1. Validasi Input
        if (nama.isEmpty()) {
            _toastMessage.value = "Nama barang wajib diisi!"
            return
        }

        // 2. Buat Objek Product dengan Nama Field BARU (Sesuai SRS)
        val newProduct = Product(
            productName = nama.trim(),  // PERBAIKAN: namaBarang -> productName
            price = harga,              // PERBAIKAN: harga -> price
            stock = stok,               // PERBAIKAN: stok -> stock
            supplier = supplier.trim(),
            userId = "" // Repository yang akan mengisi userId ini secara otomatis
        )

        saveProductToRepo(newProduct)
    }

    // Overloading untuk support panggilan lama (String input) dari Activity jika perlu
    fun addProduct(nama: String, hargaStr: String, stokStr: String, supplier: String) {
        val cleanNama = nama.trim()
        val cleanHargaStr = hargaStr.trim()
        val cleanStokStr = stokStr.trim()

        if (cleanNama.isEmpty() || cleanHargaStr.isEmpty() || cleanStokStr.isEmpty()) {
            _toastMessage.value = "Nama, Harga, dan Stok wajib diisi!"
            return
        }

        val harga = cleanHargaStr.toDoubleOrNull()
        val stok = cleanStokStr.toIntOrNull()

        if (harga == null || stok == null) {
            _toastMessage.value = "Format harga atau stok salah (harus angka)"
            return
        }

        if (harga < 0 || stok < 0) {
            _toastMessage.value = "Harga dan Stok tidak boleh negatif!"
            return
        }
        // Panggil fungsi utama
        addProduct(cleanNama, harga, stok, supplier)
    }

    // --- FUNGSI 2: SIMPAN KE REPOSITORY ---
    private fun saveProductToRepo(product: Product) {
        _isLoading.value = true

        repository.addProduct(product) { success, error ->
            _isLoading.postValue(false)

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
                val result = repository.getAllProducts(query)
                _productList.value = result

                if (result.isEmpty() && !query.isNullOrEmpty()) {
                    _toastMessage.value = "Barang '$query' tidak ditemukan"
                }
            } catch (e: Exception) {
                _toastMessage.value = "Terjadi kesalahan: ${e.message}"
                _productList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }


    // --- FUNGSI 4: UPDATE BARANG ---
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

    // --- FUNGSI 5: HAPUS BARANG ---
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

    // --- FUNGSI 6: SIMPAN TRANSAKSI (KASIR) ---
    fun saveTransaction(transaction: Transaction) {
        _isLoading.value = true
        resetOperationStatus()

        viewModelScope.launch {
            try {
                // Repository sudah menghandle logika pengurangan stok
                val isSuccess = repository.createTransaction(transaction)

                _isLoading.value = false

                if (isSuccess) {
                    _toastMessage.value = "Transaksi berhasil disimpan!"
                    _operationStatus.value = true
                    loadProducts() // Refresh stok di UI
                } else {
                    _toastMessage.value = "Gagal menyimpan transaksi (Stok habis/Error)."
                    _operationStatus.value = false
                }
            } catch (e: Exception) {
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
            } catch (e: Exception) {
                _toastMessage.value = "Gagal memuat history: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}