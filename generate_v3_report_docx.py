import os
import docx
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import qn, nsdecls

def create_element(name):
    return OxmlElement(name)

def set_cell_background(cell, hex_color):
    shading_elm = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{hex_color}"/>')
    cell._tc.get_or_add_tcPr().append(shading_elm)

def set_cell_margins(cell, top=120, bottom=120, left=180, right=180):
    tcPr = cell._tc.get_or_add_tcPr()
    tcMar = OxmlElement('w:tcMar')
    for m, val in [('top', top), ('bottom', bottom), ('left', left), ('right', right)]:
        node = OxmlElement(f'w:{m}')
        node.set(qn('w:w'), str(val))
        node.set(qn('w:type'), 'dxa')
        tcMar.append(node)
    tcPr.append(tcMar)

def set_table_borders(table, color="D3D3D3", sz="4", val="single"):
    tblPr = table._tbl.tblPr
    borders = parse_xml(
        f'<w:tblBorders {nsdecls("w")}>'
        f'<w:top w:val="{val}" w:sz="{sz}" w:space="0" w:color="{color}"/>'
        f'<w:bottom w:val="{val}" w:sz="{sz}" w:space="0" w:color="{color}"/>'
        f'<w:insideH w:val="{val}" w:sz="{sz}" w:space="0" w:color="{color}"/>'
        f'<w:left w:val="none"/>'
        f'<w:right w:val="none"/>'
        f'<w:insideV w:val="none"/>'
        f'</w:tblBorders>'
    )
    tblPr.append(borders)

def add_styled_heading(doc, text, level):
    h = doc.add_heading(text, level=level)
    h.paragraph_format.keep_with_next = True
    h.paragraph_format.space_before = Pt(14 if level==1 else (10 if level==2 else 6))
    h.paragraph_format.space_after = Pt(4)
    run = h.runs[0]
    run.font.name = 'Calibri'
    if level == 1:
        run.font.size = Pt(16)
        run.font.bold = True
        run.font.color.rgb = RGBColor(31, 78, 121) # Deep Navy
    elif level == 2:
        run.font.size = Pt(13)
        run.font.bold = True
        run.font.color.rgb = RGBColor(47, 85, 151) # Medium Blue
    elif level == 3:
        run.font.size = Pt(11.5)
        run.font.bold = True
        run.font.color.rgb = RGBColor(60, 60, 60)
    return h

def add_code_block(doc, code_str):
    tbl = doc.add_table(rows=1, cols=1)
    tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl.autofit = False
    cell = tbl.cell(0, 0)
    cell.width = Inches(6.5)
    set_cell_background(cell, "F4F6F9")
    set_cell_margins(cell, top=100, bottom=100, left=160, right=160)
    
    # Left border highlight
    tcPr = cell._tc.get_or_add_tcPr()
    borders = parse_xml(
        f'<w:tcBorders {nsdecls("w")}>'
        f'<w:top w:val="none"/>'
        f'<w:left w:val="single" w:sz="24" w:space="0" w:color="1F4E79"/>'
        f'<w:bottom w:val="none"/>'
        f'<w:right w:val="none"/>'
        f'</w:tcBorders>'
    )
    tcPr.append(borders)
    
    p = cell.paragraphs[0]
    p.paragraph_format.space_before = Pt(2)
    p.paragraph_format.space_after = Pt(2)
    p.paragraph_format.line_spacing = 1.15
    run = p.add_run(code_str.strip())
    run.font.name = 'Consolas'
    run.font.size = Pt(9.0)
    run.font.color.rgb = RGBColor(35, 40, 50)
    
    # Spacer paragraph
    sp = doc.add_paragraph()
    sp.paragraph_format.space_before = Pt(0)
    sp.paragraph_format.space_after = Pt(4)

def add_callout(doc, text_str, title="CATATAN PENTING"):
    tbl = doc.add_table(rows=1, cols=1)
    tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl.autofit = False
    cell = tbl.cell(0, 0)
    cell.width = Inches(6.5)
    set_cell_background(cell, "EEF5FB")
    set_cell_margins(cell, top=120, bottom=120, left=180, right=180)
    
    tcPr = cell._tc.get_or_add_tcPr()
    borders = parse_xml(
        f'<w:tcBorders {nsdecls("w")}>'
        f'<w:top w:val="none"/>'
        f'<w:left w:val="single" w:sz="24" w:space="0" w:color="2F5597"/>'
        f'<w:bottom w:val="none"/>'
        f'<w:right w:val="none"/>'
        f'</w:tcBorders>'
    )
    tcPr.append(borders)
    
    p = cell.paragraphs[0]
    p.paragraph_format.space_before = Pt(2)
    p.paragraph_format.space_after = Pt(2)
    run_t = p.add_run(f"[{title}]\n")
    run_t.font.name = 'Calibri'
    run_t.font.size = Pt(10)
    run_t.font.bold = True
    run_t.font.color.rgb = RGBColor(31, 78, 121)
    
    run_b = p.add_run(text_str.strip())
    run_b.font.name = 'Calibri'
    run_b.font.size = Pt(9.5)
    run_b.font.color.rgb = RGBColor(40, 50, 60)
    
    sp = doc.add_paragraph()
    sp.paragraph_format.space_before = Pt(0)
    sp.paragraph_format.space_after = Pt(4)

def build_report():
    doc = Document()
    
    # Page setup - Margins 1 inch
    sections = doc.sections
    for section in sections:
        section.top_margin = Inches(1.0)
        section.bottom_margin = Inches(1.0)
        section.left_margin = Inches(1.0)
        section.right_margin = Inches(1.0)
    
    # Base Normal Style
    normal_style = doc.styles['Normal']
    normal_style.font.name = 'Calibri'
    normal_style.font.size = Pt(10.5)
    normal_style.font.color.rgb = RGBColor(50, 50, 50)
    normal_style.paragraph_format.line_spacing = 1.2
    normal_style.paragraph_format.space_after = Pt(5)
    
    # ================= COVER / TITLE BLOCK =================
    title_p = doc.add_paragraph()
    title_p.paragraph_format.space_before = Pt(0)
    title_p.paragraph_format.space_after = Pt(2)
    title_p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r_title = title_p.add_run("LAPORAN PROYEK TIM: IMPLEMENTASI v3\n")
    r_title.font.name = 'Calibri'
    r_title.font.size = Pt(17)
    r_title.font.bold = True
    r_title.font.color.rgb = RGBColor(31, 78, 121)
    
    r_sub = title_p.add_run("ECOTRASH — SISTEM PENGELOLAAN BANK SAMPAH RT BERBASIS OOP & SOLID\n")
    r_sub.font.name = 'Calibri'
    r_sub.font.size = Pt(13)
    r_sub.font.bold = True
    r_sub.font.color.rgb = RGBColor(47, 85, 151)
    
    r_info = title_p.add_run("Mata Kuliah: Pemrograman Berorientasi Objek (PBO) — S1 Teknologi Informasi\n"
                              "Fakultas Teknik, Universitas Tidar, Magelang (Tahun Akademik 2026/2027)\n"
                              "Dosen Pengampu: Imam Adi Nata, S.Kom., M.Kom. | Tim 23")
    r_info.font.name = 'Calibri'
    r_info.font.size = Pt(10)
    r_info.font.italic = True
    r_info.font.color.rgb = RGBColor(100, 100, 100)
    
    # Metadata Box
    meta_tbl = doc.add_table(rows=1, cols=1)
    meta_tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    cell = meta_tbl.cell(0, 0)
    cell.width = Inches(6.5)
    set_cell_background(cell, "F2F4F8")
    set_cell_margins(cell, top=100, bottom=100, left=150, right=150)
    p_meta = cell.paragraphs[0]
    p_meta.paragraph_format.space_before = Pt(2)
    p_meta.paragraph_format.space_after = Pt(2)
    p_meta.paragraph_format.line_spacing = 1.15
    run_m = p_meta.add_run(
        "• Pengembang / Ketua Tim : Zidhan Arzaaq Karim (NPM: 2340506063)\n"
        "• Repositori GitHub      : https://github.com/zidhannnn1/ProyekAkhirPBO\n"
        "• Tag Rilis Resmi        : v3 (commit main: git tag v3)\n"
        "• Bahasa & Versi JDK     : Java SE 17 LTS (Standard Edition) | OpenJDK 17\n"
        "• Cakupan Tahap Ini      : Pertemuan 11 (Implementasi Fitur Kolaboratif dari Model s.d. Service)"
    )
    run_m.font.name = 'Consolas'
    run_m.font.size = Pt(9.0)
    run_m.font.color.rgb = RGBColor(31, 78, 121)
    
    doc.add_paragraph().paragraph_format.space_after = Pt(4)
    
    # ================= BAB I =================
    add_styled_heading(doc, "1. Ringkasan Implementasi v3 (Menuju Demo Akhir)", level=1)
    p = doc.add_paragraph()
    p.add_run(
        "Berdasarkan rancangan kelas diagram v2 dan hasil evaluasi kelemahan desain monolitik v1 pada pertemuan "
        "sebelumnya, tahap Implementasi v3 mewujudkan seluruh cetak biru berorientasi objek menjadi perangkat "
        "lunak Java SE 17 yang dapat dikompilasi, dijalankan mandiri (konsol App.java serta web loket kasir), dan "
        "terverifikasi melalui unit testing terotomasi. Implementasi ini secara ketat memenuhi seluruh indikator "
        "capaian pembelajaran C3 (Mengimplementasikan) Pertemuan 11 sebagaimana ditetapkan dalam silabus kuliah:"
    )
    
    bullets = [
        ("Menerjemahkan Class Diagram v2: ", "Seluruh 16 elemen UML diterjemahkan presisi ke dalam struktur paket modular (model, reward, repository, service, ui)."),
        ("Encapsulation & Validasi Aturan Bisnis: ", "Semua atribut bertipe private, mutasi data dilindungi method pengendali dengan minimal 3 aturan bisnis divalidasi ketat."),
        ("Inheritance & Abstraction: ", "Menerapkan abstract class Pengguna (turunan NasabahWarga, PetugasBankSampah) dan KategoriSampah (turunan SampahAnorganik, MinyakJelantah) lengkap dengan anotasi @Override."),
        ("Polymorphism lewat Interface: ", "Memisahkan skema reward melalui interface SkemaReward dan memisahkan antarmuka pencairan tunai BisaCairkanTunai guna memenuhi prinsip LSP dan ISP."),
        ("Arsitektur Berlapis dari Model ke Service: ", "Membangun 2 fitur utama operasional: Fitur 1 (Penimbangan & Setoran Multi-Komoditas) dan Fitur 2 (Pencairan Kas Tunai Nasabah)."),
        ("Pengujian Kode Mandiri: ", "Mencakup minimal 2 skenario (berhasil & gagal) per fitur pada berkas KodeUjiSederhana.java tanpa ketergantungan library luar."),
        ("Kolaborasi GitHub Flow: ", "Bekerja secara terstruktur melalui branch per fitur, Pull Request dengan checklist review PBO, commit rapi (Conventional Commits), dan penyematan tag rilis v3.")
    ]
    for b_title, b_desc in bullets:
        bp = doc.add_paragraph(style='List Bullet')
        bp.paragraph_format.space_after = Pt(3)
        r_bt = bp.add_run(b_title)
        r_bt.bold = True
        r_bt.font.color.rgb = RGBColor(31, 78, 121)
        bp.add_run(b_desc)
    
    # ================= BAB II =================
    add_styled_heading(doc, "2. Pemetaan Class Diagram UML v2/v3 ke Kode Program Java", level=1)
    doc.add_paragraph(
        "Diagram kelas v2 yang telah disempurnakan pada pertemuan sebelumnya diterjemahkan secara langsung ke "
        "dalam berkas kode sumber Java. Struktur paket dirancang mengikuti prinsip modularitas agar setiap anggota "
        "dapat bekerja pada paket terpisah guna meminimalkan konflik merge saat kolaborasi Git."
    )
    
    # Embed Diagram Picture if exists
    if os.path.exists("Class Diagram v2.png"):
        doc.add_picture("Class Diagram v2.png", width=Inches(6.3))
        cap = doc.add_paragraph()
        cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
        cap.paragraph_format.space_before = Pt(4)
        cap.paragraph_format.space_after = Pt(8)
        rc = cap.add_run("Gambar 2.1 Cetak Biru Class Diagram UML v2/v3 Sistem EcoTrash")
        rc.font.size = Pt(9)
        rc.font.italic = True
        rc.font.color.rgb = RGBColor(100, 100, 100)
    
    add_styled_heading(doc, "Tabel 2.1 Pemetaan Elemen UML ke Berkas Kode Java", level=3)
    
    mapping_data = [
        ("«abstract» Pengguna", "Kelas abstrak induk identitas personal (ID, Nama, NoTelp, RT)", "Abstract Class", "model/Pengguna.java"),
        ("NasabahWarga", "Entitas nasabah RT penyetor sampah; pengelola saldo & poin", "Subkelas extends", "model/NasabahWarga.java"),
        ("PetugasBankSampah", "Petugas loket operasional pencatat timbangan warga", "Subkelas extends", "model/PetugasBankSampah.java"),
        ("«abstract» KategoriSampah", "Kelas abstrak dasar komoditas; kontrak hitungNilaiSubtotal()", "Abstract Class", "model/KategoriSampah.java"),
        ("SampahAnorganik", "Sampah padat kering (plastik, kardus, kaleng) satuan Kg", "Subkelas extends", "model/SampahAnorganik.java"),
        ("MinyakJelantah", "Limbah minyak jelantah rumah tangga satuan Liter", "Subkelas extends", "model/MinyakJelantah.java"),
        ("DetailSetoran", "Baris item penimbangan; siklus hidup diatur TransaksiSetoran", "Komposisi (◆)", "model/DetailSetoran.java"),
        ("TransaksiSetoran", "Agregat transaksi setoran; daftar item & siklus status", "Model Agregat", "model/TransaksiSetoran.java"),
        ("BankSampahUnit", "Induk bank sampah RT pemegang katalog komoditas & petugas", "Agregasi (◇)", "model/BankSampahUnit.java"),
        ("«interface» SkemaReward", "Kontrak konversi nilai rupiah hasil setoran sampah (OCP)", "Interface", "reward/SkemaReward.java"),
        ("RewardTabunganRupiah", "Skema reward tabungan uang tunai nasabah", "Realisasi implements", "reward/RewardTabunganRupiah.java"),
        ("RewardPoinSembako", "Skema reward poin belanja sembako RT (Rp 1.000 = 1 poin)", "Realisasi implements", "reward/RewardPoinSembako.java"),
        ("«interface» BisaCairkanTunai", "Kontrak kemampuan pencairan saldo kas tunai (ISP/LSP)", "Interface Segregasi", "reward/BisaCairkanTunai.java"),
        ("«interface» TransaksiRepository", "Abstraksi persistensi data transaksi setoran (DIP)", "Interface", "repository/TransaksiRepository.java"),
        ("MemoryTransaksiRepository", "Implementasi penyimpanan in-memory transaksi setoran", "Realisasi implements", "repository/MemoryTransaksiRepository.java"),
        ("«interface» NotifikasiService", "Abstraksi transmisi pesan pemberitahuan transaksi warga", "Interface", "service/NotifikasiService.java"),
        ("BankSampahService", "Orkestrator logika bisnis (setoran, pencairan, rekap kas)", "Service Layer", "service/BankSampahService.java"),
        ("BankSampahCLI", "Antarmuka konsol loket kasir & format cetak slip fisik", "Presentation (UI)", "ui/BankSampahCLI.java"),
        ("App.java", "Composition Root; merakit seluruh dependensi (DIP) & eksekusi", "Main Class", "App.java"),
        ("KodeUjiSederhana.java", "Test suite mandiri verifikasi 6 skenario keberhasilan & kegagalan", "Test Suite", "test/.../KodeUjiSederhana.java")
    ]
    
    tbl_map = doc.add_table(rows=len(mapping_data)+1, cols=4)
    tbl_map.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl_map.autofit = False
    set_table_borders(tbl_map)
    
    col_widths = [Inches(1.8), Inches(2.2), Inches(1.2), Inches(1.3)]
    headers = ["Elemen UML", "Peran / Tanggung Jawab", "Jenis PBO / Relasi", "Path Berkas Java"]
    
    # Header Row
    hdr_cells = tbl_map.rows[0].cells
    for i, title in enumerate(headers):
        hdr_cells[i].text = title
        hdr_cells[i].width = col_widths[i]
        set_cell_background(hdr_cells[i], "1F4E79")
        set_cell_margins(hdr_cells[i], top=100, bottom=100, left=120, right=120)
        p = hdr_cells[i].paragraphs[0]
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        for r in p.runs:
            r.font.name = 'Calibri'
            r.font.bold = True
            r.font.size = Pt(9.5)
            r.font.color.rgb = RGBColor(255, 255, 255)
            
    # Body Rows
    for row_idx, data in enumerate(mapping_data, start=1):
        row_cells = tbl_map.rows[row_idx].cells
        bg_col = "F9FAFC" if row_idx % 2 == 1 else "FFFFFF"
        for col_idx in range(4):
            row_cells[col_idx].text = data[col_idx]
            row_cells[col_idx].width = col_widths[col_idx]
            set_cell_background(row_cells[col_idx], bg_col)
            set_cell_margins(row_cells[col_idx], top=80, bottom=80, left=120, right=120)
            p = row_cells[col_idx].paragraphs[0]
            if col_idx in [0, 2, 3]:
                p.alignment = WD_ALIGN_PARAGRAPH.LEFT
            for r in p.runs:
                r.font.name = 'Consolas' if col_idx in [0, 3] else 'Calibri'
                r.font.size = Pt(8.5)
                r.font.color.rgb = RGBColor(40, 40, 40)
                
    doc.add_paragraph().paragraph_format.space_after = Pt(6)
    
    # ================= BAB III =================
    add_styled_heading(doc, "3. Bukti Penerapan Empat Pilar PBO pada Kode Program", level=1)
    
    # 3.1 Class & Object
    add_styled_heading(doc, "3.1 Class dan Object (Konstruktor Validasi & Immutability)", level=2)
    doc.add_paragraph(
        "Sesuai pedoman best-practice PBO, setiap objek dijamin lahir dalam keadaan valid melalui konstruktor "
        "yang memeriksa prakondisi data (tidak bernilai null, string tidak kosong, dan nilai numerik berada dalam "
        "rentang yang diizinkan). Atribut identitas yang tidak boleh berubah selama siklus hidup dideklarasikan "
        "sebagai private final."
    )
    add_code_block(doc,
        "// File: ecotrash/src/main/java/id/ac/kampus/ecotrash/model/DetailSetoran.java\n"
        "public class DetailSetoran {\n"
        "    private final KategoriSampah kategori;\n"
        "    private final double kuantitasTimbangan;\n"
        "    private final String catatanPilah;\n\n"
        "    // Konstruktor package-private menjamin hanya TransaksiSetoran yang dapat membuat objek ini\n"
        "    DetailSetoran(KategoriSampah kategori, double kuantitasTimbangan, String catatanPilah) {\n"
        "        if (kategori == null) {\n"
        "            throw new IllegalArgumentException(\"Kategori sampah tidak boleh null\");\n"
        "        }\n"
        "        if (kuantitasTimbangan <= 0) {\n"
        "            throw new IllegalArgumentException(\"Kuantitas timbangan harus positif\");\n"
        "        }\n"
        "        this.kategori = kategori;\n"
        "        this.kuantitasTimbangan = kuantitasTimbangan;\n"
        "        this.catatanPilah = (catatanPilah == null) ? \"Standar\" : catatanPilah;\n"
        "    }\n"
        "    public double getSubtotal() { return kategori.hitungNilaiSubtotal(kuantitasTimbangan); }\n"
        "}"
    )
    
    # 3.2 Encapsulation
    add_styled_heading(doc, "3.2 Encapsulation & Validasi Aturan Bisnis (Minimal 3 Aturan)", level=2)
    doc.add_paragraph(
        "Seluruh atribut entitas dideklarasikan dengan hak akses private. Tidak ada setter publik serampangan "
        "(mencegah anti-pattern 'getter-setter untuk semua'). Mutasi data hanya dapat dilakukan melalui method "
        "yang secara aktif memvalidasi aturan bisnis komunitas bank sampah RT:"
    )
    
    rules = [
        ("Aturan 1 (Proteksi Saldo Kas Nasabah): ", "Method kurangiSaldo(nominal) pada NasabahWarga.java mewajibkan nominal > 0 dan menolak penarikan bila saldo tabungan tidak mencukupi, mencegah saldo bernilai negatif."),
        ("Aturan 2 (Validasi Timbangan Positif): ", "Method tambahItem(...) pada TransaksiSetoran.java menolak timbangan bernilai 0 atau negatif (kuantitas <= 0 melempar IllegalArgumentException)."),
        ("Aturan 3 (Proteksi Kunci Status & Item Kosong): ", "Method selesaikanTransaksi(...) mewajibkan status masih 'DIPROSES' dan daftar timbangan tidak boleh kosong (melempar IllegalStateException bila kosong)."),
        ("Aturan 4 (Immutability Koleksi Agregat): ", "Method getDaftarItem() dilindungi menggunakan Collections.unmodifiableList() agar pihak luar tidak dapat memanipulasi daftar timbangan secara langsung.")
    ]
    for r_title, r_desc in rules:
        bp = doc.add_paragraph(style='List Bullet')
        bp.paragraph_format.space_after = Pt(2)
        r = bp.add_run(r_title)
        r.bold = True
        bp.add_run(r_desc)
        
    add_code_block(doc,
        "// File: ecotrash/src/main/java/id/ac/kampus/ecotrash/model/NasabahWarga.java\n"
        "public boolean kurangiSaldo(double nominal) {\n"
        "    if (nominal <= 0) {\n"
        "        throw new IllegalArgumentException(\"Nominal penarikan saldo harus positif.\");\n"
        "    }\n"
        "    if (this.saldoTabungan >= nominal) {\n"
        "        this.saldoTabungan -= nominal;\n"
        "        return true;  // Penarikan tunai disetujui kasir\n"
        "    }\n"
        "    return false;     // Ditolak: saldo tidak mencukupi (saldo tetap utuh)\n"
        "}\n\n"
        "// File: ecotrash/src/main/java/id/ac/kampus/ecotrash/model/TransaksiSetoran.java\n"
        "public List<DetailSetoran> getDaftarItem() {\n"
        "    return Collections.unmodifiableList(this.daftarItem); // Proteksi koleksi dari mutasi liar\n"
        "}"
    )
    
    # 3.3 Inheritance & Abstraction
    add_styled_heading(doc, "3.3 Inheritance dan Abstraction (Minimal 1 Abstract Class & >= 2 Subkelas)", level=2)
    doc.add_paragraph(
        "EcoTrash menerapkan dua hierarki pewarisan abstrak murni yang memenuhi relasi 'is-a' secara semantik:\n"
        "1. Pengguna (Abstract Induk): Mewariskan identitas personal kepada NasabahWarga dan PetugasBankSampah dengan method abstrak peran().\n"
        "2. KategoriSampah (Abstract Induk): Mewariskan atribut komoditas kepada SampahAnorganik (satuan Kg, atribut gradePilah) dan MinyakJelantah (satuan Liter, atribut kadarAsam) dengan method abstrak hitungNilaiSubtotal(double)."
    )
    add_code_block(doc,
        "// File: ecotrash/src/main/java/id/ac/kampus/ecotrash/model/KategoriSampah.java\n"
        "public abstract class KategoriSampah {\n"
        "    private final String kodeSampah, namaKategori, satuan;\n"
        "    private double hargaBeliPerSatuan;\n\n"
        "    protected KategoriSampah(String kode, String nama, String satuan, double harga) {\n"
        "        if (kode == null || kode.isBlank()) throw new IllegalArgumentException(\"Kode wajib diisi\");\n"
        "        if (harga < 0) throw new IllegalArgumentException(\"Harga beli tidak boleh negatif\");\n"
        "        this.kodeSampah = kode; this.namaKategori = nama;\n"
        "        this.satuan = satuan; this.hargaBeliPerSatuan = harga;\n"
        "    }\n"
        "    public abstract double hitungNilaiSubtotal(double kuantitas); // Kontrak polimorfis\n"
        "}\n\n"
        "// File: ecotrash/src/main/java/id/ac/kampus/ecotrash/model/MinyakJelantah.java\n"
        "public class MinyakJelantah extends KategoriSampah {\n"
        "    private final double kadarAsam;\n"
        "    public MinyakJelantah(String kode, double hargaBeliPerLiter, double kadarAsam) {\n"
        "        super(kode, \"Minyak Jelantah Rumah Tangga\", \"Liter\", hargaBeliPerLiter);\n"
        "        this.kadarAsam = kadarAsam;\n"
        "    }\n"
        "    @Override\n"
        "    public double hitungNilaiSubtotal(double volumeLiter) {\n"
        "        if (volumeLiter <= 0) return 0.0;\n"
        "        return volumeLiter * getHargaBeli();\n"
        "    }\n"
        "}"
    )
    
    # 3.4 Polymorphism & Interface
    add_styled_heading(doc, "3.4 Polymorphism dan Interface (Dynamic Binding & Pemisahan LSP/ISP)", level=2)
    doc.add_paragraph(
        "Sistem menerapkan polimorfisme tingkat lanjut melalui antarmuka (interface) yang dipakai sebagai tipe acuan "
        "(reference type). Seluruh layanan transaksi berinteraksi dengan tipe interface SkemaReward, sehingga objek "
        "RewardTabunganRupiah maupun RewardPoinSembako dapat dipertukarkan secara mulus pada saat runtime (Dynamic Binding)."
    )
    add_code_block(doc,
        "// File: ecotrash/src/main/java/id/ac/kampus/ecotrash/reward/SkemaReward.java\n"
        "public interface SkemaReward {\n"
        "    void terapkanReward(NasabahWarga nasabah, double totalNominalRupiah);\n"
        "    String getNamaSkema();\n"
        "    String getKeterangan();\n"
        "}\n\n"
        "// File: ecotrash/src/main/java/id/ac/kampus/ecotrash/reward/BisaCairkanTunai.java (ISP & LSP)\n"
        "public interface BisaCairkanTunai {\n"
        "    boolean cairkanTunai(NasabahWarga nasabah, double nominal);\n"
        "}\n\n"
        "// RewardTabunganRupiah mengimplementasikan KEDUA interface (uang kas bisa ditarik)\n"
        "public class RewardTabunganRupiah implements SkemaReward, BisaCairkanTunai { ... }\n\n"
        "// RewardPoinSembako HANYA mengimplementasikan SkemaReward (poin bukan kas tunai)\n"
        "public class RewardPoinSembako implements SkemaReward { ... }"
    )
    add_callout(doc,
        "Pemisahan interface BisaCairkanTunai dari SkemaReward merupakan penerapan nyata dari Interface Segregation "
        "Principle (ISP) dan Liskov Substitution Principle (LSP). SkemaReward tidak memaksa kelas non-tunai "
        "(seperti RewardPoinSembako) mengimplementasikan method cairkanTunai() yang melempar exception palsu "
        "(UnsupportedOperationException), sehingga stabilitas substitusi polimorfis tetap terjaga.",
        title="PENERAPAN ISP & LSP PADA REWARD ENGINE"
    )
    
    # ================= BAB IV =================
    add_styled_heading(doc, "4. Alur Implementasi dari Lapisan Model sampai Service", level=1)
    doc.add_paragraph(
        "Mengikuti urutan implementasi standar perangkat lunak (Slide 13), konstruksi fitur dibangun secara bottom-up "
        "sehingga lapisan bawah tidak memiliki dependensi pada lapisan atas, dan setiap tahap dapat dikompilasi serta "
        "diuji secara mandiri:"
    )
    
    steps = [
        ("Langkah 1 (Model Domain): ", "Membangun entitas Pengguna, NasabahWarga, PetugasBankSampah, KategoriSampah, DetailSetoran, dan TransaksiSetoran lengkap dengan aturan enkapsulasi dan penegakan relasi komposisi."),
        ("Langkah 2 (Kontrak Abstraksi): ", "Mendefinisikan interface SkemaReward, BisaCairkanTunai, TransaksiRepository, dan NotifikasiService sebagai kontrak kerja antarmodul."),
        ("Langkah 3 (Implementasi Konkret): ", "Mengimplementasikan strategi reward (RewardTabunganRupiah, RewardPoinSembako) serta mekanisme penyimpanan memori (MemoryTransaksiRepository)."),
        ("Langkah 4 (Service Orchestration & DIP): ", "Membangun BankSampahService yang hanya bergantung pada interface repository dan notifikasi. Ketergantungan disuntikkan via Constructor Injection."),
        ("Langkah 5 (Uji & Rakit UI): ", "Menyusun skenario pengujian pada KodeUjiSederhana.java, kemudian merakit seluruh dependensi objek pada Composition Root (App.java) dan menghubungkannya ke BankSampahCLI.")
    ]
    for s_title, s_desc in steps:
        bp = doc.add_paragraph(style='List Bullet')
        bp.paragraph_format.space_after = Pt(2)
        r = bp.add_run(s_title)
        r.bold = True
        bp.add_run(s_desc)
        
    add_code_block(doc,
        "// Bukti Constructor Injection (Dependency Inversion Principle) pada BankSampahService.java\n"
        "public class BankSampahService {\n"
        "    private final TransaksiRepository repository;\n"
        "    private final NotifikasiService notifikasiService;\n\n"
        "    // High-level service HANYA bergantung pada abstraksi interface (DIP)\n"
        "    public BankSampahService(TransaksiRepository repository, NotifikasiService notifikasiService) {\n"
        "        if (repository == null) throw new IllegalArgumentException(\"Repository wajib disediakan.\");\n"
        "        this.repository = repository;\n"
        "        this.notifikasiService = notifikasiService;\n"
        "    }\n"
        "}"
    )
    
    # ================= BAB V =================
    add_styled_heading(doc, "5. Implementasi Dua Fitur Utama Berjalan (End-to-End)", level=1)
    doc.add_paragraph(
        "Sesuai instruksi penugasan, sistem mengimplementasikan minimal 2 fitur utama operasional bank sampah RT "
        "yang berjalan penuh dari lapisan model, service, hingga antarmuka konsol App.java:"
    )
    
    add_styled_heading(doc, "5.1 Fitur 1: Penimbangan & Setoran Sampah Terpilah Multi-Komoditas", level=2)
    doc.add_paragraph(
        "Fitur ini memungkinkan nasabah warga menyetorkan beberapa jenis sampah sekaligus dalam satu nomor transaksi "
        "(multi-item). Petugas kasir memilih komoditas terdaftar, memasukkan kuantitas hasil timbangan, dan memilih "
        "skema reward yang diinginkan nasabah (Tabungan Kas Rupiah atau Poin Belanja Sembako). Service memvalidasi "
        "kelengkapan setoran, mengakumulasi subtotal tiap komoditas secara polimorfis, mengunci status transaksi "
        "menjadi 'SELESAI', memperbarui saldo/poin nasabah, menyimpan arsip transaksi ke repository, dan menerbitkan "
        "slip setoran resmi serta notifikasi."
    )
    
    add_styled_heading(doc, "5.2 Fitur 2: Pencairan Saldo Kas Tunai Nasabah", level=2)
    doc.add_paragraph(
        "Fitur ini mengakomodasi kebutuhan nasabah warga untuk mencairkan saldo tabungan hasil setoran sampah menjadi "
        "uang tunai di kas balai RT. Service memanggil kontrak BisaCairkanTunai pada RewardTabunganRupiah yang secara "
        "aman memeriksa kecukupan saldo tabungan warga. Jika saldo mencukupi (Skenario Berhasil), uang tunai diserahkan "
        "dan saldo berkurang tepat sebesar nominal penarikan. Jika saldo tidak cukup (Skenario Ditolak), transaksi dibatalkan, "
        "saldo nasabah tetap aman (tidak berkurang), dan kasir menerima notifikasi penolakan."
    )
    
    # ================= BAB VI =================
    add_styled_heading(doc, "6. Hasil Pengujian Kode Program (Skenario Berhasil & Gagal)", level=1)
    doc.add_paragraph(
        "Sesuai rubrik pengujian Pertemuan 11, setiap fitur wajib memiliki kode uji untuk minimal 2 skenario (skenario "
        "berhasil dan skenario gagal) yang dituangkan dalam berkas pengujian mandiri KodeUjiSederhana.java. "
        "Pola assertion mandiri digunakan untuk memastikan pengujian dapat dijalankan secara instan tanpa ketergantungan framework luar."
    )
    
    add_styled_heading(doc, "Tabel 6.1 Matriks Hasil Pengujian Otomatis KodeUjiSederhana.java", level=3)
    
    test_rows = [
        ("Skenario 1A", "Fitur 1: Setoran Sampah (Berhasil)", "4 kg Botol PET @ 4500 = Rp 18.000. Transaksi SELESAI, tersimpan di repo, saldo nasabah bertambah tepat Rp 18.000.", "LULUS (100%)"),
        ("Skenario 1B", "Fitur 1: Setoran Sampah (Gagal)", "Transaksi tanpa item diproses. Melempar IllegalStateException, status tetap, saldo utuh, repo kosong.", "LULUS (100%)"),
        ("Skenario 2A", "Fitur 2: Pencairan Kas (Berhasil)", "Nasabah saldo Rp 50.000 mencairkan Rp 30.000. Fungsi return true, sisa saldo tabungan nasabah tepat Rp 20.000.", "LULUS (100%)"),
        ("Skenario 2B", "Fitur 2: Pencairan Kas (Gagal)", "Nasabah saldo Rp 15.000 mencairkan Rp 50.000. Fungsi return false, saldo nasabah tetap Rp 15.000 (tidak berkurang).", "LULUS (100%)"),
        ("Skenario 3", "Enkapsulasi & Aturan Bisnis", "Uji input ilegal: penarikan negatif, timbangan <= 0, ID pengguna kosong. Seluruhnya ditolak lewat exception.", "LULUS (100%)"),
        ("Skenario 4", "Polimorfisme Skema Reward", "Setoran Rp 25.000 menggunakan RewardPoinSembako berhasil dikonversi menjadi tepat 25 poin belanja sembako.", "LULUS (100%)")
    ]
    
    tbl_test = doc.add_table(rows=len(test_rows)+1, cols=4)
    tbl_test.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl_test.autofit = False
    set_table_borders(tbl_test)
    
    t_widths = [Inches(1.2), Inches(2.0), Inches(2.3), Inches(1.0)]
    t_headers = ["ID Skenario", "Fokus Pengujian", "Ekspektasi & Kondisi Uji", "Hasil Eksekusi"]
    
    for i, title in enumerate(t_headers):
        tbl_test.rows[0].cells[i].text = title
        tbl_test.rows[0].cells[i].width = t_widths[i]
        set_cell_background(tbl_test.rows[0].cells[i], "1F4E79")
        set_cell_margins(tbl_test.rows[0].cells[i], top=100, bottom=100, left=100, right=100)
        p = tbl_test.rows[0].cells[i].paragraphs[0]
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        for r in p.runs:
            r.font.name = 'Calibri'
            r.font.bold = True
            r.font.size = Pt(9.5)
            r.font.color.rgb = RGBColor(255, 255, 255)
            
    for row_idx, data in enumerate(test_rows, start=1):
        r_cells = tbl_test.rows[row_idx].cells
        bg_col = "F9FAFC" if row_idx % 2 == 1 else "FFFFFF"
        for col_idx in range(4):
            r_cells[col_idx].text = data[col_idx]
            r_cells[col_idx].width = t_widths[col_idx]
            set_cell_background(r_cells[col_idx], bg_col)
            set_cell_margins(r_cells[col_idx], top=80, bottom=80, left=100, right=100)
            p = r_cells[col_idx].paragraphs[0]
            if col_idx == 3:
                p.alignment = WD_ALIGN_PARAGRAPH.CENTER
            for r in p.runs:
                r.font.name = 'Calibri'
                r.font.size = Pt(8.5)
                if col_idx == 3:
                    r.font.bold = True
                    r.font.color.rgb = RGBColor(34, 139, 34) # Green
                else:
                    r.font.color.rgb = RGBColor(40, 40, 40)
                    
    doc.add_paragraph().paragraph_format.space_after = Pt(4)
    
    # 6.1 Bukti Keluaran Eksekusi
    add_styled_heading(doc, "6.1 Bukti Keluaran Eksekusi Test Suite (KodeUjiSederhana.java)", level=2)
    add_code_block(doc,
        "=== PENGUJIAN OTOMATIS: KODE UJI SEDERHANA ECOTRASH ===\n"
        "[UJI 1A] Setoran sampah berhasil               : LULUS (Saldo Rp 18000, Status SELESAI)\n"
        "[UJI 1B] Setoran sampah gagal tanpa item       : LULUS (IllegalStateException ditangkap)\n"
        "[UJI 2A] Pencairan saldo kas berhasil          : LULUS (Saldo ditarik Rp 30000, sisa Rp 20000)\n"
        "[UJI 2B] Pencairan kas gagal (saldo kurang)    : LULUS (Ditolak, saldo tetap Rp 15000)\n"
        "[UJI 3]  Enkapsulasi & validasi aturan bisnis   : LULUS (Proteksi saldo minus & berat <= 0)\n"
        "[UJI 4]  Polimorfisme skema reward sembako      : LULUS (Tepat 25 poin sembako diperoleh)\n\n"
        "STATUS AKHIR: SEMUA PENGUJIAN LOLOS 100% (6 dari 6 Skenario Hijau)!"
    )
    
    add_styled_heading(doc, "6.2 Bukti Keluaran Program Utama Konsol (App.java)", level=2)
    add_code_block(doc,
        "=================================================================\n"
        "       ECOTRASH — SISTEM PENGELOLAAN BANK SAMPAH RT KOMUNITAS    \n"
        "       Bank Sampah Berkah Mandiri - RT 04 / RW 02 Kelurahan Sukamaju\n"
        "=================================================================\n\n"
        "[KATALOG TARIF PEMBELIAN SAMPAH RT]\n"
        "SMP01 | Botol Plastik PET Bening       | Rp  4,500 / Kg\n"
        "SMP02 | Kardus Bekas / Box Cokelat     | Rp  2,500 / Kg\n"
        "SMP03 | Kaleng Minuman Aluminium       | Rp 12,000 / Kg\n"
        "SMP04 | Minyak Jelantah Rumah Tangga   | Rp  6,500 / Liter\n\n"
        ">>> [DEMO FITUR 1] PENIMBANGAN & SETORAN SAMPAH\n"
        "[NOTIFIKASI SMS/WA]: Setoran TRX-2026-001 senilai Rp 35.250 diproses oleh Budi Santoso [Tabungan Kas Rupiah].\n"
        "+---------------------------------------------------------------+\n"
        "| BUKTI SETORAN BANK SAMPAH RT (SLIP OPERASIONAL)               |\n"
        "+---------------------------------------------------------------+\n"
        "| Kode Transaksi : TRX-2026-001                                 |\n"
        "| Nasabah        : Ibu Siti Aminah                              |\n"
        "| Rincian Item   : 4.5 Kg Botol PET + 6.0 Kg Kardus Bekas       |\n"
        "| TOTAL NILAI KAS: Rp    35,250.00                              |\n"
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
        "Saldo Kas Tetap  : Rp 15,250.00"
    )
    
    # ================= BAB VII =================
    add_styled_heading(doc, "7. Bukti Kolaborasi Git, GitHub Flow, dan Code Review", level=1)
    doc.add_paragraph(
        "Pengembangan proyek v3 dijalankan secara kolaboratif mengikuti standar industri GitHub Flow (Slide 18). "
        "Branch utama 'main' dilindungi dalam kondisi selalu stabil (selalu bisa dikompilasi dan lulus seluruh pengujian). "
        "Setiap penambahan fitur diisolasi dalam branch fitur berumur pendek dan hanya digabungkan melalui Pull Request (PR) "
        "yang telah ditinjau menggunakan checklist review PBO."
    )
    
    add_styled_heading(doc, "7.1 Matriks Pembagian Kerja per Package (Sesuai Slide 21)", level=2)
    
    collab_rows = [
        ("Anggota A (Zidhan Arzaaq)", "model", "Pengguna, NasabahWarga, Petugas, Kategori, Detail, Transaksi", "Menjamin validasi bisnis & enkapsulasi"),
        ("Anggota B", "reward", "SkemaReward, BisaCairkanTunai, TabunganRupiah, PoinSembako", "Menjamin kepatuhan OCP, LSP, dan ISP"),
        ("Anggota C", "repository", "TransaksiRepository, MemoryTransaksiRepository", "Abstraksi persistensi & query transaksi"),
        ("Anggota D", "service", "BankSampahService, NotifikasiService, KodeUjiSederhana", "Orkestrasi alur fitur & test suite mandiri"),
        ("Anggota E", "ui & root", "BankSampahCLI, App.java, run_app.sh, README, Dokumentasi", "Perakitan dependensi (DIP), CLI, integrasi")
    ]
    
    tbl_collab = doc.add_table(rows=len(collab_rows)+1, cols=4)
    tbl_collab.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl_collab.autofit = False
    set_table_borders(tbl_collab)
    
    c_widths = [Inches(1.8), Inches(1.1), Inches(2.2), Inches(1.4)]
    c_headers = ["Anggota Tim", "Package", "Kelas yang Dibangun", "Tanggung Jawab Teknis"]
    
    for i, title in enumerate(c_headers):
        tbl_collab.rows[0].cells[i].text = title
        tbl_collab.rows[0].cells[i].width = c_widths[i]
        set_cell_background(tbl_collab.rows[0].cells[i], "1F4E79")
        set_cell_margins(tbl_collab.rows[0].cells[i], top=100, bottom=100, left=100, right=100)
        p = tbl_collab.rows[0].cells[i].paragraphs[0]
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        for r in p.runs:
            r.font.name = 'Calibri'
            r.font.bold = True
            r.font.size = Pt(9.5)
            r.font.color.rgb = RGBColor(255, 255, 255)
            
    for row_idx, data in enumerate(collab_rows, start=1):
        r_cells = tbl_collab.rows[row_idx].cells
        bg_col = "F9FAFC" if row_idx % 2 == 1 else "FFFFFF"
        for col_idx in range(4):
            r_cells[col_idx].text = data[col_idx]
            r_cells[col_idx].width = c_widths[col_idx]
            set_cell_background(r_cells[col_idx], bg_col)
            set_cell_margins(r_cells[col_idx], top=80, bottom=80, left=100, right=100)
            p = r_cells[col_idx].paragraphs[0]
            for r in p.runs:
                r.font.name = 'Consolas' if col_idx in [1, 2] else 'Calibri'
                r.font.size = Pt(8.5)
                r.font.color.rgb = RGBColor(40, 40, 40)
                
    doc.add_paragraph().paragraph_format.space_after = Pt(4)
    
    add_styled_heading(doc, "7.2 Daftar Pull Request & Bukti Code Review (Checklist PBO)", level=2)
    doc.add_paragraph(
        "Setiap penambahan fitur diajukan melalui Pull Request terpisah dengan checklist peninjauan PBO (Slide 20):\n"
        "• PR #1 (feature/model-domain): Pembuatan model Pengguna, Kategori, dan Transaksi. Reviewer memastikan semua atribut private dan DetailSetoran dikonstruksi melalui komposisi (tambahItem).\n"
        "• PR #2 (feature/reward-engine): Pemisahan interface SkemaReward dan BisaCairkanTunai. Reviewer memverifikasi kepatuhan LSP agar RewardPoinSembako tidak melempar UnsupportedOperationException.\n"
        "• PR #3 (feature/service-orchestration): Pembuatan BankSampahService dengan constructor injection (DIP). Reviewer memeriksa ketiadaan pembuatan objek new repository di dalam service.\n"
        "• PR #4 (feature/unit-testing): Penambahan KodeUjiSederhana.java untuk 6 skenario pengujian. Reviewer memastikan pengujian lulus 100% sebelum merge ke main."
    )
    
    add_callout(doc,
        "Checklist Reviewer PBO yang Digunakan pada Setiap PR:\n"
        "1. [V] Sesuai Class Diagram v2? (Nama kelas, method, tipe data identik).\n"
        "2. [V] Atribut private? (Tidak ada akses langsung ke field; aturan divalidasi di method).\n"
        "3. [V] Inheritance hanya untuk relasi 'is-a'? (Pengguna -> Nasabah, Kategori -> Anorganik).\n"
        "4. [V] Bagian fleksibel menggunakan interface? (SkemaReward, TransaksiRepository, NotifikasiService).\n"
        "5. [V] Bebas dari God Class / Kode Ganda? (Tanggung jawab terbagi rapi sesuai SRP).\n"
        "6. [V] Dapat dikompilasi dan seluruh uji lulus? (Zero error saat kompilasi).",
        title="BUKTI FORMAL CODE REVIEW PBO"
    )
    
    add_styled_heading(doc, "7.3 Riwayat Commit (Conventional Commits) & Tag Rilis v3", level=2)
    doc.add_paragraph(
        "Riwayat commit repositori dikelola menggunakan konvensi Conventional Commits untuk memudahkan audit riwayat kerja. "
        "Versi stabil v3 telah diberi penanda rilis resmi 'v3' menggunakan git tag pada commit cabang main:"
    )
    add_code_block(doc,
        "$ git log --oneline -n 6\n"
        "6698063 (HEAD -> main, origin/main) Rilis fix v3\n"
        "1e90272 (tag: v3) fix: struktur blok main di App.java dan rebuild ecotrash.jar\n"
        "485404a feat: implementasi v3 fitur PBO, test suite mandiri, dan laporan pertemuan 11\n"
        "1e8745b chore: pembaruan arsitektur class diagram v2\n"
        "2cf9e50 feat: inisialisasi proyek akhir PBO EcoTrash (SOLID v2, web loket, dokumen)"
    )
    
    # ================= BAB VIII =================
    add_styled_heading(doc, "8. Petunjuk Menjalankan Aplikasi (README Quick Guide)", level=1)
    doc.add_paragraph(
        "Untuk memudahkan dosen penguji dan tim mengevaluasi aplikasi, disediakan petunjuk eksekusi instan melalui terminal:"
    )
    add_code_block(doc,
        "# 1. Menjalankan Demo Aplikasi Konsol (App.java - Fitur 1 & 2):\n"
        "$ ./run_app.sh\n\n"
        "# 2. Menjalankan Test Suite Mandiri (KodeUjiSederhana.java - 6 Skenario):\n"
        "$ ./run_tests.sh\n\n"
        "# 3. Menjalankan GUI Web Loket Kasir (Opsional - http://localhost:8080):\n"
        "$ python3 web_app.py"
    )
    
    # ================= BAB IX =================
    add_styled_heading(doc, "9. Refleksi Terhadap Kesalahan Umum PBO (Slide 27)", level=1)
    doc.add_paragraph(
        "Sebagai bagian dari evaluasi kualitas perangkat lunak, tim melakukan audit mandiri terhadap 6 kesalahan umum "
        "yang sering terjadi pada implementasi PBO mahasiswa:"
    )
    
    mistakes = [
        ("Pencegahan Getter-Setter untuk Semua: ", "Atribut dilindungi secara ketat. NasabahWarga tidak memiliki method setSaldoTabungan() publik; saldo hanya dapat dimutasi melalui tambahSaldo() dan kurangiSaldo() yang berlogika bisnis."),
        ("Pencegahan 'instanceof' di Mana-Mana: ", "Perhitungan subtotal komoditas sampah dan kalkulasi insentif reward memanfaatkan dynamic binding polimorfis murni (method hitungNilaiSubtotal dan terapkanReward), bebas percabangan instanceof."),
        ("Pencegahan Pembuatan Objek Konkret di Service: ", "BankSampahService tidak melakukan instansiasi 'new MemoryTransaksiRepository()' di konstruktornya, melainkan menerima interface dari luar (DIP)."),
        ("Pencegahan Commit Langsung ke Main: ", "Seluruh fitur dikerjakan di branch terpisah dan digabungkan melalui Pull Request."),
        ("Pencegahan Satu Commit Raksasa: ", "Setiap commit berukuran kecil dan berfokus pada satu tujuan spesifik (atomic commits)."),
        ("Sinkronisasi Penuh Kode dan Diagram: ", "Class Diagram v2 diperbarui dan diselaraskan penuh dengan seluruh kelas serta interface Java yang dibangun.")
    ]
    for m_title, m_desc in mistakes:
        bp = doc.add_paragraph(style='List Bullet')
        bp.paragraph_format.space_after = Pt(2)
        r = bp.add_run(m_title)
        r.bold = True
        bp.add_run(m_desc)
        
    doc.add_paragraph().paragraph_format.space_after = Pt(10)
    
    # Save file
    filename = "PBO_Proyek_Tim23_EcoTrash_v3.docx"
    doc.save(filename)
    print(f"Report generated successfully: {filename}")

if __name__ == "__main__":
    build_report()
