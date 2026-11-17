package id.ac.kampus.ecotrash;

import id.ac.kampus.ecotrash.model.*;
import id.ac.kampus.ecotrash.repository.MemoryTransaksiRepository;
import id.ac.kampus.ecotrash.reward.RewardPoinSembako;
import id.ac.kampus.ecotrash.reward.RewardTabunganRupiah;
import id.ac.kampus.ecotrash.reward.SkemaReward;
import id.ac.kampus.ecotrash.service.BankSampahService;

import java.time.LocalDate;

/**
 * Pengujian mandiri untuk memverifikasi fungsionalitas fitur utama
 * dan integritas aturan bisnis tanpa ketergantungan library eksternal.
 * Mengikuti pola pengujian sederhana sesuai materi Pertemuan 11.
 */
public class KodeUjiSederhana {

    public static void main(String[] args) {
        System.out.println("Memulai eksekusi kode uji mandiri EcoTrash v3...\n");

        ujiFitur1SetoranBerhasil();
        ujiFitur1SetoranGagalTanpaItem();
        ujiFitur2PencairanTunaiBerhasil();
        ujiFitur2PencairanTunaiGagalSaldoKurang();
        ujiEncapsulationValidasiAturanBisnis();
        ujiPolymorphismRewardSembako();

        System.out.println("\nSeluruh skenario pengujian berhasil dilalui dengan valid (100% lulus).");
    }

    /**
     * Skenario 1A: Setoran sampah valid dengan skema tabungan kas rupiah.
     * Memverifikasi transaksi berpindah status ke SELESAI, tersimpan di repository,
     * dan saldo nasabah bertambah tepat sesuai kalkulasi harga.
     */
    static void ujiFitur1SetoranBerhasil() {
        var repo = new MemoryTransaksiRepository();
        var service = new BankSampahService(repo, (telp, msg) -> {});
        var nasabah = new NasabahWarga("NSB01", "Ibu Siti", "081298765432", "04", "REK-04-001");
        var botol = new SampahAnorganik("SMP01", "Botol Plastik PET", 4500, "Grade A");

        var tx = new TransaksiSetoran("TRX-TEST-001", nasabah, LocalDate.now());
        tx.tambahItem(botol, 4.0, "Kering dan bersih"); // 4 kg * 4500 = 18.000

        cek(tx.hitungTotalNilai() == 18000.0, "Total nilai harus Rp 18.000");
        cek(tx.hitungTotalBerat() == 4.0, "Total berat harus 4.0 kg");
        cek("DIPROSES".equals(tx.getStatus()), "Status awal harus DIPROSES");

        service.prosesSetoran(tx, new RewardTabunganRupiah());

        cek("SELESAI".equals(tx.getStatus()), "Status transaksi setelah diproses harus SELESAI");
        cek(repo.cariSemua().size() == 1, "Transaksi harus tersimpan di repository");
        cek(repo.cariBerdasarkanKode("TRX-TEST-001") != null, "Transaksi harus dapat ditemukan berdasarkan kode");
        cek(nasabah.getSaldoTabungan() == 18000.0, "Saldo nasabah harus bertambah menjadi Rp 18.000");

        System.out.println("[LULUS] Skenario 1A: Fitur setoran sampah berhasil & terverifikasi");
    }

    /**
     * Skenario 1B: Setoran sampah gagal karena transaksi tidak memiliki item timbangan.
     * Aturan bisnis mewajibkan minimal 1 item sampah sebelum transaksi diselesaikan.
     */
    static void ujiFitur1SetoranGagalTanpaItem() {
        var repo = new MemoryTransaksiRepository();
        var service = new BankSampahService(repo, (telp, msg) -> {});
        var nasabah = new NasabahWarga("NSB02", "Pak Joko", "081311223344", "04", "REK-04-002");

        var tx = new TransaksiSetoran("TRX-TEST-002", nasabah, LocalDate.now());
        boolean exceptionTertangkap = false;

        try {
            service.prosesSetoran(tx, new RewardTabunganRupiah());
        } catch (IllegalStateException e) {
            exceptionTertangkap = true;
        }

        cek(exceptionTertangkap, "Transaksi tanpa item harus melempar IllegalStateException");
        cek(repo.cariSemua().isEmpty(), "Transaksi gagal tidak boleh tersimpan di repository");
        cek(nasabah.getSaldoTabungan() == 0.0, "Saldo nasabah tidak boleh berubah pada transaksi gagal");

        System.out.println("[LULUS] Skenario 1B: Fitur setoran gagal (transaksi kosong) ditolak dengan benar");
    }

    /**
     * Skenario 2A: Pencairan saldo kas tunai berhasil saat saldo mencukupi.
     */
    static void ujiFitur2PencairanTunaiBerhasil() {
        var repo = new MemoryTransaksiRepository();
        var service = new BankSampahService(repo, (telp, msg) -> {});
        var nasabah = new NasabahWarga("NSB03", "Ibu Dewi", "085677889900", "04", "REK-04-003");
        nasabah.tambahSaldo(50000.0);

        boolean hasil = service.cairkanSaldoNasabah(nasabah, 30000.0);

        cek(hasil, "Pencairan harus bernilai true saat saldo mencukupi");
        cek(nasabah.getSaldoTabungan() == 20000.0, "Sisa saldo nasabah harus Rp 20.000");

        System.out.println("[LULUS] Skenario 2A: Fitur pencairan saldo kas berhasil saat saldo mencukupi");
    }

    /**
     * Skenario 2B: Pencairan saldo kas tunai gagal saat saldo tidak mencukupi.
     */
    static void ujiFitur2PencairanTunaiGagalSaldoKurang() {
        var repo = new MemoryTransaksiRepository();
        var service = new BankSampahService(repo, (telp, msg) -> {});
        var nasabah = new NasabahWarga("NSB04", "Pak Bambang", "081822334455", "04", "REK-04-004");
        nasabah.tambahSaldo(15000.0);

        boolean hasil = service.cairkanSaldoNasabah(nasabah, 50000.0);

        cek(!hasil, "Pencairan harus bernilai false saat saldo tidak cukup");
        cek(nasabah.getSaldoTabungan() == 15000.0, "Saldo nasabah tidak boleh berubah saat penarikan ditolak");

        System.out.println("[LULUS] Skenario 2B: Fitur pencairan saldo kas gagal ditolak dengan aman");
    }

    /**
     * Skenario 3: Enkapsulasi dan validasi aturan bisnis pada konstruktor dan mutator.
     */
    static void ujiEncapsulationValidasiAturanBisnis() {
        var nasabah = new NasabahWarga("NSB05", "Budi", "0811111111", "04", "REK-04-005");

        boolean errNominalNegatif = false;
        try {
            nasabah.tambahSaldo(-5000.0);
        } catch (IllegalArgumentException e) {
            errNominalNegatif = true;
        }
        cek(errNominalNegatif, "tambahSaldo dengan nilai negatif harus ditolak");

        boolean errTimbanganNol = false;
        try {
            var kardus = new SampahAnorganik("SMP02", "Kardus", 2500, "Grade A");
            var tx = new TransaksiSetoran("TRX-VAL-01", nasabah, LocalDate.now());
            tx.tambahItem(kardus, 0.0, "Salah timbang");
        } catch (IllegalArgumentException e) {
            errTimbanganNol = true;
        }
        cek(errTimbanganNol, "tambahItem dengan kuantitas <= 0 harus ditolak");

        boolean errIdKosong = false;
        try {
            new NasabahWarga("", "Anonim", "0811", "04", "REK-000");
        } catch (IllegalArgumentException e) {
            errIdKosong = true;
        }
        cek(errIdKosong, "Pengguna dengan ID kosong harus ditolak");

        System.out.println("[LULUS] Skenario 3: Enkapsulasi & 3 validasi aturan bisnis terverifikasi");
    }

    /**
     * Skenario 4: Polimorfisme skema reward (Poin Sembako vs Tabungan Rupiah).
     */
    static void ujiPolymorphismRewardSembako() {
        var nasabah = new NasabahWarga("NSB06", "Pak RT", "0899999999", "04", "REK-04-006");
        SkemaReward sembako = new RewardPoinSembako();

        sembako.terapkanReward(nasabah, 25000.0); // 25.000 / 1.000 = 25 Poin
        cek(nasabah.getPoinReward() == 25, "Nasabah harus memperoleh 25 poin belanja");
        cek(nasabah.getSaldoTabungan() == 0.0, "Saldo kas tidak boleh bertambah saat memilih skema poin");

        System.out.println("[LULUS] Skenario 4: Polimorfisme skema reward sembako terverifikasi");
    }

    static void cek(boolean kondisi, String pesanKegagalan) {
        if (!kondisi) {
            throw new AssertionError("Uji Gagal: " + pesanKegagalan);
        }
    }
}
