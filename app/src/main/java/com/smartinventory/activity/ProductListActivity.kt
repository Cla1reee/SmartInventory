package com.smartinventory.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView // Pastikan Import ini benar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartinventory.R
import com.smartinventory.adapter.ProductAdapter
import com.smartinventory.viewmodel.InventoryViewModel

class ProductListActivity : AppCompatActivity() {

    private lateinit var viewModel: InventoryViewModel
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        // 1. Setup ViewModel
        viewModel = ViewModelProvider(this).get(InventoryViewModel::class.java)

        // 2. Setup RecyclerView
        val rvProductList = findViewById<RecyclerView>(R.id.rvProductList)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // Setup Adapter
        adapter = ProductAdapter { product ->
            // Klik item -> Edit/Hapus
            val intent = Intent(this, EditItemActivity::class.java)
            intent.putExtra("EXTRA_PRODUCT", product)
            startActivity(intent)
        }

        rvProductList.layoutManager = LinearLayoutManager(this)
        rvProductList.adapter = adapter

        // 3. Setup SEARCH VIEW (Perbaikan REQ-SRCH-001)
        // Menghubungkan XML (svBarang) dengan Logika
        val svBarang = findViewById<SearchView>(R.id.svBarang)

        svBarang.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Panggil saat user tekan enter keyboard
                viewModel.loadProducts(query)
                svBarang.clearFocus() // Sembunyikan keyboard
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Panggil SETIAP HURUF diketik (Realtime)
                viewModel.loadProducts(newText)
                return true
            }
        })

        // 4. Observer Data Barang
        viewModel.productList.observe(this) { products ->
            adapter.updateData(products)

            // Logika UI kosong/isi
            if (products.isEmpty()) {
                // Opsional: Bisa tampilkan text "Data tidak ditemukan" jika mau
                // Toast.makeText(this, "Tidak ada data", Toast.LENGTH_SHORT).show()
            }
        }

        // 5. Observer Loading
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // 6. Observer Error Message
        viewModel.toastMessage.observe(this) { msg ->
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                viewModel.clearToastMessage() // Bersihkan agar tidak muncul terus
            }
        }

        // Load awal (Tanpa query)
        viewModel.loadProducts()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data saat kembali dari EditActivity
        // Ambil query yang sedang ada di searchview (jika ada) agar filter tidak hilang
        val svBarang = findViewById<SearchView>(R.id.svBarang)
        val currentQuery = svBarang.query.toString()
        viewModel.loadProducts(if (currentQuery.isNotEmpty()) currentQuery else null)
    }
}