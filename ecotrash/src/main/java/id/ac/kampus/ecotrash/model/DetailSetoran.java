package id.ac.kampus.ecotrash.model;

/**
 * Komponen penyusun satu baris penimbangan dalam transaksi setoran.
 * Terikat dalam hubungan Komposisi penuh (◆) dengan TransaksiSetoran.
 */
public class DetailSetoran {
    private KategoriSampah kategori;
    private double kuantitasTimbangan;
    private String catatanPilah;

    public DetailSetoran(KategoriSampah kategori, double kuantitasTimbangan, String catatanPilah) {
        if (kategori == null) {
            throw new IllegalArgumentException("Kategori sampah tidak boleh null.");
        }
        if (kuantitasTimbangan <= 0) {
            throw new IllegalArgumentException("Kuantitas timbangan harus bernilai lebih dari 0.");
        }
        this.kategori = kategori;
        this.kuantitasTimbangan = kuantitasTimbangan;
        this.catatanPilah = catatanPilah != null ? catatanPilah : "Kondisi Standar";
    }

    public double getSubtotal() {
        return kategori.hitungNilaiSubtotal(kuantitasTimbangan);
    }

    public KategoriSampah getKategori() {
        return kategori;
    }

    public double getKuantitasTimbangan() {
        return kuantitasTimbangan;
    }

    public String getCatatanPilah() {
        return catatanPilah;
    }
}

