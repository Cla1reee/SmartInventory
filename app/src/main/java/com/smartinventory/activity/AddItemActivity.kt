package com.smartinventory.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.smartinventory.R
import com.smartinventory.viewmodel.InventoryViewModel

class AddItemActivity : AppCompatActivity() {

    private lateinit var viewModel: InventoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        viewModel = ViewModelProvider(this).get(InventoryViewModel::class.java)

        // 1. Inisialisasi Komponen UI
        val etNama = findViewById<EditText>(R.id.etNamaBarang)
        val etHarga = findViewById<EditText>(R.id.etHarga)
        val etStok = findViewById<EditText>(R.id.etStok)
        val etSupplier = findViewById<EditText>(R.id.etSupplier)
        val btnSimpan = findViewById<Button>(R.id.btnSimpanBarang)

        // 2. Aksi Tombol Simpan
        btnSimpan.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val harga = etHarga.text.toString().trim()
            val stok = etStok.text.toString().trim()
            val supplier = etSupplier.text.toString().trim()

            // Panggil ViewModel
            viewModel.addProduct(nama, harga, stok, supplier)
        }

        // 3. Observer Status (Tutup halaman jika sukses)
        viewModel.operationStatus.observe(this) { isSuccess ->
            if (isSuccess == true) {
                // Pesan sukses sudah ditangani di observer bawah, jadi cukup finish
                finish()
            }
        }

        // 4. Observer Pesan (Toast)
        viewModel.toastMessage.observe(this) { msg ->
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }
}