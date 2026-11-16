package id.ac.kampus.ecotrash.reward;

import id.ac.kampus.ecotrash.model.NasabahWarga;

/**
 * Interface polimorfis untuk mengonversi nilai sampah menjadi insentif bagi nasabah.
 * Menerapkan prinsip Open/Closed Principle (OCP).
 */
public interface SkemaReward {
    void terapkanReward(NasabahWarga nasabah, double totalNominalRupiah);
    String getNamaSkema();
    String getKeterangan();
}

