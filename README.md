# EcoTrash: Sistem Pengelolaan Bank Sampah RT & Komunitas
> Proyek Akhir Pemrograman Berorientasi Objek (PBO) — S1 Teknologi Informasi  
> Dosen Pengampu: **Imam Adi Nata, S.Kom., M.Kom.**  
> Topik: **Pengelolaan Bank Sampah RT Berbasis OOP & Prinsip SOLID (v3 — Implementasi)**  
> Repositori: [https://github.com/zidhannnn1/ProyekAkhirPBO](https://github.com/zidhannnn1/ProyekAkhirPBO) (Rilis Tag: `v3`)

---

## 📁 Struktur Direktori Proyek

```text
proyekakhir/
├── ecotrash/                            # Proyek Resmi Java SE (Maven & Executable JAR)
│   ├── pom.xml                          # Konfigurasi Maven (Java 17 LTS, JUnit 5)
│   ├── ecotrash.jar                     # Standalone Executable JAR (Main: id.ac.kampus.ecotrash.App)
│   └── src/
│       ├── main/java/id/ac/kampus/ecotrash/
│       │   ├── model/                   # Domain Entities & Invariant Rules
│       │   │   ├── Pengguna.java        # Abstract Class Induk
│       │   │   ├── NasabahWarga.java    # Turunan Pengguna (Enkapsulasi Saldo & Poin)
│       │   │   ├── PetugasBankSampah.java# Turunan Pengguna (Operasional Loket RT)
│       │   │   ├── KategoriSampah.java  # Abstract Class Komoditas Sampah
│       │   │   ├── SampahAnorganik.java # Turunan Kategori (Plastik, Kardus, Logam)
│       │   │   ├── MinyakJelantah.java  # Turunan Kategori (Satuan Liter)
│       │   │   ├── DetailSetoran.java   # Komposisi (◆) Transaksi
│       │   │   ├── TransaksiSetoran.java# Pusat Proses Bisnis Setoran (◆ Komposisi)
│       │   │   └── BankSampahUnit.java  # Agregasi (◇) Kategori & Petugas
│       │   ├── reward/                  # Polimorfisme Skema Insentif (OCP & LSP)
│       │   │   ├── SkemaReward.java     # Interface Skema Reward
│       │   │   ├── BisaCairkanTunai.java# Interface Segregasi Penarikan Tunai (ISP/LSP)
│       │   │   ├── RewardTabunganRupiah.java # Saldo Kas Tunai
│       │   │   └── RewardPoinSembako.java   # Poin Belanja Sembako RT
│       │   ├── repository/              # Abstraksi Persistensi Data (DIP)
│       │   │   ├── TransaksiRepository.java # Interface Repository
│       │   │   └── MemoryTransaksiRepository.java
│       │   ├── service/                 # Logika Bisnis & Dependency Injection (SRP & DIP)
│       │   │   ├── BankSampahService.java   # Orkestrator Setoran & Pencairan Kas
│       │   │   └── NotifikasiService.java   # Interface Gateway Notifikasi
│       │   ├── ui/                      # Presentation Layer
│       │   │   └── BankSampahCLI.java   # Antarmuka Konsol & Cetak Slip Fisik
│       │   └── App.java                 # Main Assembler (Composition Root)
│       └── test/java/id/ac/kampus/ecotrash/
│           └── KodeUjiSederhana.java    # Test Suite Mandiri 6 Skenario (Berhasil & Gagal)
│
├── run_app.sh                           # Shell script peluncur aplikasi konsol Java
├── run_tests.sh                         # Shell script penjalan seluruh pengujian unit
├── web_app.py                           # Full-Stack Web Application Backend (Port 8080)
├── index.html                           # Frontend Antarmuka Loket Kasir Web
├── ecotrash_data.json                   # Penyimpanan Data Lokal Web App
├── ecotrash_class_diagram_v2.drawio     # Berkas XML Class Diagram UML Draw.io
├── PBO_Proyek_Tim23_EcoTrash_v3.pdf     # Dokumen Resmi Laporan Implementasi v3 (PDF)
├── PBO_Proyek_Tim23_EcoTrash_v3.docx    # Dokumen Resmi Laporan Implementasi v3 (DOCX)
├── PBO_Proyek_Tim23_EcoTrash.docx       # Dokumen Proposal Hidup & Bab Rancangan v2
└── PBO_TBP_Tim_LK.docx                  # Lembar Kerja Tim Pertemuan 9 (Audit SOLID)
```

---

## ☕ Cara Menjalankan Aplikasi Java (Konsol & Executable)

### 1. Menjalankan Demo Otomatis (App.java):
Aplikasi akan menjalankan simulasi 2 Fitur Utama (Fitur 1: Penimbangan & Setoran Sampah Polimorfis; Fitur 2: Pencairan Kas Tunai Berhasil & Ditolak) serta mencetak slip setoran fisik:
```bash
./run_app.sh
```
Atau menggunakan perintah Java langsung:
```bash
java -jar ecotrash/ecotrash.jar
```

### 2. Membuka Menu Loket Interaktif:
Untuk berinteraksi langsung melalui terminal kasir:
```bash
./run_app.sh --interactive
```

### 3. Menjalankan Seluruh Pengujian Unit (Test Suite Mandiri):
Menguji 6 skenario (berhasil & gagal per fitur, enkapsulasi aturan bisnis, dan polimorfisme reward):
```bash
./run_tests.sh
```

---

## 🚀 Cara Menjalankan Aplikasi Web Loket Kasir

Aplikasi web dapat langsung diakses melalui peramban:

1. **Jalankan Server Web**:
   ```bash
   python3 web_app.py
   ```
2. **Buka di Browser**:
   Buka URL: [http://localhost:8080](http://localhost:8080)

---

## 📊 Mengimpor Diagram Class ke Draw.io

1. Buka [app.diagrams.net](https://app.diagrams.net).
2. Pilih **File > Open From > Device**, lalu pilih berkas `ecotrash_class_diagram_v2.drawio`.
3. Atau pilih **Extras > Edit Diagram**, salin seluruh teks dari `ecotrash_class_diagram_v2.drawio`, lalu klik **OK**.
