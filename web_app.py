#!/usr/bin/env python3
"""
EcoTrash — Sistem Pengelolaan Bank Sampah RT / Komunitas
Aplikasi Web Operasional & Pos Penimbangan Sampah Terpilah
Backend HTTP Server & REST API Handler
"""

import os
import sys
import json
from datetime import datetime
from http.server import HTTPServer, BaseHTTPRequestHandler
import urllib.parse

PORT = 8080
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DATA_FILE = os.path.join(BASE_DIR, "ecotrash_data.json")
HTML_FILE = os.path.join(BASE_DIR, "index.html")


class DataStore:
    def __init__(self, filepath=DATA_FILE):
        self.filepath = filepath
        self.kategori = []
        self.nasabah = []
        self.transaksi = []
        self.load()

    def seed_defaults(self):
        self.kategori = [
            {"kode": "SMP-01", "nama": "Botol Plastik PET Bening", "tipe": "Anorganik", "harga": 4500, "satuan": "Kg", "grade": "Kering, tanpa label"},
            {"kode": "SMP-02", "nama": "Kardus & Karton Box", "tipe": "Anorganik", "harga": 2500, "satuan": "Kg", "grade": "Lipat rapi, tidak basah"},
            {"kode": "SMP-03", "nama": "Kaleng Aluminium Minuman", "tipe": "Anorganik", "harga": 12000, "satuan": "Kg", "grade": "Pipih, bersih"},
            {"kode": "SMP-04", "nama": "Kertas HVS, Koran & Arsip", "tipe": "Anorganik", "harga": 2200, "satuan": "Kg", "grade": "Ikat tali, kering"},
            {"kode": "SMP-05", "nama": "Minyak Jelantah Dapur", "tipe": "Limbah Cair", "harga": 6500, "satuan": "Liter", "grade": "Disaring, jerigen tertutup"}
        ]
        self.nasabah = [
            {"id": "NSB-001", "nama": "Ibu Siti Aminah", "telp": "0812-9876-5432", "rt": "04", "rw": "02", "rekening": "BS-04-012", "saldo": 85000, "poin": 25},
            {"id": "NSB-002", "nama": "Pak Ahmad Fauzi", "telp": "0813-1122-3344", "rt": "04", "rw": "02", "rekening": "BS-04-018", "saldo": 142000, "poin": 60},
            {"id": "NSB-003", "nama": "Ibu Dewi Lestari", "telp": "0856-7788-9900", "rt": "04", "rw": "02", "rekening": "BS-04-025", "saldo": 45000, "poin": 10},
            {"id": "NSB-004", "nama": "Pak Bambang Suryo", "telp": "0818-2233-4455", "rt": "04", "rw": "02", "rekening": "BS-04-031", "saldo": 210000, "poin": 95}
        ]
        self.transaksi = [
            {
                "kode": "TRX-2026-001",
                "tanggal": "2026-11-10 08:30",
                "nasabahId": "NSB-001",
                "nasabahNama": "Ibu Siti Aminah",
                "nasabahRekening": "BS-04-012",
                "skema": "Tabungan Kas Tunai",
                "totalNilai": 45000,
                "totalBerat": 10.0,
                "items": [
                    # pyrefly: ignore[parse-error]
                    {"kode": "SMP-01", "nama": "Botol Plastik PET Bening", "berat": 6.0, "harga": 4500, "subtotal": 27000, "satuan": "Kg"},
                    {"kode": "SMP-02", "nama": "Kardus & Karton Box", "berat": 4.0, "harga": 2500, "subtotal": 10000, "satuan": "Kg"}
                ],
                "petugas": "Budi Santoso"
            },
            {
                "kode": "TRX-2026-002",
                "tanggal": "2026-11-12 09:15",
                "nasabahId": "NSB-002",
                "nasabahNama": "Pak Ahmad Fauzi",
                "nasabahRekening": "BS-04-018",
                "skema": "Poin Sembako RT",
                "totalNilai": 65000,
                "totalBerat": 10.0,
                "items": [
                    {"kode": "SMP-05", "nama": "Minyak Jelantah Dapur", "berat": 10.0, "harga": 6500, "subtotal": 65000, "satuan": "Liter"}
                ],
                "petugas": "Budi Santoso"
            }
        ]
        self.save()

    def load(self):
        if not os.path.exists(self.filepath):
            self.seed_defaults()
            return
        try:
            with open(self.filepath, "r", encoding="utf-8") as f:
                data = json.load(f)
                self.kategori = data.get("kategori", [])
                self.nasabah = data.get("nasabah", [])
                self.transaksi = data.get("transaksi", [])
        except Exception:
            self.seed_defaults()

    def save(self):
        data = {
            "kategori": self.kategori,
            "nasabah": self.nasabah,
            "transaksi": self.transaksi
        }
        with open(self.filepath, "w", encoding="utf-8") as f:
            json.dump(data, f, indent=2, ensure_ascii=False)


store = DataStore()


class EcoTrashHandler(BaseHTTPRequestHandler):
    def log_message(self, format, *args):
        print(f"[{datetime.now().strftime('%H:%M:%S')}] {args[0]} {args[1]}")

    def send_json(self, data, status=200):
        payload = json.dumps(data, ensure_ascii=False).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
        self.send_header("Access-Control-Allow-Headers", "Content-Type")
        self.send_header("Content-Length", str(len(payload)))
        self.end_headers()
        self.wfile.write(payload)

    def do_OPTIONS(self):
        self.send_response(200)
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
        self.send_header("Access-Control-Allow-Headers", "Content-Type")
        self.end_headers()

    def do_GET(self):
        url = urllib.parse.urlparse(self.path)
        path = url.path

        if path in ["/", "/index.html"]:
            if os.path.exists(HTML_FILE):
                with open(HTML_FILE, "rb") as f:
                    content = f.read()
                self.send_response(200)
                self.send_header("Content-Type", "text/html; charset=utf-8")
                self.send_header("Content-Length", str(len(content)))
                self.end_headers()
                self.wfile.write(content)
            else:
                self.send_response(404)
                self.end_headers()
                self.wfile.write(b"File index.html tidak ditemukan.")
            return

        elif path == "/api/kategori":
            self.send_json(store.kategori)
            return

        elif path == "/api/nasabah":
            self.send_json(store.nasabah)
            return

        elif path == "/api/transaksi":
            self.send_json(store.transaksi)
            return

        elif path == "/api/rekap":
            total_berat = sum(t.get("totalBerat", 0) for t in store.transaksi)
            total_kas = sum(t.get("totalNilai", 0) for t in store.transaksi)
            rekap = {
                "totalTonaseKg": round(total_berat, 2),
                "totalKasRupiah": round(total_kas, 2),
                "jumlahTransaksi": len(store.transaksi),
                "jumlahNasabah": len(store.nasabah)
            }
            self.send_json(rekap)
            return

        else:
            self.send_response(404)
            self.end_headers()
            self.wfile.write(b"Endpoint Tidak Ditemukan")

    def do_POST(self):
        url = urllib.parse.urlparse(self.path)
        path = url.path

        content_length = int(self.headers.get("Content-Length", 0))
        post_data = self.rfile.read(content_length)

        try:
            body = json.loads(post_data.decode("utf-8"))
        except Exception:
            body = {}

        if path == "/api/transaksi":
            nasabah_id = body.get("nasabahId")
            skema_type = body.get("skema", "rupiah")
            raw_items = body.get("items", [])

            nasabah = next((n for n in store.nasabah if n["id"] == nasabah_id), None)
            if not nasabah:
                self.send_json({"success": False, "message": "Nasabah tidak ditemukan."}, 400)
                return

            total_nilai = 0.0
            total_berat = 0.0
            processed_items = []

            for it in raw_items:
                kat = next((k for k in store.kategori if k["kode"] == it.get("kode")), None)
                if kat:
                    berat = float(it.get("berat", 0))
                    subtotal = kat["harga"] * berat
                    total_nilai += subtotal
                    total_berat += berat
                    processed_items.append({
                        "kode": kat["kode"],
                        "nama": kat["nama"],
                        "berat": berat,
                        "harga": kat["harga"],
                        "subtotal": subtotal,
                        "satuan": kat["satuan"]
                    })

            skema_label = "Tabungan Kas Tunai" if skema_type == "rupiah" else "Poin Sembako RT"

            if skema_type == "rupiah":
                nasabah["saldo"] += total_nilai
            else:
                poin = int(total_nilai / 1000)
                nasabah["poin"] += poin

            tx_kode = f"TRX-2026-{str(len(store.transaksi) + 1).zfill(3)}"
            tx = {
                "kode": tx_kode,
                "tanggal": datetime.now().strftime("%Y-%m-%d %H:%M"),
                "nasabahId": nasabah["id"],
                "nasabahNama": nasabah["nama"],
                "nasabahRekening": nasabah["rekening"],
                "skema": skema_label,
                "totalNilai": total_nilai,
                "totalBerat": total_berat,
                "items": processed_items,
                "petugas": "Budi Santoso"
            }

            store.transaksi.insert(0, tx)
            store.save()

            self.send_json({"success": True, "transaksi": tx})
            return

        elif path == "/api/nasabah":
            nama = body.get("nama")
            telp = body.get("telp")
            rt = body.get("rt", "04")

            if not nama or not telp:
                self.send_json({"success": False, "message": "Nama dan kontak wajib diisi."}, 400)
                return

            new_id = f"NSB-{str(len(store.nasabah) + 1).zfill(3)}"
            rek = f"BS-{rt}-{str(len(store.nasabah) + 1).zfill(3)}"
            new_nasabah = {
                "id": new_id,
                "nama": nama,
                "telp": telp,
                "rt": rt,
                "rw": "02",
                "rekening": rek,
                "saldo": 0,
                "poin": 0
            }
            store.nasabah.append(new_nasabah)
            store.save()

            self.send_json({"success": True, "nasabah": new_nasabah})
            return

        elif path == "/api/tarik-saldo":
            nasabah_id = body.get("id")
            nominal = float(body.get("nominal", 0))

            nasabah = next((n for n in store.nasabah if n["id"] == nasabah_id), None)
            if not nasabah:
                self.send_json({"success": False, "message": "Nasabah tidak ditemukan."}, 400)
                return

            if nasabah["saldo"] < nominal:
                self.send_json({"success": False, "message": "Saldo tabungan nasabah tidak mencukupi."}, 400)
                return

            nasabah["saldo"] -= nominal
            store.save()
            self.send_json({"success": True, "sisaSaldo": nasabah["saldo"]})
            return

        else:
            self.send_response(404)
            self.end_headers()
            self.wfile.write(b"Endpoint Tidak Ditemukan")


class QuietServer(HTTPServer):
    def handle_error(self, request, client_address):
        exc_type, exc_val, _ = sys.exc_info()
        if exc_type in (BrokenPipeError, ConnectionResetError):
            return
        super().handle_error(request, client_address)


def run_server():
    server_address = ("", PORT)
    httpd = QuietServer(server_address, EcoTrashHandler)
    print("=================================================================")
    print("  BANK SAMPAH BERKAH MANDIRI — SERVER OPERASIONAL AKTIF         ")
    print("=================================================================")
    print(f"  URL Loket Web: http://localhost:{PORT}")
    print("  Desain bebas AI-slop: antarmuka kasir administratif & slip fisik.")
    print("=================================================================")
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\n[INFO] Server dinonaktifkan.")


if __name__ == "__main__":
    run_server()
