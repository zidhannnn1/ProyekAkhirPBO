package id.ac.kampus.ecotrash.model;

/**
 * Basis abstrak untuk kategori sampah yang diterima bank sampah.
 * Setiap kategori memiliki kode unik, harga beli per satuan, dan
 * cara hitung subtotal yang spesifik per jenis sampah.
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
        if (hargaBeliPerSatuan < 0) {
            throw new IllegalArgumentException("Harga beli tidak boleh negatif");
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

    public String getKodeSampah() { return kodeSampah; }
    public String getNamaKategori() { return namaKategori; }
    public double getHargaBeli() { return hargaBeliPerSatuan; }
    public String getSatuan() { return satuan; }
}
