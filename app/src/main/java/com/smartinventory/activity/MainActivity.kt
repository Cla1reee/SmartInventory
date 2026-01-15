package com.smartinventory.activity

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.smartinventory.R
import com.smartinventory.model.User

class MainActivity : AppCompatActivity() {

    // Inisialisasi Firebase
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Setup UI Components
        setupViews()
    }

    private fun setupViews() {
        // --- Referensi ID dari Layout XML Baru ---
        val tvShopName = findViewById<TextView>(R.id.tvShopName)

        val cardAddItem = findViewById<CardView>(R.id.cardAddItem)
        val cardStock = findViewById<CardView>(R.id.cardStock)
        val cardCashier = findViewById<CardView>(R.id.cardCashier)
        val cardReport = findViewById<CardView>(R.id.cardReport)

        val btnLogout = findViewById<LinearLayout>(R.id.btnLogout)

        // --- Load Data User (Nama Toko) ---
        loadUserProfile(tvShopName)

        // --- Event Listeners (Navigasi) ---

        // 1. Menu Tambah Barang
        cardAddItem.setOnClickListener {
            // Pastikan nama Activity ini sesuai dengan file Kotlin Anda
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        // 2. Menu Stok Barang
        cardStock.setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java))
        }

        // 3. Menu Kasir
        cardCashier.setOnClickListener {
            startActivity(Intent(this, CashierActivity::class.java))
        }

        // 4. Menu Laporan / Riwayat
        cardReport.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        // 5. Tombol Keluar
        btnLogout.setOnClickListener {
            showLogoutConfirmDialog()
        }
    }

    private fun loadUserProfile(tvName: TextView) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    tvName.text = user?.storeName ?: "Toko Kopi"
                }
            }
            .addOnFailureListener {
                // Silent error atau log jika perlu
                tvName.text = "Offline Mode"
            }
    }

    private fun showLogoutConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi")
            .setMessage(getString(R.string.msg_logout_confirm)) // Pastikan string ini ada di strings.xml
            .setPositiveButton("Ya") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun performLogout() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        // Hapus back stack agar user tidak bisa kembali ke Dashboard setelah logout
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}
