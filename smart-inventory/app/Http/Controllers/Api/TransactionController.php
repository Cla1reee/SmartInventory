<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Carbon\Carbon;

class TransactionController extends Controller
{
    public function syncOfflineData(Request $request)
    {
        // 1. Validasi struktur JSON dari aplikasi Mobile
        $request->validate([
            'id_barang' => 'required|exists:tb_barang,id_barang',
            'jenis_transaksi' => 'required|in:MASUK,KELUAR',
            'jumlah' => 'required|integer|min:1',
            'waktu_scan' => 'required|date'
        ]);

        DB::beginTransaction();
        try {
            // 2. Ambil data barang saat ini
            $barang = DB::table('tb_barang')->where('id_barang', $request->id_barang)->first();
            
            // 3. Kalkulasi penambahan/pengurangan stok
            $stokBaru = $request->jenis_transaksi === 'MASUK' 
                        ? $barang->stok_aktual + $request->jumlah 
                        : $barang->stok_aktual - $request->jumlah;

            // Mencegah stok menjadi minus
            if ($stokBaru < 0) {
                return response()->json(['status' => 'error', 'message' => 'Stok tidak mencukupi untuk transaksi KELUAR.'], 400);
            }

            // 4. Update stok di tb_barang
            DB::table('tb_barang')->where('id_barang', $request->id_barang)->update(['stok_aktual' => $stokBaru]);

            // 5. Catat log ke tb_transaksi dengan status_sync = true (karena sudah online)
            DB::table('tb_transaksi')->insert([
                'id_barang' => $request->id_barang,
                'id_user' => auth()->guard('api')->user()->id,
                'jenis_transaksi' => $request->jenis_transaksi,
                'jumlah' => $request->jumlah,
                'waktu_scan' => Carbon::parse($request->waktu_scan),
                'status_sync' => true, 
                'created_at' => Carbon::now(),
                'updated_at' => Carbon::now(),
            ]);

            DB::commit();

            return response()->json([
                'status' => 'success',
                'message' => 'Data sinkronisasi berhasil disimpan.',
                'stok_terkini' => $stokBaru
            ], 200);

        } catch (\Exception $e) {
            DB::rollBack();
            return response()->json([
                'status' => 'error',
                'message' => 'Gagal melakukan sinkronisasi: ' . $e->getMessage()
            ], 500);
        }
    }
}