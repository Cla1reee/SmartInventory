package com.smartinventory.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.smartinventory.R
import com.smartinventory.model.Product
import com.smartinventory.model.Transaction
import com.smartinventory.viewmodel.InventoryViewModel
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

class CashierActivity : AppCompatActivity() {

    private lateinit var viewModel: InventoryViewModel
    private var selectedProduct: Product? = null // Barang yang sedang dipilih
    private var productList = listOf<Product>() // Daftar semua barang

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cashier)

        viewModel = ViewModelProvider(this).get(InventoryViewModel::class.java)

        val spinner = findViewById<Spinner>(R.id.spinnerProduct)
        val etQty = findViewById<EditText>(R.id.etQty)
        val tvPriceInfo = findViewById<TextView>(R.id.tvPriceInfo)
        val tvTotal = findViewById<TextView>(R.id.tvTotalPrice)
        val btnPay = findViewById<Button>(R.id.btnPay)

        // 1. Ambil Data Barang untuk Spinner
        viewModel.loadProducts()
        viewModel.productList.observe(this) { products ->
            productList = products

            // Siapkan nama-nama barang untuk ditampilkan di Spinner
            val productNames = products.map { "${it.namaBarang} (Stok: ${it.stok})" }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, productNames)
            spinner.adapter = adapter
        }

        // 2. Deteksi Saat Barang Dipilih
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (productList.isNotEmpty()) {
                    selectedProduct = productList[position]
                    tvPriceInfo.text = "Harga Satuan: ${formatRupiah(selectedProduct!!.harga)}"
                    calculateTotal(etQty, tvTotal) // Hitung ulang total
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 3. Deteksi Saat Jumlah (Qty) Diketik
        etQty.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { calculateTotal(etQty, tvTotal) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 4. Tombol Bayar
        btnPay.setOnClickListener {
            val qty = etQty.text.toString().toIntOrNull() ?: 0

            if (selectedProduct == null) {
                Toast.makeText(this, "Pilih barang dulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (qty <= 0) {
                Toast.makeText(this, "Jumlah beli minimal 1", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (qty > selectedProduct!!.stok) {
                Toast.makeText(this, "Stok tidak cukup! (Sisa: ${selectedProduct!!.stok})", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Buat Data Transaksi
            val totalBayar = selectedProduct!!.harga * qty
            val newTransaction = Transaction(
                productId = selectedProduct!!.productId, // ID Barang
                namaBarang = selectedProduct!!.namaBarang,
                jumlahBeli = qty,
                totalHarga = totalBayar,
                tanggal = Date(),
                storeId = selectedProduct!!.storeId
            )

            // Kirim ke ViewModel
            viewModel.saveTransaction(newTransaction)
        }

        // 5. Observer Hasil Transaksi
        viewModel.operationStatus.observe(this) { isSuccess ->
            if (isSuccess == true) {
                Toast.makeText(this, "Transaksi Berhasil!", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        viewModel.toastMessage.observe(this) { msg ->
            if (msg != null && msg != "Transaksi berhasil disimpan!") {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi Pembantu: Hitung Total Realtime
    private fun calculateTotal(etQty: EditText, tvTotal: TextView) {
        val qty = etQty.text.toString().toIntOrNull() ?: 0
        val price = selectedProduct?.harga ?: 0.0
        val total = price * qty
        tvTotal.text = "Total: ${formatRupiah(total)}"
    }

    // Fungsi Pembantu: Format Rupiah
    private fun formatRupiah(number: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(number)
    }
}