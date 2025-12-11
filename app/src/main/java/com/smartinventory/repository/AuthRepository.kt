package com.smartinventory.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.smartinventory.model.User // Pastikan model User sudah dibuat

class AuthRepository {

    // 1. Inisialisasi Auth (Untuk Login/Register)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // 2. Inisialisasi Database (Untuk simpan Nama Toko) -- INI YANG DIBUTUHKAN KODE ANDA
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Nama koleksi di database
    private val USERS_COLLECTION = "users"

    // --- FUNGSI REGISTER (KODE PRO ANDA) ---
    fun register(email: String, password: String, namaToko: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val userId = authTask.result?.user?.uid

                    if (userId != null) {
                        // Simpan data tambahan ke Firestore
                        val newUser = User(
                            uId = userId,
                            email = email,
                            storeName = namaToko, // Perhatikan: Variabel input tetap 'namaToko', tapi dimasukkan ke 'storeName'
                            role = "admin",       // Default sesuai SRS
                            createdAt = java.util.Date() // Sesuai SRS
                        )

                        db.collection(USERS_COLLECTION)
                            .document(userId)
                            .set(newUser)
                            .addOnSuccessListener { callback(true, null) } // Sukses total
                            .addOnFailureListener { e ->
                                callback(false, "Pendaftaran berhasil, tetapi gagal menyimpan profil: ${e.message}")
                            }
                    } else {
                        callback(false, "UID tidak ditemukan.")
                    }
                } else {
                    callback(false, authTask.exception?.message ?: "Pendaftaran gagal.")
                }
            }
    }

    // --- FUNGSI LOGIN ---
    fun login(email: String, pass: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    // --- FUNGSI CEK USER ---
    fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }

    // --- FUNGSI LOGOUT ---
    fun logout() {
        auth.signOut()
    }
}