package com.smartinventory.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.smartinventory.R
import com.smartinventory.viewmodel.InventoryViewModel

class AddProductActivity : AppCompatActivity() {

    private lateinit var viewModel: InventoryViewModel
    private lateinit var btnSimpan: Button // Global variable agar bisa diakses observer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        // Inisialisasi ViewModel
        viewModel = ViewModelProvider(this).get(InventoryViewModel::class.java)

        // 1. PENTING: Reset status operasi agar tidak ada 'ghost success' dari sesi sebelumnya
        viewModel.resetOperationStatus()
        viewModel.clearToastMessage()

        // Binding Views
        val etNama = findViewById<EditText>(R.id.etNamaBarang)
        val etHarga = findViewById<EditText>(R.id.etHarga)
        val etStok = findViewById<EditText>(R.id.etStok)
        val etSupplier = findViewById<EditText>(R.id.etSupplier)
        btnSimpan = findViewById(R.id.btnSimpanBarang)

        // Listener Tombol Simpan
        btnSimpan.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val hargaStr = etHarga.text.toString().trim()
            val stokStr = etStok.text.toString().trim()
            val supplier = etSupplier.text.toString().trim()

            // Validasi String Kosong
            if (nama.isEmpty() || hargaStr.isEmpty() || stokStr.isEmpty()) {
                Toast.makeText(this, "Mohon lengkapi Nama, Harga, dan Stok!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validasi Angka (Cegah error konversi)
            val harga = hargaStr.toDoubleOrNull()
            val stok = stokStr.toIntOrNull()

            if (harga == null || stok == null) {
                Toast.makeText(this, "Format harga atau stok salah!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Panggil ViewModel
            // Ini akan memicu _isLoading = true di ViewModel
            viewModel.addProduct(nama, harga, stok, supplier)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        // 2. Observer Status (Sukses/Gagal)
        viewModel.operationStatus.observe(this) { isSuccess ->
            if (isSuccess == true) {
                // Beri sedikit delay atau langsung tutup
                finish()
            }
        }

        // 3. Observer Loading (CEGAH ANR & SPAM KLIK)
        // Saat loading = true, tombol dimatikan.
        viewModel.isLoading.observe(this) { isLoading ->
            btnSimpan.isEnabled = !isLoading
            btnSimpan.text = if (isLoading) "Menyimpan..." else "Simpan Barang"
        }

        // 4. Observer Pesan Error/Sukses
        viewModel.toastMessage.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                viewModel.clearToastMessage() // Bersihkan pesan agar tidak muncul ulang saat rotasi layar
            }
        }
    }
}