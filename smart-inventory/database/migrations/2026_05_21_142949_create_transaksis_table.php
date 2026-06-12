<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('tb_transaksi', function (Blueprint $table) {
            $table->id('id_transaksi'); // Primary Key
            
            // Foreign Key ke tb_barang
            $table->string('id_barang');
            $table->foreign('id_barang')->references('id_barang')->on('tb_barang')->onDelete('cascade');
            
            // Foreign Key ke users (Petugas yang login)
            $table->unsignedBigInteger('id_user');
            $table->foreign('id_user')->references('id')->on('users')->onDelete('cascade');
            
            $table->enum('jenis_transaksi', ['MASUK', 'KELUAR']);
            $table->integer('jumlah');
            $table->dateTime('waktu_scan');
            
            // Penanda sinkronisasi (True = berhasil masuk ke server)
            $table->boolean('status_sync')->default(true);
            
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('transaksis');
    }
};
