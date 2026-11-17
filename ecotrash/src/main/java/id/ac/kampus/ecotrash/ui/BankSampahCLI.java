package id.ac.kampus.ecotrash.ui;

import id.ac.kampus.ecotrash.model.*;
import id.ac.kampus.ecotrash.reward.RewardPoinSembako;
import id.ac.kampus.ecotrash.reward.RewardTabunganRupiah;
import id.ac.kampus.ecotrash.reward.SkemaReward;
import id.ac.kampus.ecotrash.service.BankSampahService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Antarmuka Command Line Interface (CLI) untuk interaksi konsol loket bank sampah.
 * Memisahkan presentation layer dari domain dan business service (Layered Architecture).
 */
public class BankSampahCLI {
    private final BankSampahService service;
    private final BankSampahUnit unit;

    public BankSampahCLI(BankSampahService service, BankSampahUnit unit) {
        this.service = service;
        this.unit = unit;
    }

    public void cetakBanner() {
        System.out.println("=================================================================");
        System.out.println("       ECOTRASH — SISTEM PENGELOLAAN BANK SAMPAH RT KOMUNITAS    ");
        System.out.println("                " + unit.getNamaUnit() + " - " + unit.getAlamatRT());
        System.out.println("=================================================================");
    }

    public void tampilkanKatalog() {
        System.out.println("\n[KATALOG TARIF PEMBELIAN SAMPAH RT]");
        System.out.println("-----------------------------------------------------------------");
        System.out.printf("%-8s | %-30s | %-12s | %-6s\n", "Kode", "Nama Kategori Sampah", "Harga Beli", "Satuan");
        System.out.println("-----------------------------------------------------------------");
        for (KategoriSampah k : unit.getDaftarKategori()) {
            System.out.printf("%-8s | %-30s | Rp %,10.0f | %-6s\n",
                    k.getKodeSampah(), k.getNamaKategori(), k.getHargaBeli(), k.getSatuan());
        }
        System.out.println("-----------------------------------------------------------------");
    }

    public void cetakSlipSetoran(TransaksiSetoran tx) {
        System.out.println("\n+---------------------------------------------------------------+");
        System.out.println("| BUKTI SETORAN BANK SAMPAH RT (SLIP OPERASIONAL)               |");
        System.out.println("+---------------------------------------------------------------+");
        System.out.printf("| Kode Transaksi : %-44s |\n", tx.getKodeTransaksi());
        System.out.printf("| Tanggal        : %-44s |\n", tx.getTanggalSetor());
        System.out.printf("| Nasabah        : %-44s |\n", tx.getNasabah().getNamaLengkap());
        System.out.printf("| No. Rekening   : %-44s |\n", tx.getNasabah().getNomorRekening());
        System.out.printf("| Status         : %-44s |\n", tx.getStatus());
        System.out.println("+---------------------------------------------------------------+");
        System.out.printf("| %-22s | %-8s | %-12s | %-12s |\n", "Komoditas", "Jumlah", "Tarif Satuan", "Subtotal");
        System.out.println("|------------------------+----------+--------------+--------------|");
        for (DetailSetoran d : tx.getDaftarItem()) {
            System.out.printf("| %-22s | %6.2f %-1s | Rp %,10.0f | Rp %,10.0f |\n",
                    d.getKategori().getNamaKategori(),
                    d.getKuantitasTimbangan(),
                    d.getKategori().getSatuan(),
                    d.getKategori().getHargaBeli(),
                    d.getSubtotal());
        }
        System.out.println("+---------------------------------------------------------------+");
        System.out.printf("| TOTAL TIMBANGAN : %6.2f Kg/Liter                               |\n", tx.hitungTotalBerat());
        System.out.printf("| TOTAL NILAI KAS : Rp %,12.2f                                 |\n", tx.hitungTotalNilai());
        System.out.println("+---------------------------------------------------------------+");
    }

    public void tampilkanRekapitulasi() {
        System.out.println("\n[REKAPITULASI KINERJA BANK SAMPAH]");
        System.out.println("-----------------------------------------------------------------");
        System.out.printf("Total Timbangan Sampah Terkelola : %.2f Kg/Liter\n", service.hitungTotalTonaseKg());
        System.out.printf("Total Perputaran Nilai Kas Warga : Rp %,.2f\n", service.hitungTotalPerputaranKas());
        System.out.printf("Jumlah Transaksi Tercatat        : %d Transaksi\n", service.getSemuaTransaksi().size());
        System.out.println("-----------------------------------------------------------------");
    }

    public void jalankanMenuInteraktif(Scanner scanner, List<NasabahWarga> daftarNasabah) {
        boolean berjalan = true;
        while (berjalan) {
            System.out.println("\n>>> MENU OPERASIONAL KASIR BANK SAMPAH");
            System.out.println("1. Lihat Katalog Komoditas & Tarif");
            System.out.println("2. Input Penimbangan & Setoran Sampah (Fitur 1)");
            System.out.println("3. Pencairan Saldo Kas Tunai Nasabah (Fitur 2)");
            System.out.println("4. Lihat Saldo & Rekening Nasabah");
            System.out.println("5. Lihat Rekapitulasi Pembukuan RT");
            System.out.println("0. Selesai / Keluar");
            System.out.print("Pilih opsi [0-5]: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    tampilkanKatalog();
                    break;
                case "2":
                    prosesInputSetoranInteraktif(scanner, daftarNasabah);
                    break;
                case "3":
                    prosesPencairanInteraktif(scanner, daftarNasabah);
                    break;
                case "4":
                    tampilkanDaftarNasabah(daftarNasabah);
                    break;
                case "5":
                    tampilkanRekapitulasi();
                    break;
                case "0":
                    berjalan = false;
                    System.out.println("Sesi kasir selesai. Terima kasih.");
                    break;
                default:
                    System.out.println("Pilihan tidak valid, silakan ulangi.");
            }
        }
    }

    private void tampilkanDaftarNasabah(List<NasabahWarga> daftarNasabah) {
        System.out.println("\n[BUKU TABUNGAN NASABAH WARGA]");
        System.out.println("-----------------------------------------------------------------");
        System.out.printf("%-10s | %-20s | %-15s | %-10s\n", "Rekening", "Nama Warga", "Saldo Kas (Rp)", "Poin Sembako");
        System.out.println("-----------------------------------------------------------------");
        for (NasabahWarga n : daftarNasabah) {
            System.out.printf("%-10s | %-20s | Rp %,13.2f | %4d Poin\n",
                    n.getNomorRekening(), n.getNamaLengkap(), n.getSaldoTabungan(), n.getPoinReward());
        }
        System.out.println("-----------------------------------------------------------------");
    }

    private void prosesInputSetoranInteraktif(Scanner scanner, List<NasabahWarga> daftarNasabah) {
        tampilkanDaftarNasabah(daftarNasabah);
        System.out.print("Masukkan nomor rekening nasabah: ");
        String rek = scanner.nextLine().trim();

        NasabahWarga nasabah = null;
        for (NasabahWarga n : daftarNasabah) {
            if (n.getNomorRekening().equalsIgnoreCase(rek)) {
                nasabah = n;
                break;
            }
        }
        if (nasabah == null) {
            System.out.println("[GAGAL] Nasabah dengan nomor rekening tersebut tidak ditemukan.");
            return;
        }

        String kodeTx = "TRX-CLI-" + System.currentTimeMillis() % 10000;
        TransaksiSetoran tx = new TransaksiSetoran(kodeTx, nasabah, LocalDate.now());

        tampilkanKatalog();
        boolean tambahLagi = true;
        while (tambahLagi) {
            System.out.print("Masukkan kode komoditas (misal SMP01): ");
            String kodeKategori = scanner.nextLine().trim();
            KategoriSampah kategori = unit.cariKategori(kodeKategori).orElse(null);
            if (kategori == null) {
                System.out.println("[PERINGATAN] Kode komoditas tidak valid.");
            } else {
                System.out.print("Masukkan kuantitas timbangan (" + kategori.getSatuan() + "): ");
                try {
                    double berat = Double.parseDouble(scanner.nextLine().trim());
                    tx.tambahItem(kategori, berat, "Penimbangan Loket");
                    System.out.printf("Item %s seberat %.2f %s berhasil ditambahkan.\n",
                            kategori.getNamaKategori(), berat, kategori.getSatuan());
                } catch (Exception e) {
                    System.out.println("[PERINGATAN] Format angka tidak valid.");
                }
            }
            System.out.print("Tambah komoditas lain? (y/n): ");
            tambahLagi = scanner.nextLine().trim().equalsIgnoreCase("y");
        }

        if (tx.getDaftarItem().isEmpty()) {
            System.out.println("[GAGAL] Tidak ada item penimbangan. Transaksi dibatalkan.");
            return;
        }

        System.out.println("Pilih skema reward:");
        System.out.println("1. Tabungan Kas Uang Tunai");
        System.out.println("2. Poin Belanja Sembako RT");
        System.out.print("Pilihan skema [1/2]: ");
        String skemaOpt = scanner.nextLine().trim();
        SkemaReward skema = skemaOpt.equals("2") ? new RewardPoinSembako() : new RewardTabunganRupiah();

        service.prosesSetoran(tx, skema);
        cetakSlipSetoran(tx);
        System.out.println("[SUKSES] Transaksi setoran berhasil disahkan dan saldo diperbarui.");
    }

    private void prosesPencairanInteraktif(Scanner scanner, List<NasabahWarga> daftarNasabah) {
        tampilkanDaftarNasabah(daftarNasabah);
        System.out.print("Masukkan nomor rekening nasabah: ");
        String rek = scanner.nextLine().trim();

        NasabahWarga nasabah = null;
        for (NasabahWarga n : daftarNasabah) {
            if (n.getNomorRekening().equalsIgnoreCase(rek)) {
                nasabah = n;
                break;
            }
        }
        if (nasabah == null) {
            System.out.println("[GAGAL] Nasabah tidak ditemukan.");
            return;
        }

        System.out.printf("Saldo kas tabungan saat ini: Rp %,.2f\n", nasabah.getSaldoTabungan());
        System.out.print("Masukkan nominal pencairan (Rp): ");
        try {
            double nominal = Double.parseDouble(scanner.nextLine().trim());
            boolean berhasil = service.cairkanSaldoNasabah(nasabah, nominal);
            if (berhasil) {
                System.out.printf("[SUKSES] Uang tunai sebesar Rp %,.2f berhasil diserahkan ke nasabah.\n", nominal);
                System.out.printf("Sisa saldo tabungan: Rp %,.2f\n", nasabah.getSaldoTabungan());
            } else {
                System.out.println("[GAGAL] Pencairan ditolak: Saldo tabungan nasabah tidak mencukupi.");
            }
        } catch (Exception e) {
            System.out.println("[GAGAL] Terjadi kesalahan: " + e.getMessage());
        }
    }
}
