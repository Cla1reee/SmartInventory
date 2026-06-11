<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use Illuminate\Support\Facades\DB;

class DashboardController extends Controller
{
    public function index()
    {
        // Menarik semua data barang dari database
        $inventory = DB::table('tb_barang')->orderBy('created_at', 'desc')->get();
        
        // Melempar data ke tampilan Blade
        return view('dashboard', compact('inventory'));
    }
}