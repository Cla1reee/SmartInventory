package com.smartinventory.activity

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartinventory.R
import com.smartinventory.adapter.CartAdapter
import com.smartinventory.adapter.ProductAdapter
import com.smartinventory.model.Product
import com.smartinventory.utils.toRupiah
import com.smartinventory.viewmodel.CashierViewModel
import com.smartinventory.viewmodel.InventoryViewModel // Asumsi Anda pakai ini untuk load list produk awal

class CashierActivity : AppCompatActivity() {

    private lateinit var cashierViewModel: CashierViewModel
    private lateinit var inventoryViewModel: InventoryViewModel // Untuk load data produk sumber

    private lateinit var cartAdapter: CartAdapter
    private lateinit var productAdapter: ProductAdapter

    // UI Components
    private lateinit var rvProductSource: RecyclerView
    private lateinit var rvCart: RecyclerView
    private lateinit var tvTotalTransaction: TextView
    private lateinit var btnCheckout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cashier)

        // 1. Inisialisasi ViewModel
        cashierViewModel = ViewModelProvider(this)[CashierViewModel::class.java]
        inventoryViewModel = ViewModelProvider(this)[InventoryViewModel::class.java]

        // 2. Binding Views
        rvProductSource = findViewById(R.id.rvProductSource)
        rvCart = findViewById(R.id.rvCart)
        tvTotalTransaction = findViewById(R.id.tvTotalTransaction)
        btnCheckout = findViewById(R.id.btnCheckout)

        setupProductList()
        setupCartList()
        observeViewModels()
        setupActions()
    }

    private fun setupProductList() {
        // Setup RecyclerView untuk Sumber Produk (Yang mau dipilih)
        // PENTING: ProductAdapter harus punya callback/listener saat item diklik
        productAdapter = ProductAdapter(arrayListOf()) { selectedProduct ->
            // Saat produk diklik -> Masukkan ke Keranjang via ViewModel
            cashierViewModel.addToCart(selectedProduct)
        }

        rvProductSource.layoutManager = LinearLayoutManager(this)
        rvProductSource.adapter = productAdapter

        // Load data produk dari Firebase (gunakan logic InventoryViewModel yang sudah ada)
        inventoryViewModel.loadProducts()
    }

    private fun setupCartList() {
        // Setup RecyclerView untuk Keranjang (Tabel Kasir)
        // Ambil list langsung dari ViewModel agar sinkron
        val currentCart = cashierViewModel.cartItems.value ?: arrayListOf()

        cartAdapter = CartAdapter(currentCart) {
            // Callback: Saat ada perubahan Qty di adapter, minta ViewModel hitung ulang
            cashierViewModel.refreshCartCalculations()
        }

        rvCart.layoutManager = LinearLayoutManager(this)
        rvCart.adapter = cartAdapter
    }

    private fun observeViewModels() {
        // A. Observasi List Produk (Sumber)
        // KOREKSI: Menggunakan 'productList' sesuai nama di InventoryViewModel Anda
        inventoryViewModel.productList.observe(this) { listBarang ->
            if (listBarang != null) {
                productAdapter.updateData(listBarang)
            }
        }

        // B. Observasi Keranjang Belanja (Target)
        cashierViewModel.cartItems.observe(this) {
            // Saat data keranjang berubah, refresh tampilan tabel
            cartAdapter.notifyDataSetChanged()
        }

        // C. Observasi Total Harga
        cashierViewModel.totalTransaction.observe(this) { total ->
            tvTotalTransaction.text = total.toRupiah()
        }

        // D. Observasi Status Checkout (Sukses/Gagal)
        cashierViewModel.checkoutStatus.observe(this) { result ->
            result.onSuccess { message ->
                android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()
                // Opsional: Clear keranjang atau refresh halaman
                inventoryViewModel.loadProducts() // Refresh stok di list atas
            }
            result.onFailure { error ->
                android.widget.Toast.makeText(this, "Gagal: ${error.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupActions() {
        btnCheckout.setOnClickListener {
            // Panggil fungsi checkout sakti (Batch Write)
            cashierViewModel.processCheckout()
            finish()
        }
    }
}