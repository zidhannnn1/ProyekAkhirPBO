package id.ac.kampus.ecotrash.reward;

import id.ac.kampus.ecotrash.model.NasabahWarga;

/**
 * Kontrak konversi nilai rupiah hasil setoran menjadi bentuk reward.
 * Setiap implementasi mendefinisikan mekanisme reward yang berbeda (OCP).
 */
public interface SkemaReward {
    void terapkanReward(NasabahWarga nasabah, double totalNominalRupiah);
    String getNamaSkema();
    String getKeterangan();
}
