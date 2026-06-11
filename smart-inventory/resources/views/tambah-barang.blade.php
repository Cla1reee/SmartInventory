<div class="p-10 max-w-lg mx-auto bg-white shadow rounded">
    <h2 class="text-2xl font-bold mb-6">Tambah Barang Baru</h2>
    <form action="/dashboard/simpan" method="POST">
        @csrf
        <div class="mb-4">
            <label class="block">ID Barang</label>
            <input type="text" name="id_barang" class="w-full border p-2 rounded" required>
        </div>
        <div class="mb-4">
            <label class="block">Nama Barang</label>
            <input type="text" name="nama_barang" class="w-full border p-2 rounded" required>
        </div>
        <div class="mb-4">
            <label class="block">Stok Awal</label>
            <input type="number" name="stok_aktual" class="w-full border p-2 rounded" required>
        </div>
        <button type="submit" class="bg-blue-600 text-white px-4 py-2 rounded">Simpan Barang</button>
    </form>
</div>