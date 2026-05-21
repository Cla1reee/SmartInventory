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
        $table->id('id_transaksi');
        $table->string('id_barang', 10);
        $table->enum('jenis_transaksi', ['MASUK', 'KELUAR']);
        $table->integer('jumlah');
        $table->dateTime('waktu_scan');
        $table->boolean('status_sync')->default(true); // True artinya sudah masuk server
        $table->timestamps();

        // Foreign Key Constraint
        $table->foreign('id_barang')->references('id_barang')->on('tb_barang')->onDelete('cascade');
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
