package com.smartinventory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.smartinventory.repository.AuthRepository

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    // Menggunakan Boolean? (Nullable) agar bisa kita reset statusnya
    private val _operationStatus = MutableLiveData<Boolean?>()
    val operationStatus: LiveData<Boolean?> = _operationStatus

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    // --- FUNGSI REGISTER ---
    fun register(email: String, pass: String, namaToko: String) {
        // 1. Reset status agar bersih sebelum mulai
        _operationStatus.value = null
        _message.value = null

        // 2. Validasi awal
        if (email.isEmpty() || pass.isEmpty() || namaToko.isEmpty()) {
            _message.value = "Data tidak boleh kosong!"
            return
        }

        // 3. Panggil Repository
        repository.register(email, pass, namaToko) { success, errorMsg ->
            // --- BAGIAN INI SANGAT PENTING ---
            // Gunakan .postValue() agar aman dipanggil dari background thread (Firebase)
            if (success) {
                _message.postValue("Registrasi Berhasil! Silakan Login.")
                _operationStatus.postValue(true) // Kirim sinyal SUKSES ke Activity
            } else {
                _message.postValue(errorMsg ?: "Registrasi Gagal")
                _operationStatus.postValue(false)
            }
        }
    }

    // --- FUNGSI LOGIN ---
    fun login(email: String, pass: String) {
        _operationStatus.value = null
        _message.value = null

        if (email.isEmpty() || pass.isEmpty()) {
            _message.value = "Email dan Password harus diisi!"
            return
        }

        repository.login(email, pass) { success, errorMsg ->
            if (success) {
                _message.postValue("Login Berhasil")
                _operationStatus.postValue(true)
            } else {
                _message.postValue(errorMsg ?: "Login Gagal")
                _operationStatus.postValue(false)
            }
        }
    }

    // Fungsi tambahan
    // Cek User Sedang Login (Untuk Auto-Login di Splash Screen)
    fun isUserLoggedIn(): Boolean {
        return repository.getCurrentUserUid() != null
    }
    fun getCurrentUserUid() = repository.getCurrentUserUid()
    fun logout() = repository.logout()
}
