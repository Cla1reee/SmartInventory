<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;
use Carbon\Carbon;

class BarangSeeder extends Seeder
{
    public function run(): void
    {
        DB::table('tb_barang')->insert([
            [
                'id_barang' => 'BRG-001',
                'nama_barang' => 'Kabel UTP Cat6',
                'stok_aktual' => 150,
                'qr_code_string' => 'HASH-BRG-001-UTP',
                'created_at' => Carbon::now(),
                'updated_at' => Carbon::now(),
            ],
            [
                'id_barang' => 'BRG-002',
                'nama_barang' => 'Router Mikrotik RB750',
                'stok_aktual' => 20,
                'qr_code_string' => 'HASH-BRG-002-MIK',
                'created_at' => Carbon::now(),
                'updated_at' => Carbon::now(),
            ],
            [
                'id_barang' => 'BRG-003',
                'nama_barang' => 'Switch Hub 8 Port',
                'stok_aktual' => 45,
                'qr_code_string' => 'HASH-BRG-003-HUB',
                'created_at' => Carbon::now(),
                'updated_at' => Carbon::now(),
            ]
        ]);
    }
}