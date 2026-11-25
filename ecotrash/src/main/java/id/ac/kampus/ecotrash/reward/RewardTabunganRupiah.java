package id.ac.kampus.ecotrash.reward;

import id.ac.kampus.ecotrash.exception.SaldoTidakCukupException;
import id.ac.kampus.ecotrash.model.NasabahWarga;

/**
 * Skema reward berupa tabungan kas rupiah yang bisa dicairkan tunai.
 */
public class RewardTabunganRupiah implements SkemaReward, BisaCairkanTunai {

    @Override
    public void terapkanReward(NasabahWarga nasabah, double totalNominalRupiah) {
        if (nasabah == null) {
            throw new IllegalArgumentException("Nasabah tidak boleh null.");
        }
        if (totalNominalRupiah <= 0) {
            return;
        }
        nasabah.tambahSaldo(totalNominalRupiah);
    }

    @Override
    public void cairkanTunai(NasabahWarga nasabah, double nominal) throws SaldoTidakCukupException {
        if (nasabah == null) {
            throw new IllegalArgumentException("Nasabah tidak boleh null.");
        }
        nasabah.kurangiSaldo(nominal);
    }

    @Override
    public String getNamaSkema() {
        return "Tabungan Kas Rupiah";
    }

    @Override
    public String getKeterangan() {
        return "Nilai setoran masuk ke saldo tabungan nasabah dan dapat dicairkan tunai.";
    }
}
