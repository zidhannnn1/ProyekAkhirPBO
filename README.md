# EcoTrash: Sistem Pengelolaan Bank Sampah RT & Komunitas
> Proyek Akhir Pemrograman Berorientasi Objek (PBO) — S1 Teknologi Informasi  
> Dosen Pengampu: **Imam Adi Nata, S.Kom., M.Kom.**  
> Topik: **Pengelolaan Bank Sampah RT Berbasis OOP & Prinsip SOLID (v2)**

---

## 📁 Struktur Direktori Proyek

```text
proyekakhir/
├── ecotrash/                            # Proyek Resmi Java SE (Maven)
│   ├── pom.xml                          # Konfigurasi Maven (Java 17 LTS, JUnit 5)
│   └── src/main/java/id/ac/kampus/ecotrash/
│       ├── model/                       # Domain Entities & Invariant Rules
│       │   ├── Pengguna.java            # Abstract Class Induk
│       │   ├── NasabahWarga.java        # Turunan Pengguna (Enkapsulasi Saldo & Poin)
│       │   ├── PetugasBankSampah.java   # Turunan Pengguna (Operasional RT)
│       │   ├── KategoriSampah.java      # Abstract Class Komoditas Sampah
│       │   ├── SampahAnorganik.java     # Turunan Kategori (Plastik, Kardus, Logam)
│       │   ├── MinyakJelantah.java      # Turunan Kategori (Satuan Liter)
│       │   ├── DetailSetoran.java       # Komposisi (◆) Transaksi
│       │   ├── TransaksiSetoran.java    # Pusat Proses Bisnis Setoran
│       │   └── BankSampahUnit.java      # Agregasi (◇) Kategori & Petugas
│       ├── reward/                      # Polimorfisme Skema Insentif (OCP & LSP)
│       │   ├── SkemaReward.java         # Interface Skema Reward
│       │   ├── BisaCairkanTunai.java    # Interface Segregasi Penarikan Tunai
│       │   ├── RewardTabunganRupiah.java# Implementasi Saldo Kas Tunai
│       │   └── RewardPoinSembako.java   # Implementasi Poin Belanja Sembako RT
│       ├── repository/                  # Abstraksi Persistensi Data (DIP)
│       │   ├── TransaksiRepository.java # Interface Repository
│       │   └── MemoryTransaksiRepository.java
│       ├── service/                     # Logika Bisnis & Dependency Injection
│       │   ├── BankSampahService.java   # Constructor Injection (Repo & Notif)
│       │   └── NotifikasiService.java   # Interface Notifikasi
│       ├── ui/                          # Presentation Layer
│       │   └── BankSampahCLI.java       # Antarmuka Konsol
│       └── App.java                     # Main Assembler (Composition Root)
│
├── web_app.py                           # Full-Stack Web Application (Port 8080)
├── ecotrash_data.json                   # Penyimpanan Data Lokal Web App
├── ecotrash_class_diagram_v2.drawio     # Berkas XML Class Diagram UML Draw.io
├── PBO_Proyek_Tim_EcoTrash_v2.docx      # Dokumen Proposal Hidup Lengkap (Bab 1-11)
├── PBO_TBP_Tim_Proposal.docx            # Dokumen Proposal Resmi
└── PBO_TBP_Tim_LK.docx                  # Lembar Kerja Tim Pertemuan 9 (Audit SOLID)
```

---

## 🚀 Cara Menjalankan Aplikasi Web

Aplikasi web telah aktif dan dapat langsung diakses melalui peramban:

1. **Jalankan Server Web**:
   ```bash
   python3 web_app.py
   ```
2. **Buka di Browser**:
   Buka URL: [http://localhost:8080](http://localhost:8080)

### Fitur Interaktif pada Web App:
- **Input Penimbangan**: Pencatatan setoran multi-item dengan kalkulasi subtotal realtime.
- **Pilihan Skema Reward**: Konversi otomatis ke Saldo Kas Rupiah atau Poin Sembako RT.
- **Buku Rekening Nasabah**: Cek saldo tabungan warga, histori setoran, dan penarikan tunai.
- **Katalog Tarif Sampah**: Tabel harga beli komoditas terpilah per kg/liter.
- **Rekapitulasi Kas & Tonase**: Statistik total sampah terkelola dan kas beredar RT.

---

## ☕ Cara Membuka & Menjalankan Proyek Java

1. Buka folder `ecotrash` di IDE pilihan Anda (**IntelliJ IDEA**, **Visual Studio Code**, atau **Apache NetBeans**).
2. Jalankan berkas utama:
   `id.ac.kampus.ecotrash.App`
3. Atau melalui terminal dengan Apache Maven:
   ```bash
   cd ecotrash
   mvn clean compile
   mvn exec:java
   ```

---

## 📊 Mengimpor Diagram Class ke Draw.io

1. Buka [app.diagrams.net](https://app.diagrams.net).
2. Pilih **File > Open From > Device**, lalu pilih berkas `ecotrash_class_diagram_v2.drawio`.
3. Atau pilih **Extras > Edit Diagram**, salin seluruh teks dari `ecotrash_class_diagram_v2.drawio`, lalu klik **OK**.

