package id.ac.kampus.ecotrash.model;

/**
 * Satu baris item timbangan dalam transaksi setoran.
 * Merupakan Composition (◆) dari TransaksiSetoran — siklus hidup
 * ditentukan oleh transaksi induk; tidak pernah dibuat secara mandiri.
 */
public class DetailSetoran {

    private final KategoriSampah kategori;
    private double kuantitasTimbangan;
    private String catatanPilah;

    DetailSetoran(KategoriSampah kategori, double kuantitasTimbangan,
                  String catatanPilah) {
        if (kategori == null) {
            throw new IllegalArgumentException("Kategori sampah tidak boleh null");
        }
        if (kuantitasTimbangan <= 0) {
            throw new IllegalArgumentException("Kuantitas timbangan harus positif");
        }
        this.kategori = kategori;
        this.kuantitasTimbangan = kuantitasTimbangan;
        this.catatanPilah = (catatanPilah == null || catatanPilah.isBlank()) ? "Standar" : catatanPilah;
    }

    /** Memungkinkan TransaksiSetoran menggabungkan timbangan kategori yang sama. */
    void tambahKuantitas(double tambahan, String catatanBaru) {
        if (tambahan <= 0) {
            throw new IllegalArgumentException("Kuantitas tambahan harus positif");
        }
        this.kuantitasTimbangan += tambahan;
        if (catatanBaru != null && !catatanBaru.isBlank() && !catatanBaru.equals(this.catatanPilah)) {
            this.catatanPilah = this.catatanPilah + "; " + catatanBaru;
        }
    }

    public double getSubtotal() {
        return kategori.hitungNilaiSubtotal(kuantitasTimbangan);
    }

    public KategoriSampah getKategori() { return kategori; }
    public double getKuantitasTimbangan() { return kuantitasTimbangan; }
    public String getCatatanPilah() { return catatanPilah; }
}
