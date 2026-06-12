<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Smart Scanner Mobile</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/html5-qrcode"></script>
</head>
<body class="bg-gray-900 text-white min-h-screen flex flex-col">

    <header class="p-4 bg-gray-800 shadow flex justify-between items-center">
        <h1 class="text-xl font-bold">SmartScanner</h1>
        <div id="network-status" class="px-3 py-1 rounded-full text-sm font-semibold bg-green-500 text-white">
            Online
        </div>
    </header>

    <main class="flex-1 p-4 flex flex-col items-center justify-center">
        <div id="reader" class="w-full max-w-sm bg-black rounded-lg overflow-hidden border-2 border-gray-700 shadow-lg"></div>
        <p class="mt-4 text-sm text-gray-400 text-center">Arahkan kamera ke QR Code barang</p>
        
        <div class="w-full max-w-sm mt-8">
            <h2 class="text-lg font-semibold mb-2 border-b border-gray-700 pb-1">Riwayat Scan:</h2>
            <ul id="scan-log" class="text-sm space-y-2 max-h-40 overflow-y-auto">
                <li class="text-gray-500 italic">Belum ada aktivitas...</li>
            </ul>
        </div>
    </main>

    <script>
        // Konfigurasi Elemen UI
        const networkBadge = document.getElementById('network-status');
        const scanLog = document.getElementById('scan-log');
        let html5QrcodeScanner;

        // Simulasi Token JWT Petugas (Untuk MVP Malam Ini)
        // Karena kita belum membuat halaman Login PWA, kita inject token dummy untuk pengujian.
        // Di sistem nyata, token ini didapat setelah login.
        const DUMMY_JWT_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vMTI3LjAuMC4xOjgwMDAvYXBpL2xvZ2luIiwiaWF0IjoxNzgxMjc4NjUxLCJleHAiOjE3ODEyODIyNTEsIm5iZiI6MTc4MTI3ODY1MSwianRpIjoiSERabzZJS0RkU3BLQVBXbiIsInN1YiI6IjEiLCJwcnYiOiIyM2JkNWM4OTQ5ZjYwMGFkYjM5ZTcwMWM0MDA4NzJkYjdhNTk3NmY3In0.7wHuQZ3ZIQR11jLv9BlxlWwZAruGiaHaKT7Q-hiQlu8"; 

        // 1. Logika Pemantau Jaringan (TODO 6)
        function updateNetworkStatus() {
            if (navigator.onLine) {
                networkBadge.textContent = 'Online';
                networkBadge.className = 'px-3 py-1 rounded-full text-sm font-semibold bg-green-500 text-white';
                syncOfflineData(); // Auto-sync saat sinyal kembali (TODO 7)
            } else {
                networkBadge.textContent = 'Offline (Blank Spot)';
                networkBadge.className = 'px-3 py-1 rounded-full text-sm font-semibold bg-red-600 text-white';
            }
        }

        window.addEventListener('online', updateNetworkStatus);
        window.addEventListener('offline', updateNetworkStatus);

        // 2. Logika Menyimpan & Mengirim Data (TODO 3)
        function processScanResult(qrCodeMessage) {
            // Jeda scanner sementara agar tidak scan berulang kali
            html5QrcodeScanner.pause(true);

            // Buat objek transaksi (Ambil ID Barang dari Hash "HASH-BRG-001" -> "BRG-001")
            const idBarang = qrCodeMessage.replace('HASH-', '');
            
            const payload = {
                id_barang: idBarang,
                jenis_transaksi: 'MASUK', // Default untuk MVP
                jumlah: 1,
                waktu_scan: new Date().toISOString().slice(0, 19).replace('T', ' ')
            };

            addLog(`Scanned: ${idBarang}`, 'text-blue-400');

            if (navigator.onLine) {
                // Sinyal Ada -> Tembak API
                sendDataToServer(payload);
            } else {
                // Sinyal Hilang -> Simpan ke LocalStorage
                saveToLocalStorage(payload);
                addLog(`Tersimpan lokal (Offline): ${idBarang}`, 'text-yellow-400');
                setTimeout(() => html5QrcodeScanner.resume(), 2000);
            }
        }

        function sendDataToServer(payload) {
            fetch('/api/transaksi-sync', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${DUMMY_JWT_TOKEN}`
                },
                body: JSON.stringify(payload)
            })
            .then(async response => {
                if(response.ok) {
                    addLog(`Sukses terkirim ke Server: ${payload.id_barang}`, 'text-green-400');
                } else {
                    // TANGKAP ERROR ASLI DARI LARAVEL
                    const errText = await response.text();
                    addLog(`Error ${response.status}: ${errText.substring(0, 60)}...`, 'text-red-500 font-bold');
                }
            })
            .catch(error => {
                addLog(`Gagal mengirim, server mati.`, 'text-red-400');
            })
            .finally(() => {
                setTimeout(() => html5QrcodeScanner.resume(), 2000);
            });
        }

        // 3. Logika LocalStorage (Pengganti SQLite)
        function saveToLocalStorage(payload) {
            let offlineQueue = JSON.parse(localStorage.getItem('offline_queue')) || [];
            offlineQueue.push(payload);
            localStorage.setItem('offline_queue', JSON.stringify(offlineQueue));
        }

        // 4. Logika Auto-Sync di Latar Belakang (TODO 4)
        function syncOfflineData() {
            let offlineQueue = JSON.parse(localStorage.getItem('offline_queue')) || [];
            if (offlineQueue.length > 0) {
                addLog(`Menyinkronkan ${offlineQueue.length} data tertunda...`, 'text-yellow-300');
                
                // Dalam MVP ini, kita loop tembakan API (Bisa dioptimasi menjadi Bulk Insert nantinya)
                offlineQueue.forEach((payload, index) => {
                    setTimeout(() => {
                        sendDataToServer(payload);
                    }, index * 1000); // Jeda 1 detik per tembakan
                });

                // Kosongkan antrean setelah dikirim
                localStorage.removeItem('offline_queue');
            }
        }

        function addLog(message, colorClass) {
            const time = new Date().toLocaleTimeString();
            const li = document.createElement('li');
            li.className = colorClass;
            li.innerHTML = `[${time}] ${message}`;
            if (scanLog.firstElementChild && scanLog.firstElementChild.innerText.includes('Belum ada')) {
                scanLog.innerHTML = ''; // Hapus teks default
            }
            scanLog.prepend(li);
        }

        // Inisialisasi Kamera HTML5
        document.addEventListener("DOMContentLoaded", () => {
            updateNetworkStatus(); // Cek status saat dimuat
            
            html5QrcodeScanner = new Html5QrcodeScanner(
                "reader", { fps: 10, qrbox: {width: 250, height: 250} }, /* verbose= */ false);
            
            html5QrcodeScanner.render(processScanResult, (errorMessage) => {
                // Abaikan error saat kamera sedang mencari frame
            });
        });
    </script>
</body>
</html>