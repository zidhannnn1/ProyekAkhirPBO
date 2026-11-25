package id.ac.kampus.ecotrash.service;

import id.ac.kampus.ecotrash.exception.DataTidakDitemukanException;
import id.ac.kampus.ecotrash.exception.SaldoTidakCukupException;
import id.ac.kampus.ecotrash.exception.TransaksiTidakValidException;
import id.ac.kampus.ecotrash.model.DetailSetoran;
import id.ac.kampus.ecotrash.model.NasabahWarga;
import id.ac.kampus.ecotrash.model.PetugasBankSampah;
import id.ac.kampus.ecotrash.model.StatusTransaksi;
import id.ac.kampus.ecotrash.model.TransaksiSetoran;
import id.ac.kampus.ecotrash.repository.Repository;
import id.ac.kampus.ecotrash.reward.BisaCairkanTunai;
import id.ac.kampus.ecotrash.reward.RewardTabunganRupiah;
import id.ac.kampus.ecotrash.reward.SkemaReward;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Orkestrasi proses bisnis bank sampah RT.
 * Menerima dependensi generik dan abstraksi melalui constructor injection (DIP).
 */
public class BankSampahService {

    private final Repository<TransaksiSetoran, String> repository;
    private final NotifikasiService notifikasiService;
    private final BisaCairkanTunai skemaPencairan;

    public BankSampahService(Repository<TransaksiSetoran, String> repository,
                             NotifikasiService notifikasiService) {
        this(repository, notifikasiService, new RewardTabunganRupiah());
    }

    public BankSampahService(Repository<TransaksiSetoran, String> repository,
                             NotifikasiService notifikasiService,
                             BisaCairkanTunai skemaPencairan) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository transaksi wajib disediakan.");
        }
        if (skemaPencairan == null) {
            throw new IllegalArgumentException("Skema pencairan tunai wajib disediakan.");
        }
        this.repository = repository;
        this.notifikasiService = notifikasiService;
        this.skemaPencairan = skemaPencairan;
    }

    public void prosesSetoran(TransaksiSetoran tx, SkemaReward skema)
            throws TransaksiTidakValidException {
        prosesSetoran(tx, skema, null);
    }

    /**
     * Memproses setoran sampah: validasi, terapkan skema reward, simpan ke repository,
     * dan kirim notifikasi. Jika gagal karena aturan transaksi, kirim notifikasi galat
     * dan lempar ulang exception ke pemanggil (tangkap, bersihkan, lempar ulang).
     */
    public void prosesSetoran(TransaksiSetoran tx, SkemaReward skema, PetugasBankSampah petugas)
            throws TransaksiTidakValidException {
        if (tx == null) {
            throw new IllegalArgumentException("Transaksi tidak boleh null.");
        }
        if (skema == null) {
            throw new IllegalArgumentException("Skema reward harus dipilih.");
        }

        tx.selesaikanTransaksi(skema);
        repository.simpan(tx);

        String infoPetugas = (petugas != null) ? " oleh " + petugas.getNamaLengkap() : "";
        String pesan = String.format(
                "Setoran %s senilai Rp %.0f berhasil diproses%s dengan skema [%s].",
                tx.getKodeTransaksi(), tx.hitungTotalNilai(), infoPetugas, skema.getNamaSkema());
        kirimNotif(tx.getNasabah().getNoTelepon(), pesan);
    }

    /**
     * Mencairkan saldo tunai nasabah. Melempar SaldoTidakCukupException jika saldo kurang,
     * setelah mencatat pesan penolakan ke layanan notifikasi.
     */
    public void cairkanSaldoNasabah(NasabahWarga nasabah, double nominal)
            throws SaldoTidakCukupException {
        if (nasabah == null) {
            throw new IllegalArgumentException("Nasabah tidak boleh null.");
        }

        try {
            skemaPencairan.cairkanTunai(nasabah, nominal);
        } catch (SaldoTidakCukupException e) {
            kirimNotif(nasabah.getNoTelepon(), "Pencairan ditolak: " + e.getMessage());
            throw e;
        }

        String pesan = String.format(
                "Pencairan tunai Rp %.0f berhasil. Sisa saldo: Rp %.0f.",
                nominal, nasabah.getSaldoTabungan());
        kirimNotif(nasabah.getNoTelepon(), pesan);
    }

    /**
     * Membatalkan transaksi yang sudah selesai (misal salah input kasir) dan
     * mengembalikan saldo tabungan kas nasabah apabila menggunakan tabungan rupiah.
     */
    public void batalkanSetoran(String kodeTransaksi)
            throws DataTidakDitemukanException, TransaksiTidakValidException, SaldoTidakCukupException {
        TransaksiSetoran tx = repository.ambil(kodeTransaksi);
        if (tx.getStatus() == StatusTransaksi.DIBATALKAN) {
            throw new TransaksiTidakValidException(kodeTransaksi, "Transaksi sudah dibatalkan sebelumnya.");
        }
        if (tx.getStatus() == StatusTransaksi.SELESAI
                && tx.getSkemaDiterapkan() instanceof BisaCairkanTunai pencairan) {
            pencairan.cairkanTunai(tx.getNasabah(), tx.hitungTotalNilai());
        }
        tx.batalkan();
        kirimNotif(tx.getNasabah().getNoTelepon(),
                "Setoran " + kodeTransaksi + " dibatalkan dan saldo disesuaikan.");
    }

    public TransaksiSetoran ambilTransaksi(String kodeTransaksi) throws DataTidakDitemukanException {
        return repository.ambil(kodeTransaksi);
    }

    public Map<String, Double> beratPerKategori() {
        return repository.semua().stream()
                .filter(tx -> tx.getStatus() == StatusTransaksi.SELESAI)
                .flatMap(tx -> tx.getDaftarItem().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getKategori().getNamaKategori(),
                        TreeMap::new,
                        Collectors.summingDouble(DetailSetoran::getKuantitasTimbangan)));
    }

    public double hitungTotalTonaseKg() {
        return repository.semua().stream()
                .filter(tx -> tx.getStatus() == StatusTransaksi.SELESAI)
                .mapToDouble(TransaksiSetoran::hitungTotalBerat)
                .sum();
    }

    public double hitungTotalPerputaranKas() {
        return repository.semua().stream()
                .filter(tx -> tx.getStatus() == StatusTransaksi.SELESAI)
                .mapToDouble(TransaksiSetoran::hitungTotalNilai)
                .sum();
    }

    public List<TransaksiSetoran> getSemuaTransaksi() {
        return repository.semua();
    }

    public List<TransaksiSetoran> cariTransaksiNasabah(String nomorRekening) {
        if (nomorRekening == null) {
            return List.of();
        }
        return repository.semua().stream()
                .filter(tx -> nomorRekening.equals(tx.getNasabah().getNomorRekening()))
                .collect(Collectors.toList());
    }

    public Repository<TransaksiSetoran, String> getRepository() {
        return repository;
    }

    private void kirimNotif(String noTelepon, String pesan) {
        if (notifikasiService != null) {
            notifikasiService.kirimNotifikasi(noTelepon, pesan);
        }
    }
}
