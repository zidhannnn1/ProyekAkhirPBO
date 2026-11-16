package id.ac.kampus.ecotrash.service;

import id.ac.kampus.ecotrash.model.DetailSetoran;
import id.ac.kampus.ecotrash.model.NasabahWarga;
import id.ac.kampus.ecotrash.model.TransaksiSetoran;
import id.ac.kampus.ecotrash.repository.TransaksiRepository;
import id.ac.kampus.ecotrash.reward.SkemaReward;
import java.util.List;

/**
 * Service pengorkestrasi alur logika proses bisnis penimbangan sampah dan pembukuan.
 * Menerapkan prinsip Single Responsibility Principle (SRP) dan Dependency Inversion Principle (DIP).
 */
public class BankSampahService {
    private final TransaksiRepository repo;
    private final NotifikasiService notifikasi;

    /**
     * Constructor Dependency Injection: bergantung pada abstraksi, bukan kelas konkret.
     */
    public BankSampahService(TransaksiRepository repo, NotifikasiService notifikasi) {
        if (repo == null) {
            throw new IllegalArgumentException("Repository tidak boleh null.");
        }
        this.repo = repo;
        this.notifikasi = notifikasi;
    }

    public void prosesSetoran(TransaksiSetoran transaksi, SkemaReward skema) {
        if (transaksi == null || skema == null) {
            throw new IllegalArgumentException("Transaksi dan Skema Reward harus terdefinisi.");
        }

        transaksi.selesaikanTransaksi(skema);
        repo.simpan(transaksi);

        if (notifikasi != null) {
            String pesan = String.format(
                "Halo %s, setoran Anda dengan kode %s berhasil diproses. Total nilai: Rp %,.0f melalui skema %s.",
                transaksi.getNasabah().getNamaLengkap(),
                transaksi.getKodeTransaksi(),
                transaksi.hitungTotalNilai(),
                skema.getNamaSkema()
            );
            notifikasi.kirimNotifikasi(transaksi.getNasabah().getNoTelepon(), pesan);
        }
    }

    public double hitungTotalTonaseKg() {
        double totalKg = 0.0;
        List<TransaksiSetoran> list = repo.cariSemua();
        for (TransaksiSetoran t : list) {
            if ("SELESAI".equalsIgnoreCase(t.getStatus())) {
                for (DetailSetoran d : t.getDaftarItem()) {
                    totalKg += d.getKuantitasTimbangan();
                }
            }
        }
        return totalKg;
    }

    public double hitungTotalPerputaranKas() {
        double total = 0.0;
        List<TransaksiSetoran> list = repo.cariSemua();
        for (TransaksiSetoran t : list) {
            if ("SELESAI".equalsIgnoreCase(t.getStatus())) {
                total += t.hitungTotalNilai();
            }
        }
        return total;
    }

    public List<TransaksiSetoran> getSemuaTransaksi() {
        return repo.cariSemua();
    }

    public TransaksiRepository getRepository() {
        return repo;
    }
}

