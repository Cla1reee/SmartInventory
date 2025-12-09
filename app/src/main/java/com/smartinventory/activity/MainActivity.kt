package com.smartinventory.activity

import android.os.Bundle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.smartinventory.ui.theme.SmartInventoryTheme
import android.content.Intent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.smartinventory.R
import com.smartinventory.viewmodel.AuthViewModel
import android.widget.Toast
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // 1. Cek User Login
        if (viewModel.getCurrentUserUid() == null) {
            goToLogin()
            return
        }

        // 2. Inisialisasi Komponen UI
        val tvNamaToko = findViewById<TextView>(R.id.tvNamaToko)
        val tvUserEmail = findViewById<TextView>(R.id.tvUserEmail)

        val cardAdd = findViewById<CardView>(R.id.cardAdd)
        val cardList = findViewById<CardView>(R.id.cardList)
        // Ubah variabel ini sesuai ID baru di XML
        val cardCashier = findViewById<CardView>(R.id.cardCashier)
        val cardHistory = findViewById<CardView>(R.id.cardHistory) // ID Baru
        val cardLogout = findViewById<CardView>(R.id.cardLogout)

        // --- 1. Tombol TAMBAH BARANG (Kembali ke fungsi asli) ---
        cardAdd.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }

        // --- 2. Tombol STOK BARANG ---
        cardList.setOnClickListener {
            val intent = Intent(this, ProductListActivity::class.java)
            startActivity(intent)
        }

        // --- 3. Tombol KASIR (Fungsi Baru) ---
        cardCashier.setOnClickListener {
            val intent = Intent(this, CashierActivity::class.java)
            startActivity(intent)
        }

        // --- 4. Tombol LOGOUT ---
        cardLogout.setOnClickListener {
            viewModel.logout()
            Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
            goToLogin()
        }
        // --- 5. Tombol LAPORAN ---
        cardHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SmartInventoryTheme {
        Greeting("Android")
    }
}