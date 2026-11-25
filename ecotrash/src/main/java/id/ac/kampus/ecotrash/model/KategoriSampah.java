package id.ac.kampus.ecotrash.model;

import java.util.Objects;

/**
 * Basis abstrak untuk kategori sampah yang diterima bank sampah.
 * Mengimplementasikan equals() dan hashCode() berbasis kodeSampah (atribut immutable)
 * agar aman digunakan pada Set maupun sebagai kunci Map.
 */
public abstract class KategoriSampah {

    private final String kodeSampah;
    private final String namaKategori;
    private final String satuan;
    private double hargaBeliPerSatuan;

    protected KategoriSampah(String kodeSampah, String namaKategori,
                             String satuan, double hargaBeliPerSatuan) {
        if (kodeSampah == null || kodeSampah.isBlank()) {
            throw new IllegalArgumentException("kodeSampah tidak boleh kosong");
        }
        if (namaKategori == null || namaKategori.isBlank()) {
            throw new IllegalArgumentException("namaKategori tidak boleh kosong");
        }
        if (satuan == null || satuan.isBlank()) {
            throw new IllegalArgumentException("satuan tidak boleh kosong");
        }
        if (hargaBeliPerSatuan <= 0) {
            throw new IllegalArgumentException("Harga beli harus lebih besar dari 0");
        }
        this.kodeSampah = kodeSampah;
        this.namaKategori = namaKategori;
        this.satuan = satuan;
        this.hargaBeliPerSatuan = hargaBeliPerSatuan;
    }

    public abstract double hitungNilaiSubtotal(double kuantitas);

    public void setHargaBeli(double hargaBaru) {
        if (hargaBaru <= 0) {
            throw new IllegalArgumentException("Harga beli baru harus positif");
        }
        this.hargaBeliPerSatuan = hargaBaru;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KategoriSampah other)) return false;
        return Objects.equals(kodeSampah, other.kodeSampah);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kodeSampah);
    }

    public String getKodeSampah() { return kodeSampah; }
    public String getNamaKategori() { return namaKategori; }
    public double getHargaBeli() { return hargaBeliPerSatuan; }
    public String getSatuan() { return satuan; }
}
