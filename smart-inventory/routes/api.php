<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\TransactionController;
use App\Http\Controllers\Api\AuthController;

// Endpoint publik (Tidak butuh token)
Route::post('/login', [AuthController::class, 'login']);

// Endpoint privat (Butuh token JWT)
Route::middleware('auth:api')->group(function () {
    Route::post('/transaksi-sync', [TransactionController::class, 'syncOfflineData']);
});

Route::middleware('auth:api')->group(function () {
    Route::post('/transaksi-sync', [TransactionController::class, 'syncOfflineData']);
});

Route::get('/user', function (Request $request) {
    return $request->user();
})->middleware('auth:sanctum');
