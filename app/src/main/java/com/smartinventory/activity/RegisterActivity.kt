package com.smartinventory.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.smartinventory.R
import com.smartinventory.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register) // Hubungkan ke XML yang baru dibuat

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // 1. Inisialisasi Komponen
        val etNamaToko = findViewById<EditText>(R.id.etNamaToko)
        val etEmail = findViewById<EditText>(R.id.etEmailReg)
        val etPass = findViewById<EditText>(R.id.etPassReg)
        val btnRegister = findViewById<Button>(R.id.btnRegisterSubmit)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        // 2. Tombol Daftar
        btnRegister.setOnClickListener {
            val namaToko = etNamaToko.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val pass = etPass.text.toString().trim()

            if (namaToko.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Semua data harus diisi!", Toast.LENGTH_SHORT).show()
            } else {
                // Panggil fungsi register di ViewModel
                viewModel.register(email, pass, namaToko)
            }
        }

        // 3. Tombol "Sudah punya akun" (Balik ke Login)
        tvLoginLink.setOnClickListener {
            finish() // Menutup halaman register, otomatis balik ke Login
        }

        // 4. Observer: Mendengarkan hasil dari Firebase
        viewModel.operationStatus.observe(this) { isSuccess ->
            // Cek null safety (karena sekarang bisa null)
            if (isSuccess == true) {
                finish() // Menutup halaman
            }
        }

        viewModel.message.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }
}