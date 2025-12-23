package com.smartinventory.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.smartinventory.R
import com.smartinventory.model.Product
import com.smartinventory.viewmodel.InventoryViewModel

class EditItemActivity : AppCompatActivity() {

    private lateinit var viewModel: InventoryViewModel
    private lateinit var currentProduct: Product // Data barang yang sedang diedit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)

        viewModel = ViewModelProvider(this).get(InventoryViewModel::class.java)

        // 1. TERIMA DATA DARI LIST (Parcelable)
        // Kita pakai teknik pengaman agar tidak error jika data null
        val productData = intent.getParcelableExtra<Product>("EXTRA_PRODUCT")

        if (productData == null) {
            Toast.makeText(this, "Error: Data barang tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish() // Tutup halaman jika data kosong
            return
        }
        currentProduct = productData

        // 2. ISI FORMULIR DENGAN DATA LAMA
        val etNama = findViewById<EditText>(R.id.etEditNama)
        val etHarga = findViewById<EditText>(R.id.etEditHarga)
        val etStok = findViewById<EditText>(R.id.etEditStok)
        val etSupplier = findViewById<EditText>(R.id.etEditSupplier)

        // Isi text-nya
        etNama.setText(currentProduct.productName)
        // Ubah angka ke text agar bisa diedit
        etHarga.setText(currentProduct.price.toInt().toString()) // .toInt() biar .0 nya hilang
        etStok.setText(currentProduct.stock.toString())
        etSupplier.setText(currentProduct.supplier)

        // 3. TOMBOL UPDATE
        val btnUpdate = findViewById<Button>(R.id.btnUpdate)
        btnUpdate.setOnClickListener {
            val namaBaru = etNama.text.toString().trim()
            val hargaBaru = etHarga.text.toString().toDoubleOrNull() ?: 0.0
            val stokBaru = etStok.text.toString().toIntOrNull() ?: 0
            val supplierBaru = etSupplier.text.toString().trim()

            // VALIDASI TAMBAHAN (Sesuai REQ-SAFE-002)
            if (hargaBaru < 0 || stokBaru < 0) {
                Toast.makeText(this, "Harga dan Stok tidak boleh negatif!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Siapkan bungkusan data yang mau diupdate
            val updates = mapOf(
                "productName" to namaBaru,
                "Price" to hargaBaru,
                "stock" to stokBaru,
                "supplier" to supplierBaru
            )

            // Kirim ke ViewModel
            viewModel.updateProduct(currentProduct.productId, updates)
        }

        // 4. TOMBOL HAPUS (DELETE)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        btnDelete.setOnClickListener {
            showDeleteConfirmation()
        }

        // 5. OBSERVER HASIL (Toast & Tutup)
        viewModel.toastMessage.observe(this) { msg ->
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

            // Jika sukses update atau hapus, tutup halaman ini
            if (msg == "Data berhasil diperbarui" || msg == "Barang dihapus") {
                finish()
            }
        }
    }

    // Fungsi menampilkan Dialog Konfirmasi Hapus (Biar tidak kepencet)
    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Barang?")
            .setMessage("Apakah Anda yakin ingin menghapus '${currentProduct.productName}'? Data tidak bisa dikembalikan.")
            .setPositiveButton("Hapus") { _, _ ->
                // Panggil ViewModel Delete
                viewModel.deleteProduct(currentProduct.productId)
                finish()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}