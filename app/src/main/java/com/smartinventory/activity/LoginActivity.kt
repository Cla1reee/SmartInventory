package com.smartinventory.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.smartinventory.viewmodel.AuthViewModel
import com.smartinventory.R

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    // Deklarasi variabel UI
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegisterLink: TextView // 1. Variabel ini WAJIB ada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inisialisasi ViewModel
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // Cek Auto Login (Jika user sudah pernah login, langsung masuk Dashboard)
        if (viewModel.getCurrentUserUid() != null) {
            navigateToMain()
            return
        }

        // 2. Hubungkan Variable dengan ID di XML
        etEmail = findViewById(R.id.etUsername) // Pastikan di XML id-nya etUsername
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegisterLink = findViewById(R.id.tvRegisterLink) // 3. Hubungkan ID teks "Daftar"

        // Setup Tombol Login
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.login(email, password)
            }
        }

        // 4. Setup Link ke Halaman Register (INI YANG TADINYA BELUM JALAN)
        tvRegisterLink.setOnClickListener {
            // Pindah ke RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Observer: Mendengarkan respon sukses/gagal dari ViewModel
        viewModel.operationStatus.observe(this) { isSuccess ->
            if (isSuccess == true) {
                Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                navigateToMain()
            }
        }

        // Observer: Mendengarkan pesan error (misal: password salah)
        viewModel.message.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    // Fungsi Navigasi ke Dashboard
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        // Flag ini mencegah user kembali ke halaman Login saat tekan tombol Back
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}