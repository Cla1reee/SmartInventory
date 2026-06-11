<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Web\DashboardController;

Route::get('/', function () {
    return redirect('/dashboard');
});

Route::get('/dashboard', [DashboardController::class, 'index']);
Route::get('/dashboard/tambah', [DashboardController::class, 'create']);
Route::post('/dashboard/simpan', [DashboardController::class, 'store']);