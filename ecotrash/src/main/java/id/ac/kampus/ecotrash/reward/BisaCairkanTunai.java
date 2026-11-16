package id.ac.kampus.ecotrash.reward;

import id.ac.kampus.ecotrash.model.NasabahWarga;

/**
 * Interface kapabilitas untuk reward yang mendukung penarikan uang tunai.
 * Menerapkan prinsip Liskov Substitution Principle (LSP) dan Interface Segregation Principle (ISP).
 */
public interface BisaCairkanTunai {
    boolean cairkanTunai(NasabahWarga nasabah, double nominal);
}

