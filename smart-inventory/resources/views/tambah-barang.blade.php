<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tambah Barang - Smart Inventory</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-50 text-gray-800">
    <div class="flex min-h-screen">
        <aside class="w-64 bg-slate-800 text-white p-6">
            <h1 class="text-2xl font-bold mb-8">Smart Inventory</h1>
            <ul>
                <li class="mb-4"><a href="/dashboard" class="text-blue-400 font-semibold">Dashboard</a></li>
                <li class="mb-4"><a href="/dashboard" class="hover:text-blue-300">Inventory</a></li>
            </ul>
        </aside>

        <main class="flex-1 p-10">
            <div class="flex items-center justify-between mb-8">
                <div>
                    <h2 class="text-3xl font-bold text-gray-800">Tambah Barang Baru</h2>
                    <p class="text-gray-500 mt-2">Isi data barang untuk ditambahkan ke inventaris.</p>
                </div>
                <a href="/dashboard" class="bg-slate-800 text-white px-4 py-2 rounded shadow hover:bg-slate-900 transition">Kembali ke Dashboard</a>
            </div>

            <div class="bg-white rounded-lg shadow p-8 max-w-2xl mx-auto">
                <form action="/dashboard/simpan" method="POST" class="space-y-6">
                    @csrf

                    <div>
                        <label class="block text-sm font-semibold text-gray-700 mb-2">ID Barang</label>
                        <input type="text" name="id_barang" class="w-full border border-gray-300 rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-blue-500" required>
                    </div>

                    <div>
                        <label class="block text-sm font-semibold text-gray-700 mb-2">Nama Barang</label>
                        <input type="text" name="nama_barang" class="w-full border border-gray-300 rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-blue-500" required>
                    </div>

                    <div>
                        <label class="block text-sm font-semibold text-gray-700 mb-2">Stok Awal</label>
                        <input type="number" name="stok_aktual" class="w-full border border-gray-300 rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-blue-500" required>
                    </div>

                    <div class="flex justify-end gap-3">
                        <a href="/dashboard" class="inline-flex items-center justify-center px-5 py-3 rounded-lg border border-gray-300 text-sm font-semibold text-gray-700 hover:bg-gray-100 transition">Batal</a>
                        <button type="submit" class="inline-flex items-center justify-center px-5 py-3 rounded-lg bg-blue-600 text-white text-sm font-semibold hover:bg-blue-700 transition">Simpan Barang</button>
                    </div>
                </form>
            </div>
        </main>
    </div>
</body>
</html>
