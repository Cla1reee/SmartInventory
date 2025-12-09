package com.smartinventory.activity

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import com.smartinventory.R
import com.smartinventory.adapter.ProductAdapter
import com.smartinventory.viewmodel.InventoryViewModel

class ProductListActivity : AppCompatActivity() {

    private lateinit var viewModel: InventoryViewModel
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        // 1. Setup RecyclerView
        val rvProductList = findViewById<RecyclerView>(R.id.rvProductList)
        rvProductList.layoutManager = LinearLayoutManager(this)

        adapter = ProductAdapter { product ->
            // Saat item diklik, buka EditItemActivity
            val intent = Intent(this, EditItemActivity::class.java)

            // "Titip" data barang ke dalam intent (ini bisa berkat @Parcelize tadi)
            intent.putExtra("EXTRA_PRODUCT", product)

            startActivity(intent)
        }
        rvProductList.adapter = adapter

        // 2. Setup ViewModel
        viewModel = ViewModelProvider(this).get(InventoryViewModel::class.java)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // 3. Observer Data Barang
        viewModel.productList.observe(this) { products ->
            adapter.updateData(products)

            if (products.isEmpty()) {
                Toast.makeText(this, "Belum ada barang", Toast.LENGTH_SHORT).show()
            }
        }

        // 4. Observer Loading
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // 5. Observer Error Message
        viewModel.toastMessage.observe(this) { msg ->
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        // 6. Panggil Data dari Firebase
        viewModel.loadProducts()
    }

    // Agar saat kembali ke sini data ter-refresh (misal habis tambah barang)
    override fun onResume() {
        super.onResume()
        viewModel.loadProducts()
    }
}