<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class DashboardController extends Controller
{
    // Menampilkan halaman dashboard utama
    public function index()
    {
        $inventory = DB::table('tb_barang')->orderBy('created_at', 'desc')->get();
        return view('dashboard', compact('inventory'));
    }

    // Menampilkan form tambah barang
    public function create()
    {
        return view('tambah-barang');
    }

    // Menyimpan data barang baru ke database
    public function store(Request $request)
    {
        // Validasi input
        $request->validate([
            'id_barang' => 'required|unique:tb_barang,id_barang|max:20',
            'nama_barang' => 'required|string|max:255',
            'stok_aktual' => 'required|numeric|min:0',
        ]);

        // Simpan ke database
        DB::table('tb_barang')->insert([
            'id_barang' => $request->id_barang,
            'nama_barang' => $request->nama_barang,
            'stok_aktual' => $request->stok_aktual,
            'qr_code_string' => 'HASH-' . $request->id_barang,
            'created_at' => now(),
            'updated_at' => now(),
        ]);

        // Kembali ke dashboard dengan pesan sukses
        return redirect('/dashboard')->with('success', 'Barang berhasil ditambahkan!');
    }
}