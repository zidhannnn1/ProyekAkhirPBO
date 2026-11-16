package id.ac.kampus.ecotrash.ui;

import id.ac.kampus.ecotrash.model.*;
import id.ac.kampus.ecotrash.service.BankSampahService;
import id.ac.kampus.ecotrash.reward.SkemaReward;
import java.util.List;

/**
 * Antarmuka Command Line Interface (CLI) untuk interaksi konsol.
 * Berada di Presentation Layer.
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

    public void tampilkanRekapitulasi() {
        System.out.println("\n[REKAPITULASI KINERJA BANK SAMPAH]");
        System.out.println("-----------------------------------------------------------------");
        System.out.printf("Total Timbangan Sampah Terkelola : %.2f Kg/Liter\n", service.hitungTotalTonaseKg());
        System.out.printf("Total Perputaran Nilai Kas Warga : Rp %,.2f\n", service.hitungTotalPerputaranKas());
        System.out.printf("Jumlah Transaksi Tercatat        : %d Transaksi\n", service.getSemuaTransaksi().size());
        System.out.println("-----------------------------------------------------------------");
    }
}

