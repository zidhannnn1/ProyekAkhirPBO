import os
import sys
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import nsdecls, qn

def set_cell_background(cell, fill_hex):
    tcPr = cell._tc.get_or_add_tcPr()
    shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{fill_hex}"/>')
    tcPr.append(shd)

def set_cell_margins(cell, top=100, bottom=100, left=150, right=150):
    tcPr = cell._tc.get_or_add_tcPr()
    tcMar = parse_xml(f'<w:tcMar {nsdecls("w")}><w:top w:w="{top}" w:type="dxa"/><w:bottom w:w="{bottom}" w:type="dxa"/><w:left w:w="{left}" w:type="dxa"/><w:right w:w="{right}" w:type="dxa"/></w:tcMar>')
    tcPr.append(tcMar)

def create_v3_docx():
    doc = Document()

    # Set Margins 1 inch
    for s in doc.sections:
        s.top_margin = Inches(1)
        s.bottom_margin = Inches(1)
        s.left_margin = Inches(1)
        s.right_margin = Inches(1)

    # Style colors
    PRIMARY = RGBColor(23, 56, 43)    # Forest Green
    SLATE = RGBColor(15, 23, 42)      # Slate Dark
    MUTED = RGBColor(71, 85, 105)     # Slate Muted

    # Title
    p_title = doc.add_paragraph()
    p_title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r_title = p_title.add_run("LAPORAN PROYEK TIM: IMPLEMENTASI v3\n")
    r_title.bold = True
    r_title.font.size = Pt(18)
    r_title.font.name = "Calibri"
    r_title.font.color.rgb = PRIMARY

    r_sub = p_title.add_run("ECOTRASH — SISTEM PENGELOLAAN BANK SAMPAH RT BERBASIS OOP & SOLID\n")
    r_sub.bold = True
    r_sub.font.size = Pt(13)
    r_sub.font.name = "Calibri"
    r_sub.font.color.rgb = SLATE

    r_meta = p_title.add_run("Mata Kuliah: Pemrograman Berorientasi Objek (PBO) — S1 Teknologi Informasi\n"
                             "Dosen Pengampu: Imam Adi Nata, S.Kom., M.Kom. | Tim 23\n"
                             "Repositori GitHub: https://github.com/zidhannnn1/ProyekAkhirPBO (Tag: v3)")
    r_meta.font.size = Pt(10)
    r_meta.font.italic = True
    r_meta.font.color.rgb = MUTED

    doc.add_paragraph()

    # 1. Ringkasan Implementasi v3
    h1 = doc.add_heading("1. Ringkasan Implementasi v3 (Menuju Demo Akhir)", level=2)
    h1.style.font.color.rgb = PRIMARY

    p = doc.add_paragraph(
        "Berdasarkan rancangan kelas diagram v2 dan hasil audit kelemahan v1 pada pertemuan sebelumnya, "
        "tahap Implementasi v3 mewujudkan seluruh cetak biru arsitektur berorientasi objek menjadi kode program "
        "Java SE 17 yang dapat dikompilasi, dijalankan (konsol App.java & web loket), dan diuji secara komprehensif. "
        "Implementasi ini secara ketat memenuhi seluruh indikator capaian pembelajaran Pertemuan 11:"
    )
    p.runs[0].font.size = Pt(11)

    bullets = [
        ("Kesesuaian Rancangan: ", "Seluruh 16 elemen UML (kelas, abstract class, interface, relasi asosiasi, agregasi, komposisi, dependensi, dan realisasi) diterjemahkan presisi ke dalam package modular."),
        ("Encapsulation & Validasi Aturan Bisnis: ", "Semua atribut entitas private final / private, mutasi data dilindungi method dengan validasi ketat (>0, non-empty, proteksi saldo minus), dan konstruktor package-private pada DetailSetoran untuk menegakkan komposisi penuh."),
        ("Inheritance & Abstraction: ", "Menerapkan abstract class Pengguna (dengan turunan NasabahWarga dan PetugasBankSampah) serta KategoriSampah (dengan turunan SampahAnorganik dan MinyakJelantah) dengan implementasi method abstrak @Override."),
        ("Polymorphism & Interface: ", "Memisahkan skema reward melalui interface SkemaReward (RewardTabunganRupiah dan RewardPoinSembako), interface segregasi BisaCairkanTunai (LSP), dan interface TransaksiRepository serta NotifikasiService (DIP)."),
        ("2 Fitur Utama Berjalan: ", "Fitur 1 (Penimbangan & Setoran Sampah Terpilah Multi-Komoditas) dan Fitur 2 (Pencairan Saldo Kas Tunai Nasabah) berjalan penuh dari model, reward, repository, service, hingga antarmuka CLI."),
        ("Pengujian Komprehensif: ", "Membuat kelas uji mandiri KodeUjiSederhana.java yang mencakup skenario berhasil dan gagal untuk setiap fitur, validasi aturan bisnis, serta polimorfisme reward.")
    ]
    for b_title, b_desc in bullets:
        bp = doc.add_paragraph(style='List Bullet')
        r1 = bp.add_run(b_title)
        r1.bold = True
        r1.font.size = Pt(10.5)
        r2 = bp.add_run(b_desc)
        r2.font.size = Pt(10.5)

    doc.add_paragraph()

    # 2. Tabel Pemetaan Class Diagram ke Berkas Kode Java
    h2 = doc.add_heading("2. Pemetaan Class Diagram UML ke Berkas Kode Java", level=2)
    h2.style.font.color.rgb = PRIMARY

    table = doc.add_table(rows=1, cols=4)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False

    hdr = table.rows[0].cells
    headers = ["Elemen UML", "Peran / Tanggung Jawab", "Jenis PBO / Relasi", "Path Berkas Java"]
    widths = [Inches(1.5), Inches(2.2), Inches(1.3), Inches(1.8)]

    for idx, name in enumerate(headers):
        hdr[idx].text = name
        hdr[idx].paragraphs[0].runs[0].bold = True
        hdr[idx].paragraphs[0].runs[0].font.size = Pt(10)
        hdr[idx].paragraphs[0].runs[0].font.color.rgb = RGBColor(255, 255, 255)
        set_cell_background(hdr[idx], "17382B")
        set_cell_margins(hdr[idx], 120, 120, 150, 150)
        hdr[idx].width = widths[idx]

    mapping_data = [
        ("«abstract» Pengguna", "Kelas abstrak induk identitas warga & petugas", "Abstract Class", "model/Pengguna.java"),
        ("NasabahWarga", "Nasabah penyetor sampah, pengelola saldo & poin", "Subkelas extends", "model/NasabahWarga.java"),
        ("PetugasBankSampah", "Petugas loket operasional balai RT", "Subkelas extends", "model/PetugasBankSampah.java"),
        ("«abstract» KategoriSampah", "Kelas abstrak komoditas sampah pilah", "Abstract Class", "model/KategoriSampah.java"),
        ("SampahAnorganik", "Sampah padat kering (botol PET, kardus, kaleng)", "Subkelas extends", "model/SampahAnorganik.java"),
        ("MinyakJelantah", "Limbah cair rumah tangga terukur Liter", "Subkelas extends", "model/MinyakJelantah.java"),
        ("DetailSetoran", "Baris penimbangan (kuantitas, tarif, subtotal)", "Komposisi (◆)", "model/DetailSetoran.java"),
        ("TransaksiSetoran", "Induk transaksi setoran warga, siklus status", "Agregat Komposisi", "model/TransaksiSetoran.java"),
        ("BankSampahUnit", "Induk unit bank sampah balai RT", "Agregasi (◇)", "model/BankSampahUnit.java"),
        ("«interface» SkemaReward", "Kontrak polimorfis konversi insentif warga", "Interface (OCP)", "reward/SkemaReward.java"),
        ("«interface» BisaCairkanTunai", "Kontrak kapabilitas pencairan kas tunai", "Interface (ISP/LSP)", "reward/BisaCairkanTunai.java"),
        ("RewardTabunganRupiah", "Implementasi insentif saldo kas tunai", "Implements 2 Interface", "reward/RewardTabunganRupiah.java"),
        ("RewardPoinSembako", "Implementasi insentif poin belanja sembako RT", "Implements SkemaReward", "reward/RewardPoinSembako.java"),
        ("«interface» TransaksiRepository", "Abstraksi penyimpanan data setoran", "Interface (DIP)", "repository/TransaksiRepository.java"),
        ("MemoryTransaksiRepository", "Penyimpanan in-memory thread-safe", "Implements Repository", "repository/MemoryTransaksiRepository.java"),
        ("«interface» NotifikasiService", "Abstraksi gateway konfirmasi setoran SMS/WA", "Interface (DIP)", "service/NotifikasiService.java"),
        ("BankSampahService", "Orkestrator alur transaksi & perputaran kas", "Service (SRP & DIP)", "service/BankSampahService.java"),
        ("BankSampahCLI", "Antarmuka konsol loket kasir & cetak slip", "Presentation Layer", "ui/BankSampahCLI.java"),
        ("App", "Composition Root perakit dependensi objek (DI)", "Main Assembler", "App.java")
    ]

    for row_idx, data in enumerate(mapping_data):
        row = table.add_row().cells
        bg_color = "F8FAFC" if row_idx % 2 == 1 else "FFFFFF"
        for c_idx, text in enumerate(data):
            row[c_idx].text = text
            row[c_idx].paragraphs[0].runs[0].font.size = Pt(9.5)
            row[c_idx].width = widths[c_idx]
            set_cell_background(row[c_idx], bg_color)
            set_cell_margins(row[c_idx], 80, 80, 120, 120)

    doc.add_paragraph()

    # 3. Penerapan 4 Pilar PBO
    h3 = doc.add_heading("3. Bukti Penerapan Empat Pilar PBO pada Kode Program", level=2)
    h3.style.font.color.rgb = PRIMARY

    # 3.1 Class & Object
    doc.add_heading("3.1 Class dan Object (Konstruktor Validasi & Immutability)", level=3)
    p = doc.add_paragraph(
        "Setiap objek diciptakan dalam status yang valid sejak awal melalui konstruktor. "
        "Atribut yang tidak boleh berubah setelah objek dibentuk dideklarasikan final, sementara mutasi data hanya dimungkinkan melalui method pengendali aturan bisnis."
    )
    p.runs[0].font.size = Pt(10.5)

    code1 = (
        "// Contoh pada TransaksiSetoran.java (Penegakan Komposisi ◆ & Validasi Invariant)\n"
        "public TransaksiSetoran(String kodeTransaksi, NasabahWarga nasabah, LocalDate tanggalSetor) {\n"
        "    if (kodeTransaksi == null || kodeTransaksi.isBlank()) {\n"
        "        throw new IllegalArgumentException(\"Kode transaksi tidak boleh kosong\");\n"
        "    }\n"
        "    if (nasabah == null) {\n"
        "        throw new IllegalArgumentException(\"Nasabah penyetor tidak boleh null\");\n"
        "    }\n"
        "    this.kodeTransaksi = kodeTransaksi;\n"
        "    this.nasabah = nasabah;\n"
        "    this.tanggalSetor = (tanggalSetor != null) ? tanggalSetor : LocalDate.now();\n"
        "    this.status = \"DIPROSES\";\n"
        "    this.daftarItem = new ArrayList<>(); // Komposisi: dibuat di dalam induk\n"
        "}"
    )
    cp1 = doc.add_paragraph()
    cr1 = cp1.add_run(code1)
    cr1.font.name = "Courier New"
    cr1.font.size = Pt(9)
    cp1.paragraph_format.left_indent = Inches(0.2)

    # 3.2 Encapsulation
    doc.add_heading("3.2 Encapsulation (Proteksi Data & 3+ Validasi Aturan Bisnis)", level=3)
    p = doc.add_paragraph(
        "Semua atribut bernilai private. Tiga aturan bisnis utama divalidasi ketat pada method pengendali:\n"
        "1. Validasi saldo kas saat penarikan (kurangiSaldo melempar exception jika nominal <= 0 dan menolak transaksi jika saldo kurang).\n"
        "2. Validasi kuantitas penimbangan sampah (tambahItem mewajibkan berat > 0).\n"
        "3. Validasi status transaksi (selesaikanTransaksi mewajibkan daftar item tidak kosong).\n"
        "Selain itu, getDaftarItem() dilindungi menggunakan Collections.unmodifiableList() agar daftar timbangan tidak bisa dimanipulasi dari luar."
    )
    p.runs[0].font.size = Pt(10.5)

    code2 = (
        "// Contoh pada NasabahWarga.java (Aturan Proteksi Kas & Poin)\n"
        "public boolean kurangiSaldo(double nominal) {\n"
        "    if (nominal <= 0) {\n"
        "        throw new IllegalArgumentException(\"Nominal penarikan saldo harus positif.\");\n"
        "    }\n"
        "    if (this.saldoTabungan >= nominal) {\n"
        "        this.saldoTabungan -= nominal;\n"
        "        return true; // Penarikan kas disetujui\n"
        "    }\n"
        "    return false; // Penarikan ditolak karena saldo tabungan tidak cukup\n"
        "}"
    )
    cp2 = doc.add_paragraph()
    cr2 = cp2.add_run(code2)
    cr2.font.name = "Courier New"
    cr2.font.size = Pt(9)
    cp2.paragraph_format.left_indent = Inches(0.2)

    # 3.3 Inheritance & Abstraction
    doc.add_heading("3.3 Inheritance dan Abstraction (Kelas Abstrak & Overriding)", level=3)
    p = doc.add_paragraph(
        "Sistem menerapkan dua hierarki kelas abstrak:\n"
        "- Pengguna: Mewariskan idPengguna, namaLengkap, noTelepon, nomorRT kepada NasabahWarga dan PetugasBankSampah, dengan method abstrak peran() yang wajib dioverride.\n"
        "- KategoriSampah: Mewariskan kodeSampah, namaKategori, hargaBeli, satuan kepada SampahAnorganik dan MinyakJelantah, dengan method abstrak hitungNilaiSubtotal(double)."
    )
    p.runs[0].font.size = Pt(10.5)

    code3 = (
        "// Contoh pada KategoriSampah.java & MinyakJelantah.java\n"
        "public abstract class KategoriSampah {\n"
        "    protected final String kodeSampah, namaKategori, satuan;\n"
        "    protected double hargaBeliPerSatuan;\n"
        "    public abstract double hitungNilaiSubtotal(double kuantitas);\n"
        "}\n\n"
        "public class MinyakJelantah extends KategoriSampah {\n"
        "    private final double kadarAsam;\n"
        "    public MinyakJelantah(String kode, double hargaBeli, double kadarAsam) {\n"
        "        super(kode, \"Minyak Jelantah Rumah Tangga\", hargaBeli, \"Liter\");\n"
        "        this.kadarAsam = kadarAsam;\n"
        "    }\n"
        "    @Override\n"
        "    public double hitungNilaiSubtotal(double volumeLiter) {\n"
        "        if (volumeLiter <= 0) return 0.0;\n"
        "        return volumeLiter * getHargaBeli();\n"
        "    }\n"
        "}"
    )
    cp3 = doc.add_paragraph()
    cr3 = cp3.add_run(code3)
    cr3.font.name = "Courier New"
    cr3.font.size = Pt(9)
    cp3.paragraph_format.left_indent = Inches(0.2)

    # 3.4 Polymorphism & Interface
    doc.add_heading("3.4 Polymorphism dan Interface (Dynamic Binding & Kontrak Segregasi)", level=3)
    p = doc.add_paragraph(
        "Polimorfisme diterapkan melalui interface SkemaReward. BankSampahService hanya mengenal tipe interface SkemaReward, "
        "sehingga penambahan skema baru (misal Token Listrik atau Zakat Sampah) cukup membuat kelas baru tanpa mengubah logika service (Open/Closed Principle). "
        "Selain itu, interface BisaCairkanTunai memisahkan kapabilitas pencairan tunai sehingga RewardPoinSembako tidak dipaksa mengimplementasikan penarikan uang (Liskov Substitution Principle)."
    )
    p.runs[0].font.size = Pt(10.5)

    code4 = (
        "// Penerapan Polimorfisme pada proses setoran di BankSampahService.java\n"
        "public void prosesSetoran(TransaksiSetoran tx, SkemaReward skema) {\n"
        "    tx.selesaikanTransaksi(skema); // Dynamic binding: mengeksekusi skema riil\n"
        "    repository.simpan(tx);\n"
        "    if (notifikasiService != null) {\n"
        "        String pesan = String.format(\"Setoran %s senilai Rp %.0f berhasil dengan skema [%s].\",\n"
        "                tx.getKodeTransaksi(), tx.hitungTotalNilai(), skema.getNamaSkema());\n"
        "        notifikasiService.kirimNotifikasi(tx.getNasabah().getNoTelepon(), pesan);\n"
        "    }\n"
        "}"
    )
    cp4 = doc.add_paragraph()
    cr4 = cp4.add_run(code4)
    cr4.font.name = "Courier New"
    cr4.font.size = Pt(9)
    cp4.paragraph_format.left_indent = Inches(0.2)

    doc.add_paragraph()

    # 4. Implementasi 2 Fitur Utama
    h4 = doc.add_heading("4. Implementasi Dua Fitur Utama (Alur Model -> Service -> UI)", level=2)
    h4.style.font.color.rgb = PRIMARY

    p = doc.add_paragraph(
        "Aplikasi mewujudkan dua alur proses bisnis inti dari loket bank sampah komunitas:\n"
        "1. Fitur 1 (Penimbangan & Setoran Sampah Terpilah): Petugas memilih nasabah ber-KTP/rekening RT, "
        "menambahkan satu atau lebih komoditas hasil timbangan (timbangan digital), menghitung akumulasi total berat dan nilai rupiah, "
        "serta mengonversi hasil setoran menjadi saldo tabungan kas rupiah atau poin reward belanja sembako.\n"
        "2. Fitur 2 (Pencairan Saldo Kas Tunai): Nasabah dapat mencairkan saldo tabungan kas hasil setoran sampah menjadi uang tunai di balai RT. "
        "Service memvalidasi kecukupan saldo tabungan sebelum kas diserahkan, menolak transaksi jika saldo tidak mencukupi, dan mencetak bukti penarikan."
    )
    p.runs[0].font.size = Pt(10.5)

    doc.add_paragraph()

    # 5. Hasil Pengujian
    h5 = doc.add_heading("5. Hasil Pengujian Kode Program (Skenario Berhasil & Gagal)", level=2)
    h5.style.font.color.rgb = PRIMARY

    p = doc.add_paragraph(
        "Sesuai rubrik evaluasi Pertemuan 11, seluruh fitur diuji minimal 2 skenario (skenario berhasil dan skenario gagal/validasi) "
        "menggunakan kelas uji mandiri KodeUjiSederhana.java. Hasil eksekusi menunjukkan seluruh 6 skenario lulus 100%:"
    )
    p.runs[0].font.size = Pt(10.5)

    test_table = doc.add_table(rows=1, cols=4)
    test_table.alignment = WD_TABLE_ALIGNMENT.CENTER
    test_hdr = test_table.rows[0].cells
    t_headers = ["ID Skenario", "Fokus Pengujian", "Ekspektasi & Kondisi Uji", "Hasil Eksekusi"]
    t_widths = [Inches(1.2), Inches(2.2), Inches(2.5), Inches(0.9)]

    for idx, name in enumerate(t_headers):
        test_hdr[idx].text = name
        test_hdr[idx].paragraphs[0].runs[0].bold = True
        test_hdr[idx].paragraphs[0].runs[0].font.size = Pt(10)
        test_hdr[idx].paragraphs[0].runs[0].font.color.rgb = RGBColor(255, 255, 255)
        set_cell_background(test_hdr[idx], "17382B")
        set_cell_margins(test_hdr[idx], 100, 100, 120, 120)
        test_hdr[idx].width = t_widths[idx]

    tests_data = [
        ("Skenario 1A", "Fitur 1: Setoran Sampah (Berhasil)", "4 kg Botol PET @ 4500 = Rp 18.000. Transaksi SELESAI, tersimpan di repo, saldo nasabah bertambah tepat Rp 18.000.", "LULUS"),
        ("Skenario 1B", "Fitur 1: Setoran Sampah (Gagal)", "Transaksi tanpa item timbangan diproses. Melempar IllegalStateException, status tidak berubah, repo tetap kosong.", "LULUS"),
        ("Skenario 2A", "Fitur 2: Pencairan Kas (Berhasil)", "Nasabah saldo Rp 50.000 mencairkan Rp 30.000. Fungsi return true, sisa saldo tabungan nasabah tepat Rp 20.000.", "LULUS"),
        ("Skenario 2B", "Fitur 2: Pencairan Kas (Gagal)", "Nasabah saldo Rp 15.000 mencairkan Rp 50.000. Fungsi return false, saldo nasabah tetap Rp 15.000 (tidak berkurang).", "LULUS"),
        ("Skenario 3", "Encapsulation & Validasi Bisnis", "Uji nominal mutasi negatif, kuantitas timbangan <= 0, dan ID kosong. Ketiganya melempar IllegalArgumentException.", "LULUS"),
        ("Skenario 4", "Polymorphism Skema Sembako", "Setoran Rp 25.000 skema sembako menghasilkan tepat 25 poin belanja sembako tanpa menambah saldo kas rupiah.", "LULUS")
    ]

    for r_idx, tdata in enumerate(tests_data):
        row = test_table.add_row().cells
        bg = "F8FAFC" if r_idx % 2 == 1 else "FFFFFF"
        for c_idx, val in enumerate(tdata):
            row[c_idx].text = val
            row[c_idx].paragraphs[0].runs[0].font.size = Pt(9.5)
            row[c_idx].width = t_widths[c_idx]
            set_cell_background(row[c_idx], bg)
            set_cell_margins(row[c_idx], 80, 80, 120, 120)

    doc.add_paragraph()

    # Keluaran Konsol
    doc.add_heading("5.1 Bukti Keluaran Program Konsol (App.java)", level=3)
    output_app = (
        "=================================================================\n"
        "       ECOTRASH — SISTEM PENGELOLAAN BANK SAMPAH RT KOMUNITAS    \n"
        "                Bank Sampah Berkah Mandiri - RT 04 / RW 02 Kelurahan Sukamaju\n"
        "=================================================================\n\n"
        "[KATALOG TARIF PEMBELIAN SAMPAH RT]\n"
        "-----------------------------------------------------------------\n"
        "Kode     | Nama Kategori Sampah           | Harga Beli   | Satuan\n"
        "-----------------------------------------------------------------\n"
        "SMP01    | Botol Plastik PET Bening       | Rp      4,500 | Kg    \n"
        "SMP02    | Kardus Bekas / Box Cokelat     | Rp      2,500 | Kg    \n"
        "SMP03    | Kaleng Minuman Aluminium       | Rp     12,000 | Kg    \n"
        "SMP04    | Minyak Jelantah Rumah Tangga   | Rp      6,500 | Liter \n"
        "-----------------------------------------------------------------\n\n"
        ">>> [DEMO FITUR 1] PENIMBANGAN & SETORAN SAMPAH\n"
        "[NOTIFIKASI SMS/WA ke 081298765432]: Setoran TRX-2026-001 senilai Rp 35250 diproses oleh Budi Santoso dengan skema [Tabungan Kas Rupiah].\n"
        "+---------------------------------------------------------------+\n"
        "| BUKTI SETORAN BANK SAMPAH RT (SLIP OPERASIONAL)               |\n"
        "+---------------------------------------------------------------+\n"
        "| Kode Transaksi : TRX-2026-001                                 |\n"
        "| Nasabah        : Ibu Siti Aminah                              |\n"
        "| No. Rekening   : REK-04-012                                   |\n"
        "| Status         : SELESAI                                      |\n"
        "+---------------------------------------------------------------+\n"
        "| TOTAL TIMBANGAN :  10.50 Kg/Liter                               |\n"
        "| TOTAL NILAI KAS : Rp    35,250.00                                 |\n"
        "+---------------------------------------------------------------+\n"
        "Saldo Tabungan Kas Ibu Siti: Rp 35,250.00\n\n"
        ">>> [DEMO FITUR 2] PENCAIRAN SALDO KAS TUNAI NASABAH\n"
        "[Skenario 2A: Pencairan Berhasil]\n"
        "Saldo awal Ibu Siti: Rp 35,250.00\n"
        "Memproses penarikan kas tunai sebesar Rp 20.000...\n"
        "Status Pencairan : BERHASIL (Uang Tunai Diserahkan)\n"
        "Sisa Saldo Kas   : Rp 15,250.00\n\n"
        "[Skenario 2B: Pencairan Ditolak (Saldo Kurang)]\n"
        "Saldo saat ini: Rp 15,250.00\n"
        "Mencoba penarikan kas tunai sebesar Rp 50.000...\n"
        "Status Pencairan : GAGAL DITOLAK (Saldo Tabungan Tidak Mencukupi)\n"
        "Saldo Kas Tetap  : Rp 15,250.00\n\n"
        "[REKAPITULASI KINERJA BANK SAMPAH]\n"
        "Total Timbangan Sampah Terkelola : 16.50 Kg/Liter\n"
        "Total Perputaran Nilai Kas Warga : Rp 85,250.00\n"
        "Jumlah Transaksi Tercatat        : 2 Transaksi"
    )
    cp5 = doc.add_paragraph()
    cr5 = cp5.add_run(output_app)
    cr5.font.name = "Courier New"
    cr5.font.size = Pt(8.5)
    cp5.paragraph_format.left_indent = Inches(0.2)

    doc.add_paragraph()

    # 6. Bukti Kolaborasi Git
    h6 = doc.add_heading("6. Bukti Kolaborasi Git & GitHub Flow", level=2)
    h6.style.font.color.rgb = PRIMARY

    p = doc.add_paragraph(
        "Pengembangan dilakukan mengikuti standar GitHub Flow kolaboratif:\n"
        "- Branching Strategy: Branch utama 'main' selalu dalam kondisi stabil (bisa dikompilasi dan diuji). "
        "Setiap fitur dan modul dikembangkan melalui branch terpisah: feature/setoran-sampah, feature/pencairan-saldo, dan feature/pengujian-unit.\n"
        "- Conventional Commits: Setiap commit memiliki pesan deskriptif sesuai tipe kontribusi (feat, test, refactor, docs).\n"
        "- Tag Rilis v3: Versi rilis v3 telah disematkan secara resmi pada commit rilis main melalui perintah 'git tag v3' dan di-push ke remote GitHub."
    )
    p.runs[0].font.size = Pt(10.5)

    doc.save("PBO_Proyek_Tim23_EcoTrash_v3.docx")
    print("DOCX PBO_Proyek_Tim23_EcoTrash_v3.docx berhasil dibuat.")

if __name__ == "__main__":
    create_v3_docx()

