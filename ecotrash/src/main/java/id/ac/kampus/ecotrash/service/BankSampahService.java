package id.ac.kampus.ecotrash.service;

import id.ac.kampus.ecotrash.model.NasabahWarga;
import id.ac.kampus.ecotrash.model.PetugasBankSampah;
import id.ac.kampus.ecotrash.model.TransaksiSetoran;
import id.ac.kampus.ecotrash.repository.TransaksiRepository;
import id.ac.kampus.ecotrash.reward.RewardTabunganRupiah;
import id.ac.kampus.ecotrash.reward.SkemaReward;

import java.util.List;

/**
 * Orkestrasi proses bisnis bank sampah RT.
 * Menerima dependensi melalui constructor injection (DIP) dan mendelegasikan
 * persistensi serta notifikasi ke kolaborator masing-masing (SRP).
 */
public class BankSampahService {

    private final TransaksiRepository repository;
    private final NotifikasiService notifikasiService;

    public BankSampahService(TransaksiRepository repository, NotifikasiService notifikasiService) {
        if (repository == null) {
            throw new IllegalArgumentException("TransaksiRepository wajib disediakan.");
        }
        this.repository = repository;
        this.notifikasiService = notifikasiService;
    }

    /**
     * Memproses setoran sampah: validasi, selesaikan transaksi dengan skema reward,
     * simpan ke repository, dan kirim notifikasi jika tersedia.
     */
    public void prosesSetoran(TransaksiSetoran tx, SkemaReward skema) {
        if (tx == null) {
            throw new IllegalArgumentException("Transaksi tidak boleh null.");
        }
        if (skema == null) {
            throw new IllegalArgumentException("Skema reward harus dipilih.");
        }

        tx.selesaikanTransaksi(skema);
        repository.simpan(tx);

        if (notifikasiService != null) {
            String pesan = String.format(
                    "Setoran %s senilai Rp %.0f berhasil diproses dengan skema [%s].",
                    tx.getKodeTransaksi(), tx.hitungTotalNilai(), skema.getNamaSkema());
            notifikasiService.kirimNotifikasi(tx.getNasabah().getNoTelepon(), pesan);
        }
    }

    /**
     * Versi overloaded yang juga mencatat nama petugas penimbang.
     */
    public void prosesSetoran(TransaksiSetoran tx, SkemaReward skema, PetugasBankSampah petugas) {
        if (tx == null) {
            throw new IllegalArgumentException("Transaksi tidak boleh null.");
        }
        if (skema == null) {
            throw new IllegalArgumentException("Skema reward harus dipilih.");
        }

        tx.selesaikanTransaksi(skema);
        repository.simpan(tx);

        if (notifikasiService != null) {
            String namaPetugas = (petugas != null) ? petugas.getNamaLengkap() : "Tidak diketahui";
            String pesan = String.format(
                    "Setoran %s senilai Rp %.0f diproses oleh %s dengan skema [%s].",
                    tx.getKodeTransaksi(), tx.hitungTotalNilai(), namaPetugas, skema.getNamaSkema());
            notifikasiService.kirimNotifikasi(tx.getNasabah().getNoTelepon(), pesan);
        }
    }

    /**
     * Mencairkan saldo tunai nasabah menggunakan skema tabungan rupiah.
     * Mengembalikan true jika pencairan berhasil (saldo mencukupi).
     */
    public boolean cairkanSaldoNasabah(NasabahWarga nasabah, double nominal) {
        if (nasabah == null) {
            throw new IllegalArgumentException("Nasabah tidak boleh null.");
        }

        RewardTabunganRupiah tabungan = new RewardTabunganRupiah();
        boolean berhasil = tabungan.cairkanTunai(nasabah, nominal);

        if (berhasil && notifikasiService != null) {
            String pesan = String.format(
                    "Pencairan tunai Rp %.0f berhasil. Sisa saldo: Rp %.0f.",
                    nominal, nasabah.getSaldoTabungan());
            notifikasiService.kirimNotifikasi(nasabah.getNoTelepon(), pesan);
        }

        return berhasil;
    }

    /**
     * Total berat sampah (kg) dari seluruh transaksi berstatus SELESAI.
     */
    public double hitungTotalTonaseKg() {
        double total = 0.0;
        for (TransaksiSetoran tx : repository.cariSemua()) {
            if ("SELESAI".equals(tx.getStatus())) {
                total += tx.hitungTotalBerat();
            }
        }
        return total;
    }

    /**
     * Total perputaran kas (rupiah) dari seluruh transaksi berstatus SELESAI.
     */
    public double hitungTotalPerputaranKas() {
        double total = 0.0;
        for (TransaksiSetoran tx : repository.cariSemua()) {
            if ("SELESAI".equals(tx.getStatus())) {
                total += tx.hitungTotalNilai();
            }
        }
        return total;
    }

    public List<TransaksiSetoran> getSemuaTransaksi() {
        return repository.cariSemua();
    }

    public List<TransaksiSetoran> cariTransaksiNasabah(String nomorRekening) {
        return repository.cariBerdasarkanRekeningNasabah(nomorRekening);
    }

    public TransaksiRepository getRepository() {
        return repository;
    }
}
