package id.ac.kampus.ecotrash.model;

/**
 * Minyak jelantah bekas rumah tangga, diukur dalam Liter.
 * Kadar asam menentukan kualitas minyak yang disetor.
 */
public class MinyakJelantah extends KategoriSampah {

    private final double kadarAsam;

    public MinyakJelantah(String kodeSampah, double hargaBeliPerLiter,
                          double kadarAsam) {
        super(kodeSampah, "Minyak Jelantah Rumah Tangga", "Liter",
              hargaBeliPerLiter);
        this.kadarAsam = kadarAsam;
    }

    @Override
    public double hitungNilaiSubtotal(double volumeLiter) {
        if (volumeLiter <= 0) return 0;
        return volumeLiter * getHargaBeli();
    }

    public double getKadarAsam() { return kadarAsam; }
}
