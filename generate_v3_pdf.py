import os
import sys
from reportlab.lib.pagesizes import A4
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, Preformatted, PageBreak, KeepTogether
from reportlab.pdfgen import canvas

class NumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super(NumberedCanvas, self).__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_number(num_pages)
            canvas.Canvas.showPage(self)
        canvas.Canvas.save(self)

    def draw_page_number(self, page_count):
        self.saveState()
        self.setFont("Helvetica", 8)
        self.setFillColor(colors.HexColor("#64748B"))
        # Header
        self.drawString(54, A4[1] - 36, "EcoTrash — Laporan Proyek Tim PBO v3 (Implementasi)")
        self.setStrokeColor(colors.HexColor("#CBD5E1"))
        self.setLineWidth(0.5)
        self.line(54, A4[1] - 42, A4[0] - 54, A4[1] - 42)
        # Footer
        page_text = f"Halaman {self._pageNumber} dari {page_count}"
        self.drawRightString(A4[0] - 54, 36, page_text)
        self.drawString(54, 36, "Teknologi Informasi — Dosen: Imam Adi Nata, S.Kom., M.Kom.")
        self.line(54, 48, A4[0] - 54, 48)
        self.restoreState()

def generate_pdf():
    pdf_filename = "PBO_Proyek_Tim23_EcoTrash_v3.pdf"
    doc = SimpleDocTemplate(
        pdf_filename,
        pagesize=A4,
        leftMargin=54,
        rightMargin=54,
        topMargin=54,
        bottomMargin=54
    )

    styles = getSampleStyleSheet()

    # Custom styles
    title_style = ParagraphStyle(
        'DocTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=16,
        leading=20,
        alignment=1, # Center
        textColor=colors.HexColor("#17382B")
    )

    sub_style = ParagraphStyle(
        'DocSub',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=11,
        leading=15,
        alignment=1,
        textColor=colors.HexColor("#0F172A")
    )

    meta_style = ParagraphStyle(
        'DocMeta',
        parent=styles['Normal'],
        fontName='Helvetica-Oblique',
        fontSize=9,
        leading=13,
        alignment=1,
        textColor=colors.HexColor("#475569")
    )

    h1_style = ParagraphStyle(
        'H1',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=12,
        leading=16,
        textColor=colors.HexColor("#17382B"),
        spaceBefore=14,
        spaceAfter=6
    )

    h2_style = ParagraphStyle(
        'H2',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=10,
        leading=14,
        textColor=colors.HexColor("#1F4E3D"),
        spaceBefore=10,
        spaceAfter=4
    )

    body_style = ParagraphStyle(
        'Body',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9,
        leading=13,
        textColor=colors.HexColor("#1E293B"),
        spaceAfter=6
    )

    bullet_style = ParagraphStyle(
        'Bullet',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8.5,
        leading=12,
        textColor=colors.HexColor("#1E293B"),
        leftIndent=15,
        spaceAfter=4
    )

    code_style = ParagraphStyle(
        'CodeSnippet',
        parent=styles['Normal'],
        fontName='Courier',
        fontSize=7.5,
        leading=9.5,
        textColor=colors.HexColor("#0F172A")
    )

    table_cell = ParagraphStyle(
        'TableCell',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8,
        leading=10,
        textColor=colors.HexColor("#1E293B")
    )

    table_hdr = ParagraphStyle(
        'TableHdr',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=8.5,
        leading=11,
        textColor=colors.white
    )

    story = []

    # Title block
    story.append(Paragraph("LAPORAN PROYEK TIM: IMPLEMENTASI v3", title_style))
    story.append(Spacer(1, 4))
    story.append(Paragraph("ECOTRASH — SISTEM PENGELOLAAN BANK SAMPAH RT BERBASIS OOP & SOLID", sub_style))
    story.append(Spacer(1, 4))
    story.append(Paragraph("Mata Kuliah: Pemrograman Berorientasi Objek (PBO) — S1 Teknologi Informasi<br/>"
                           "Dosen Pengampu: Imam Adi Nata, S.Kom., M.Kom. | Tim 23<br/>"
                           "Repositori GitHub: <u>https://github.com/zidhannnn1/ProyekAkhirPBO</u> (Rilis Tag: v3)", meta_style))
    story.append(Spacer(1, 14))

    # Section 1
    story.append(Paragraph("1. Ringkasan Implementasi v3 (Menuju Demo Akhir)", h1_style))
    story.append(Paragraph(
        "Berdasarkan rancangan kelas diagram v2 dan hasil audit kelemahan v1 pada pertemuan sebelumnya, "
        "tahap Implementasi v3 mewujudkan seluruh cetak biru arsitektur berorientasi objek menjadi kode program "
        "Java SE 17 yang dapat dikompilasi, dijalankan (konsol App.java & web loket), dan diuji secara komprehensif. "
        "Implementasi ini secara ketat memenuhi seluruh indikator capaian pembelajaran Pertemuan 11:", body_style
    ))

    bullets = [
        ("Kesesuaian Rancangan: ", "Seluruh 16 elemen UML (kelas, abstract class, interface, relasi asosiasi, agregasi, komposisi, dependensi, dan realisasi) diterjemahkan presisi ke dalam package modular."),
        ("Encapsulation & Validasi Aturan Bisnis: ", "Semua atribut entitas private, mutasi data dilindungi method dengan validasi ketat (>0, non-empty, proteksi saldo minus), dan konstruktor package-private pada DetailSetoran untuk menegakkan komposisi penuh."),
        ("Inheritance & Abstraction: ", "Menerapkan abstract class Pengguna (turunan: NasabahWarga, PetugasBankSampah) serta KategoriSampah (turunan: SampahAnorganik, MinyakJelantah) dengan implementasi method abstrak @Override."),
        ("Polymorphism & Interface: ", "Memisahkan skema reward melalui interface SkemaReward (RewardTabunganRupiah dan RewardPoinSembako), interface segregasi BisaCairkanTunai (LSP), dan interface TransaksiRepository serta NotifikasiService (DIP)."),
        ("2 Fitur Utama Berjalan: ", "Fitur 1 (Penimbangan & Setoran Sampah Terpilah Multi-Komoditas) dan Fitur 2 (Pencairan Saldo Kas Tunai Nasabah) berjalan penuh dari model, reward, repository, service, hingga antarmuka CLI."),
        ("Pengujian Komprehensif: ", "Membuat kelas uji mandiri KodeUjiSederhana.java yang mencakup skenario berhasil dan gagal untuk setiap fitur, validasi aturan bisnis, serta polimorfisme reward.")
    ]
    for b_title, b_desc in bullets:
        story.append(Paragraph(f"• <b>{b_title}</b>{b_desc}", bullet_style))

    story.append(Spacer(1, 10))

    # Section 2: Pemetaan UML ke Java
    story.append(Paragraph("2. Pemetaan Class Diagram UML ke Berkas Kode Java", h1_style))

    table_data = [
        [Paragraph("Elemen UML", table_hdr), Paragraph("Peran / Tanggung Jawab", table_hdr), Paragraph("Jenis PBO / Relasi", table_hdr), Paragraph("Path Berkas Java", table_hdr)]
    ]

    mapping = [
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

    for item in mapping:
        table_data.append([
            Paragraph(item[0], table_cell),
            Paragraph(item[1], table_cell),
            Paragraph(item[2], table_cell),
            Paragraph(item[3], table_cell)
        ])

    t = Table(table_data, colWidths=[105, 160, 95, 125])
    t.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor("#17382B")),
        ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
        ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
        ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#CBD5E1")),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, colors.HexColor("#F8FAFC")]),
        ('TOPPADDING', (0, 0), (-1, -1), 3),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
    ]))
    story.append(t)
    story.append(Spacer(1, 12))

    # Section 3: Penerapan 4 Pilar
    story.append(Paragraph("3. Bukti Penerapan Empat Pilar PBO pada Kode Program", h1_style))

    story.append(Paragraph("3.1 Class dan Object (Konstruktor Validasi & Invariant)", h2_style))
    story.append(Paragraph("Setiap objek dijamin lahir dalam keadaan valid melalui konstruktor. Atribut yang bersifat immutable dideklarasikan final.", body_style))
    code_text1 = (
        "// TransaksiSetoran.java: Komposisi ◆ & Invariant\n"
        "public TransaksiSetoran(String kodeTransaksi, NasabahWarga nasabah, LocalDate tanggalSetor) {\n"
        "    if (kodeTransaksi == null || kodeTransaksi.isBlank()) {\n"
        "        throw new IllegalArgumentException(\"Kode transaksi tidak boleh kosong\");\n"
        "    }\n"
        "    if (nasabah == null) throw new IllegalArgumentException(\"Nasabah tidak boleh null\");\n"
        "    this.kodeTransaksi = kodeTransaksi; this.nasabah = nasabah;\n"
        "    this.tanggalSetor = (tanggalSetor != null) ? tanggalSetor : LocalDate.now();\n"
        "    this.status = \"DIPROSES\";\n"
        "    this.daftarItem = new ArrayList<>(); // Komposisi: dibuat di dalam induk\n"
        "}"
    )
    t_code1 = Table([[Preformatted(code_text1, code_style)]], colWidths=[485])
    t_code1.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), colors.HexColor("#F1F5F9")),
        ('LEFTPADDING', (0, 0), (-1, -1), 8),
        ('RIGHTPADDING', (0, 0), (-1, -1), 8),
        ('TOPPADDING', (0, 0), (-1, -1), 6),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 6),
        ('BOX', (0, 0), (-1, -1), 0.5, colors.HexColor("#CBD5E1")),
    ]))
    story.append(t_code1)
    story.append(Spacer(1, 8))

    story.append(Paragraph("3.2 Encapsulation (Proteksi Data & Validasi Aturan Bisnis)", h2_style))
    story.append(Paragraph("Semua atribut bernilai private. Mutasi saldo kas dilindungi method dengan aturan validasi nominal > 0 dan saldo mencukupi:", body_style))
    code_text2 = (
        "// NasabahWarga.java: Mutator dilindungi aturan bisnis\n"
        "public boolean kurangiSaldo(double nominal) {\n"
        "    if (nominal <= 0) throw new IllegalArgumentException(\"Nominal penarikan saldo harus positif.\");\n"
        "    if (this.saldoTabungan >= nominal) {\n"
        "        this.saldoTabungan -= nominal;\n"
        "        return true; // Disetujui\n"
        "    }\n"
        "    return false; // Ditolak: saldo tabungan tidak cukup\n"
        "}"
    )
    t_code2 = Table([[Preformatted(code_text2, code_style)]], colWidths=[485])
    t_code2.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), colors.HexColor("#F1F5F9")),
        ('LEFTPADDING', (0, 0), (-1, -1), 8),
        ('RIGHTPADDING', (0, 0), (-1, -1), 8),
        ('TOPPADDING', (0, 0), (-1, -1), 6),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 6),
        ('BOX', (0, 0), (-1, -1), 0.5, colors.HexColor("#CBD5E1")),
    ]))
    story.append(t_code2)
    story.append(Spacer(1, 8))

    story.append(Paragraph("3.3 Inheritance dan Abstraction (Kelas Abstrak & Overriding)", h2_style))
    story.append(Paragraph("Kelas abstrak KategoriSampah menetapkan kontrak subtotal yang wajib diimplementasikan subkelas anorganik dan jelantah:", body_style))
    code_text3 = (
        "public abstract class KategoriSampah {\n"
        "    public abstract double hitungNilaiSubtotal(double kuantitas);\n"
        "}\n\n"
        "public class MinyakJelantah extends KategoriSampah {\n"
        "    @Override\n"
        "    public double hitungNilaiSubtotal(double volumeLiter) {\n"
        "        if (volumeLiter <= 0) return 0.0;\n"
        "        return volumeLiter * getHargaBeli();\n"
        "    }\n"
        "}"
    )
    t_code3 = Table([[Preformatted(code_text3, code_style)]], colWidths=[485])
    t_code3.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), colors.HexColor("#F1F5F9")),
        ('LEFTPADDING', (0, 0), (-1, -1), 8),
        ('RIGHTPADDING', (0, 0), (-1, -1), 8),
        ('TOPPADDING', (0, 0), (-1, -1), 6),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 6),
        ('BOX', (0, 0), (-1, -1), 0.5, colors.HexColor("#CBD5E1")),
    ]))
    story.append(t_code3)
    story.append(Spacer(1, 8))

    story.append(Paragraph("3.4 Polymorphism dan Interface (Dynamic Binding & DIP)", h2_style))
    story.append(Paragraph("BankSampahService hanya mengenal tipe interface SkemaReward, memungkinkan penambahan reward baru tanpa mengubah service:", body_style))
    code_text4 = (
        "// BankSampahService.java: Dynamic binding via interface\n"
        "public void prosesSetoran(TransaksiSetoran tx, SkemaReward skema) {\n"
        "    tx.selesaikanTransaksi(skema); // Mengeksekusi implementasi reward yang dipilih\n"
        "    repository.simpan(tx);\n"
        "    if (notifikasiService != null) {\n"
        "        notifikasiService.kirimNotifikasi(tx.getNasabah().getNoTelepon(),\n"
        "            \"Setoran \" + tx.getKodeTransaksi() + \" berhasil via \" + skema.getNamaSkema());\n"
        "    }\n"
        "}"
    )
    t_code4 = Table([[Preformatted(code_text4, code_style)]], colWidths=[485])
    t_code4.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), colors.HexColor("#F1F5F9")),
        ('LEFTPADDING', (0, 0), (-1, -1), 8),
        ('RIGHTPADDING', (0, 0), (-1, -1), 8),
        ('TOPPADDING', (0, 0), (-1, -1), 6),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 6),
        ('BOX', (0, 0), (-1, -1), 0.5, colors.HexColor("#CBD5E1")),
    ]))
    story.append(t_code4)
    story.append(Spacer(1, 10))

    # Section 4: 2 Fitur Utama
    story.append(Paragraph("4. Implementasi Dua Fitur Utama (Alur Model -> Service -> UI)", h1_style))
    story.append(Paragraph(
        "<b>Fitur 1 (Penimbangan & Setoran Sampah Terpilah):</b> Petugas menimbang multi-komoditas nasabah, "
        "kalkulasi total timbangan dan rupiah secara realtime, serta penyelesaian transaksi dengan skema Tabungan Rupiah atau Poin Sembako.<br/>"
        "<b>Fitur 2 (Pencairan Saldo Kas Tunai):</b> Nasabah dapat mencairkan saldo tabungan kas di loket balai RT. "
        "Validasi ketat menolak transaksi bila nominal melebihi sisa saldo tabungan nasabah.", body_style
    ))
    story.append(Spacer(1, 10))

    # Section 5: Hasil Pengujian
    story.append(Paragraph("5. Hasil Pengujian Kode Program (Skenario Berhasil & Gagal)", h1_style))
    story.append(Paragraph(
        "Seluruh fungsionalitas diuji melalui berkas KodeUjiSederhana.java (mencakup 2 skenario per fitur serta pengujian aturan bisnis):", body_style
    ))

    test_headers = [Paragraph("ID", table_hdr), Paragraph("Fokus Pengujian", table_hdr), Paragraph("Kondisi & Ekspektasi", table_hdr), Paragraph("Hasil", table_hdr)]
    test_rows = [test_headers]

    t_data = [
        ("Skenario 1A", "Fitur 1: Setoran Berhasil", "4 kg Botol PET @ 4500 = Rp 18.000. Status SELESAI, tersimpan di repo, saldo naik Rp 18.000.", "LULUS"),
        ("Skenario 1B", "Fitur 1: Setoran Gagal", "Transaksi tanpa item timbangan melempar IllegalStateException, status DIPROSES, repo kosong.", "LULUS"),
        ("Skenario 2A", "Fitur 2: Pencairan Berhasil", "Nasabah saldo Rp 50.000 cairkan Rp 30.000 -> return true, sisa saldo tepat Rp 20.000.", "LULUS"),
        ("Skenario 2B", "Fitur 2: Pencairan Gagal", "Nasabah saldo Rp 15.000 cairkan Rp 50.000 -> return false, saldo tetap Rp 15.000 (aman).", "LULUS"),
        ("Skenario 3", "Encapsulation & Validasi", "Uji nominal mutasi negatif, kuantitas <= 0, dan ID kosong. Ketiganya melempar exception.", "LULUS"),
        ("Skenario 4", "Polimorfisme Sembako", "Setoran Rp 25.000 skema sembako menghasilkan tepat 25 poin belanja tanpa menambah kas.", "LULUS")
    ]

    for td in t_data:
        test_rows.append([
            Paragraph(td[0], table_cell),
            Paragraph(td[1], table_cell),
            Paragraph(td[2], table_cell),
            Paragraph(td[3], table_cell)
        ])

    tt = Table(test_rows, colWidths=[65, 125, 230, 65])
    tt.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor("#17382B")),
        ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
        ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
        ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#CBD5E1")),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, colors.HexColor("#F8FAFC")]),
        ('TOPPADDING', (0, 0), (-1, -1), 3),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
    ]))
    story.append(tt)
    story.append(Spacer(1, 10))

    # Section 6: Kolaborasi Git
    story.append(Paragraph("6. Bukti Kolaborasi Git & GitHub Flow", h1_style))
    story.append(Paragraph(
        "• <b>Branching Model:</b> Pengembangan fitur diisolasi pada branch mandiri (feature/setoran-sampah, feature/pencairan-saldo, feature/pengujian-unit).<br/>"
        "• <b>Conventional Commits:</b> Pesan commit rapi dan terstandar (feat, test, refactor, docs).<br/>"
        "• <b>Tag Rilis:</b> Tag versi resmi 'v3' disematkan pada branch main di repositori GitHub.<br/>"
        "• <b>Tautan Remote:</b> https://github.com/zidhannnn1/ProyekAkhirPBO", body_style
    ))

    doc.build(story, canvasmaker=NumberedCanvas)
    print(f"PDF {pdf_filename} berhasil dibuat.")

if __name__ == "__main__":
    generate_pdf()
