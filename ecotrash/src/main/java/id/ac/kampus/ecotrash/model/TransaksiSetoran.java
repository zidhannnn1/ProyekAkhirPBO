package id.ac.kampus.ecotrash.model;

import id.ac.kampus.ecotrash.exception.TransaksiTidakValidException;
import id.ac.kampus.ecotrash.reward.SkemaReward;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Agregat transaksi setoran sampah nasabah.
 * Mengelola komposisi DetailSetoran dan siklus StatusTransaksi.
 */
public class TransaksiSetoran {

    private final String kodeTransaksi;
    private final NasabahWarga nasabah;
    private final LocalDate tanggalSetor;
    private StatusTransaksi status;
    private SkemaReward skemaDiterapkan;
    private final List<DetailSetoran> daftarItem;

    public TransaksiSetoran(String kodeTransaksi, NasabahWarga nasabah,
                            LocalDate tanggalSetor) {
        if (kodeTransaksi == null || kodeTransaksi.isBlank()) {
            throw new IllegalArgumentException("kodeTransaksi tidak boleh kosong");
        }
        if (nasabah == null) {
            throw new IllegalArgumentException("Nasabah tidak boleh null");
        }
        this.kodeTransaksi = kodeTransaksi;
        this.nasabah = nasabah;
        this.tanggalSetor = (tanggalSetor != null) ? tanggalSetor : LocalDate.now();
        this.status = StatusTransaksi.DIPROSES;
        this.daftarItem = new ArrayList<>();
    }

    /** Membuat DetailSetoran baru secara internal (Composition pattern). */
    public void tambahItem(KategoriSampah kategori, double kuantitas,
                           String catatan) throws TransaksiTidakValidException {
        if (kategori == null) {
            throw new IllegalArgumentException("Kategori sampah tidak boleh null");
        }
        if (kuantitas <= 0) {
            throw new IllegalArgumentException("Kuantitas timbangan harus lebih besar dari 0");
        }
        daftarItem.add(new DetailSetoran(kategori, kuantitas, catatan));
    }

    public double hitungTotalNilai() {
        return daftarItem.stream()
                .mapToDouble(DetailSetoran::getSubtotal)
                .sum();
    }

    public double hitungTotalBerat() {
        return daftarItem.stream()
                .mapToDouble(DetailSetoran::getKuantitasTimbangan)
                .sum();
    }

    /**
     * Menyelesaikan transaksi: menghitung reward via skema, lalu
     * mengubah status menjadi SELESAI. Gagal jika belum ada item.
     */
    public void selesaikanTransaksi(SkemaReward skema) throws TransaksiTidakValidException {
        if (this.status != StatusTransaksi.DIPROSES) {
            throw new TransaksiTidakValidException(kodeTransaksi,
                    "Transaksi sudah berstatus " + this.status);
        }
        if (daftarItem.isEmpty()) {
            throw new TransaksiTidakValidException(kodeTransaksi,
                    "Transaksi tidak memiliki item setoran.");
        }
        if (skema == null) {
            throw new IllegalArgumentException("SkemaReward tidak boleh null");
        }
        skema.terapkanReward(this.nasabah, hitungTotalNilai());
        this.skemaDiterapkan = skema;
        this.status = StatusTransaksi.SELESAI;
    }

    public void batalkan() throws TransaksiTidakValidException {
        if (this.status == StatusTransaksi.DIBATALKAN) {
            throw new TransaksiTidakValidException(kodeTransaksi,
                    "Transaksi sudah berstatus DIBATALKAN.");
        }
        this.status = StatusTransaksi.DIBATALKAN;
    }

    public String getKodeTransaksi() {
        return kodeTransaksi;
    }

    public NasabahWarga getNasabah() {
        return nasabah;
    }

    public LocalDate getTanggalSetor() {
        return tanggalSetor;
    }

    public StatusTransaksi getStatus() {
        return status;
    }

    public SkemaReward getSkemaDiterapkan() {
        return skemaDiterapkan;
    }

    public List<DetailSetoran> getDaftarItem() {
        return Collections.unmodifiableList(daftarItem);
    }
}
