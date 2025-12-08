package com.smartinventory.activity

import android.os.Bundle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.smartinventory.ui.theme.SmartInventoryTheme
import android.content.Intent
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.smartinventory.R
import com.smartinventory.viewmodel.AuthViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var tvUserInfo: TextView
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Inisialisasi ViewModel
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // 2. CEK KEAMANAN (Wajib)
        // Jika user belum login (uid null), paksa pindah ke LoginActivity
        if (viewModel.getCurrentUserUid() == null) {
            goToLogin()
            return
        }

        // 3. Inisialisasi UI
        tvUserInfo = findViewById(R.id.tvUserInfo)
        btnLogout = findViewById(R.id.btnLogout)

        // Tampilkan UID User sementara (bukti berhasil masuk)
        val currentUid = viewModel.getCurrentUserUid()
        tvUserInfo.text = "User ID: $currentUid"

        // 4. Logika Tombol Logout
        btnLogout.setOnClickListener {
            viewModel.logout() // Kita perlu tambahkan fungsi ini di ViewModel
            goToLogin()
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        // Hapus history agar saat diback tidak balik ke Dashboard
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