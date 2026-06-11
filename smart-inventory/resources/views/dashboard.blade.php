<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Smart Inventory Admin</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-50 text-gray-800">
    <div class="flex h-screen">
        <div class="w-64 bg-slate-800 text-white p-6">
            <h1 class="text-2xl font-bold mb-8">SmartStock AI</h1>
            <ul>
                <li class="mb-4"><a href="#" class="text-blue-400 font-semibold">Dashboard</a></li>
                <li class="mb-4"><a href="#" class="hover:text-blue-300">Inventory</a></li>
            </ul>
        </div>

        <div class="flex-1 p-10 overflow-y-auto">
            <div class="flex justify-between items-center mb-8">
                <h2 class="text-3xl font-bold text-gray-800">Inventory Overview</h2>
                <button class="bg-blue-600 text-white px-4 py-2 rounded shadow hover:bg-blue-700">
                    + Add New Product
                </button>
            </div>

            <div class="bg-white rounded-lg shadow overflow-hidden">
                <table class="w-full text-left border-collapse">
                    <thead>
                        <tr class="bg-gray-100 border-b">
                            <th class="p-4 font-semibold text-gray-600">ID BARANG</th>
                            <th class="p-4 font-semibold text-gray-600">NAMA PRODUK</th>
                            <th class="p-4 font-semibold text-gray-600">STOK AKTUAL</th>
                            <th class="p-4 font-semibold text-gray-600">AKSI</th>
                        </tr>
                    </thead>
                    <tbody>
                        @foreach($inventory as $item)
                        <tr class="border-b hover:bg-gray-50">
                            <td class="p-4 font-mono text-sm">{{ $item->id_barang }}</td>
                            <td class="p-4 font-medium">{{ $item->nama_barang }}</td>
                            <td class="p-4">
                                @if($item->stok_aktual < 30)
                                    <span class="text-red-600 font-bold">{{ $item->stok_aktual }} (Kritis)</span>
                                @else
                                    <span class="text-green-600 font-bold">{{ $item->stok_aktual }}</span>
                                @endif
                            </td>
                            <td class="p-4">
                                <button class="bg-indigo-100 text-indigo-700 px-3 py-1 rounded text-sm font-semibold">
                                    Print QR Code
                                </button>
                            </td>
                        </tr>
                        @endforeach
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>