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
            <h1 class="text-2xl font-bold mb-8">Smart Inventory</h1>
            <ul>
                <li class="mb-4"><a href="#" class="text-blue-400 font-semibold">Dashboard</a></li>
                <li class="mb-4"><a href="#" class="hover:text-blue-300">Inventory</a></li>
            </ul>
        </div>

        <div class="flex-1 p-10 overflow-y-auto">
            <div class="flex justify-between items-center mb-8">
                <h2 class="text-3xl font-bold text-gray-800">Inventory Overview</h2>
                <a href="/dashboard/tambah" class="bg-blue-600 text-white px-4 py-2 rounded shadow hover:bg-blue-700">
                    + Add New Product
                </a>
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
                                <button onclick="generateQR('{{ $item->nama_barang }}', '{{ $item->qr_code_string }}')" class="bg-indigo-100 text-indigo-700 px-3 py-1 rounded text-sm font-semibold hover:bg-indigo-200 transition">
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

    <div id="qrModal" class="fixed inset-0 bg-black bg-opacity-50 hidden flex items-center justify-center">
        <div class="bg-white p-8 rounded-lg shadow-xl text-center max-w-sm w-full">
            <h3 class="text-xl font-bold mb-4 text-gray-800" id="qrTitle">QR Code Barang</h3>
            <div id="qrcode-container" class="flex justify-center mb-6 border p-4 bg-gray-50"></div>
            <p class="text-sm font-mono text-gray-500 mb-6" id="qrHash">Hash String</p>
            <div class="flex justify-between space-x-4">
                <button onclick="closeModal()" class="w-full bg-gray-200 text-gray-800 px-4 py-2 rounded hover:bg-gray-300 font-semibold">Tutup</button>
                <button onclick="window.print()" class="w-full bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 font-semibold">Cetak</button>
            </div>
        </div>
    </div>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/qrcodejs/1.0.0/qrcode.min.js"></script>
    
    <script>
        const modal = document.getElementById('qrModal');
        const qrContainer = document.getElementById('qrcode-container');
        const qrTitle = document.getElementById('qrTitle');
        const qrHash = document.getElementById('qrHash');

        function generateQR(namaBarang, hashString) {
            // Bersihkan QR Code lama jika ada
            qrContainer.innerHTML = '';
            
            // Set teks judul dan hash di modal
            qrTitle.innerText = namaBarang;
            qrHash.innerText = hashString;

            // Generate QR Code baru
            new QRCode(qrContainer, {
                text: hashString,
                width: 200,
                height: 200,
                colorDark : "#0f172a", 
                colorLight : "#ffffff",
                correctLevel : QRCode.CorrectLevel.H
            });

            // Tampilkan Modal
            modal.classList.remove('hidden');
        }

        function closeModal() {
            modal.classList.add('hidden');
        }
    </script>
</body>
</html>