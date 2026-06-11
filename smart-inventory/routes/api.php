<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\TransactionController;

Route::post('/transaksi-sync', [TransactionController::class, 'syncOfflineData']);

Route::get('/user', function (Request $request) {
    return $request->user();
})->middleware('auth:sanctum');
