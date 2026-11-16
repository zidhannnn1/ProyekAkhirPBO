package id.ac.kampus.ecotrash.reward;

import id.ac.kampus.ecotrash.model.NasabahWarga;

/**
 * Implementasi reward berupa saldo uang tunai yang dicatat pada buku tabungan nasabah.
 */
public class RewardTabunganRupiah implements SkemaReward, BisaCairkanTunai {

    @Override
    public void terapkanReward(NasabahWarga nasabah, double totalNominalRupiah) {
        if (nasabah != null && totalNominalRupiah > 0) {
            nasabah.tambahSaldo(totalNominalRupiah);
        }
    }

    @Override
    public boolean cairkanTunai(NasabahWarga nasabah, double nominal) {
        if (nasabah == null || nominal <= 0) return false;
        return nasabah.kurangiSaldo(nominal);
    }

    @Override
    public String getNamaSkema() {
        return "Tabungan Kas Rupiah";
    }

    @Override
    public String getKeterangan() {
        return "Nilai penjualan sampah 100% masuk ke saldo tabungan kas uang tunai.";
    }
}

