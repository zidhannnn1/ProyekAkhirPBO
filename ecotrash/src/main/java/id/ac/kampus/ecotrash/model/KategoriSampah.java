package id.ac.kampus.ecotrash.model;

/**
 * Kelas abstrak induk untuk seluruh komoditas sampah yang diterima di bank sampah.
 */
public abstract class KategoriSampah {
    private String kodeSampah;
    private String namaKategori;
    private double hargaBeliPerSatuan;
    private String satuan;

    public KategoriSampah(String kodeSampah, String namaKategori, double hargaBeliPerSatuan, String satuan) {
        if (kodeSampah == null || kodeSampah.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode sampah tidak boleh kosong.");
        }
        if (hargaBeliPerSatuan < 0) {
            throw new IllegalArgumentException("Harga beli per satuan tidak boleh bernilai negatif.");
        }
        this.kodeSampah = kodeSampah;
        this.namaKategori = namaKategori;
        this.hargaBeliPerSatuan = hargaBeliPerSatuan;
        this.satuan = satuan;
    }

    /**
     * Method abstrak untuk menghitung nilai rupiah subtotal berdasarkan jumlah kuantitas.
     */
    public abstract double hitungNilaiSubtotal(double kuantitas);

    public void setHargaBeli(double hargaBaru) {
        if (hargaBaru <= 0) {
            throw new IllegalArgumentException("Harga baru harus bernilai positif.");
        }
        this.hargaBeliPerSatuan = hargaBaru;
    }

    public String getKodeSampah() {
        return kodeSampah;
    }

    public String getNamaKategori() {
        return namaKategori;
    }

    public double getHargaBeli() {
        return hargaBeliPerSatuan;
    }

    public String getSatuan() {
        return satuan;
    }
}

