package id.ac.kampus.ecotrash.model;

import id.ac.kampus.ecotrash.reward.SkemaReward;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Transaksi penyetoran sampah oleh nasabah.
 * Memiliki Composition (◆) terhadap DetailSetoran — item hanya
 * dibuat melalui {@link #tambahItem} dan tidak bisa hidup di luar transaksi.
 */
public class TransaksiSetoran {

    private final String kodeTransaksi;
    private final NasabahWarga nasabah;
    private final LocalDate tanggalSetor;
    private String status;
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
        this.status = "DIPROSES";
        this.daftarItem = new ArrayList<>();
    }

    /** Membuat DetailSetoran baru secara internal (Composition pattern). */
    public void tambahItem(KategoriSampah kategori, double kuantitas,
                           String catatan) {
        if (kategori == null) {
            throw new IllegalArgumentException("Kategori sampah tidak boleh null");
        }
        if (kuantitas <= 0) {
            throw new IllegalArgumentException("Kuantitas timbangan harus lebih besar dari 0");
        }
        daftarItem.add(new DetailSetoran(kategori, kuantitas, catatan));
    }

    public double hitungTotalNilai() {
        double total = 0;
        for (DetailSetoran item : daftarItem) {
            total += item.getSubtotal();
        }
        return total;
    }

    public double hitungTotalBerat() {
        double total = 0;
        for (DetailSetoran item : daftarItem) {
            total += item.getKuantitasTimbangan();
        }
        return total;
    }

    /**
     * Menyelesaikan transaksi: menghitung reward via skema, lalu
     * mengubah status menjadi SELESAI. Gagal jika belum ada item.
     */
    public void selesaikanTransaksi(SkemaReward skema) {
        if (daftarItem.isEmpty()) {
            throw new IllegalStateException("Transaksi tidak memiliki item setoran");
        }
        if (skema == null) {
            throw new IllegalArgumentException("SkemaReward tidak boleh null");
        }
        skema.terapkanReward(this.nasabah, hitungTotalNilai());
        this.status = "SELESAI";
    }

    public String getKodeTransaksi() { return kodeTransaksi; }
    public NasabahWarga getNasabah() { return nasabah; }
    public LocalDate getTanggalSetor() { return tanggalSetor; }
    public String getStatus() { return status; }
    public List<DetailSetoran> getDaftarItem() {
        return Collections.unmodifiableList(daftarItem);
    }
}
