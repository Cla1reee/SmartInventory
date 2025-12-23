package com.smartinventory.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.smartinventory.R
import com.smartinventory.model.User

class MainActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Inisialisasi View (Sesuaikan ID dengan XML Anda)
        val tvStoreName = findViewById<TextView>(R.id.tvStoreName) // ID untuk "Nama Toko Anda"
        val tvEmail = findViewById<TextView>(R.id.tvUserEmail)     // ID untuk "email@toko.com"

        // Tombol-tombol Menu
        val btnAdd = findViewById<CardView>(R.id.cardAdd)   // Asumsi pakai CardView
        val btnStock = findViewById<CardView>(R.id.cardStock)
        val btnCashier = findViewById<CardView>(R.id.cardCashier)
        val btnReport = findViewById<CardView>(R.id.cardHistory)
        val btnLogout = findViewById<CardView>(R.id.cardLogout)

        // 2. Load Data User (Nama Toko & Email)
        loadUserProfile(tvStoreName, tvEmail)

        // 3. Navigasi Tombol (Sesuai Struktur Project Anda)
        btnAdd.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        btnStock.setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java)) // Atau Activity Stok
        }

        btnCashier.setOnClickListener {
            startActivity(Intent(this, CashierActivity::class.java))
        }

        btnReport.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Tutup Dashboard agar tidak bisa diback
        }
    }

    private fun loadUserProfile(tvName: TextView, tvEmail: TextView) {
        val userId = auth.currentUser?.uid
        if (userId == null) return

        // Ambil data dari collection 'users'
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Konversi ke object User (Pastikan Model User.kt sudah benar field-nya)
                    val user = document.toObject(User::class.java)

                    // Tampilkan ke Layar
                    tvName.text = user?.storeName ?: "Nama Toko Tidak Ditemukan"
                    tvEmail.text = user?.email ?: "Email Tidak Ditemukan"
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memuat profil", Toast.LENGTH_SHORT).show()
            }
    }
}