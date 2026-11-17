package id.ac.kampus.ecotrash.reward;

import id.ac.kampus.ecotrash.model.NasabahWarga;

/**
 * Kemampuan pencairan saldo tunai.
 * Dipisah dari SkemaReward agar skema non-tunai (seperti poin) tidak dipaksa
 * mengimplementasi operasi yang tidak relevan (ISP/LSP).
 */
public interface BisaCairkanTunai {
    boolean cairkanTunai(NasabahWarga nasabah, double nominal);
}
