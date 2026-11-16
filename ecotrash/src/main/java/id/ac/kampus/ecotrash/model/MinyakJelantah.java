package id.ac.kampus.ecotrash.model;

/**
 * Spesialisasi limbah cair minyak jelantah rumah tangga yang diukur dalam satuan Liter.
 */
public class MinyakJelantah extends KategoriSampah {
    private double kadarAsam;

    public MinyakJelantah(String kodeSampah, double hargaBeliPerLiter, double kadarAsam) {
        super(kodeSampah, "Minyak Jelantah Rumah Tangga", hargaBeliPerLiter, "Liter");
        this.kadarAsam = kadarAsam;
    }

    @Override
    public double hitungNilaiSubtotal(double volumeLiter) {
        if (volumeLiter <= 0) return 0.0;
        return volumeLiter * getHargaBeli();
    }

    public double getKadarAsam() {
        return kadarAsam;
    }
}

