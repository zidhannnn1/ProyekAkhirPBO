package id.ac.kampus.ecotrash.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import id.ac.kampus.ecotrash.reward.SkemaReward;

/**
 * Entitas inti proses bisnis setoran sampah.
 * Memiliki hubungan Komposisi (◆) dengan DetailSetoran dan Asosiasi dengan NasabahWarga.
 */
public class TransaksiSetoran {
    private String kodeTransaksi;
    private NasabahWarga nasabah;
    private LocalDate tanggalSetor;
    private String status; // "DIPROSES", "SELESAI", "BATAL"
    private List<DetailSetoran> daftarItem;

    public TransaksiSetoran(String kodeTransaksi, NasabahWarga nasabah, LocalDate tanggalSetor) {
        if (kodeTransaksi == null || kodeTransaksi.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode transaksi wajib diisi.");
        }
        if (nasabah == null) {
            throw new IllegalArgumentException("Nasabah penyetor tidak boleh null.");
        }
        this.kodeTransaksi = kodeTransaksi;
        this.nasabah = nasabah;
        this.tanggalSetor = tanggalSetor != null ? tanggalSetor : LocalDate.now();
        this.status = "DIPROSES";
        this.daftarItem = new ArrayList<>();
    }

    /**
     * Membentuk objek DetailSetoran di dalam TransaksiSetoran (Karakteristik Komposisi).
     */
    public void tambahItem(KategoriSampah kategori, double kuantitas, String catatan) {
        if (kategori == null) {
            throw new IllegalArgumentException("Kategori tidak valid.");
        }
        if (kuantitas <= 0) {
            throw new IllegalArgumentException("Berat timbangan harus > 0.");
        }
        DetailSetoran detail = new DetailSetoran(kategori, kuantitas, catatan);
        this.daftarItem.add(detail);
    }

    public double hitungTotalNilai() {
        double total = 0.0;
        for (DetailSetoran item : daftarItem) {
            total += item.getSubtotal();
        }
        return total;
    }

    public double hitungTotalBerat() {
        double total = 0.0;
        for (DetailSetoran item : daftarItem) {
            total += item.getKuantitasTimbangan();
        }
        return total;
    }

    public void selesaikanTransaksi(SkemaReward skema) {
        if (daftarItem.isEmpty()) {
            throw new IllegalStateException("Transaksi tidak memiliki item sampah untuk diselesaikan.");
        }
        if (skema == null) {
            throw new IllegalArgumentException("Skema reward harus dipilih.");
        }
        double totalNominal = hitungTotalNilai();
        skema.terapkanReward(this.nasabah, totalNominal);
        this.status = "SELESAI";
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<DetailSetoran> getDaftarItem() {
        return Collections.unmodifiableList(daftarItem);
    }
}

