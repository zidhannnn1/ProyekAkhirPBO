package id.ac.kampus.ecotrash.model;

/**
 * Sampah anorganik padat (plastik, kardus, logam) yang ditimbang dalam Kg.
 * Grade pilah menentukan kualitas pemilahan oleh nasabah.
 */
public class SampahAnorganik extends KategoriSampah {

    private final String gradePilah;

    public SampahAnorganik(String kodeSampah, String namaKategori,
                           double hargaBeliPerKg, String gradePilah) {
        super(kodeSampah, namaKategori, "Kg", hargaBeliPerKg);
        this.gradePilah = gradePilah;
    }

    @Override
    public double hitungNilaiSubtotal(double beratKg) {
        if (beratKg <= 0) return 0;
        return beratKg * getHargaBeli();
    }

    public String getGradePilah() { return gradePilah; }
}
