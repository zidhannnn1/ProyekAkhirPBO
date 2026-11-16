package id.ac.kampus.ecotrash.model;

/**
 * Spesialisasi kategori sampah anorganik padat (botol plastik, kardus, kaleng, dll).
 */
public class SampahAnorganik extends KategoriSampah {
    private String gradePilah; // Misal: "A (Bersih)", "B (Campur)"

    public SampahAnorganik(String kodeSampah, String namaKategori, double hargaBeliPerKg, String gradePilah) {
        super(kodeSampah, namaKategori, hargaBeliPerKg, "Kg");
        this.gradePilah = gradePilah;
    }

    @Override
    public double hitungNilaiSubtotal(double beratKg) {
        if (beratKg <= 0) return 0.0;
        return beratKg * getHargaBeli();
    }

    public String getGradePilah() {
        return gradePilah;
    }
}

