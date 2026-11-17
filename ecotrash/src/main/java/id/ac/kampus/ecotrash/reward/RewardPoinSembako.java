package id.ac.kampus.ecotrash.reward;

import id.ac.kampus.ecotrash.model.NasabahWarga;

/**
 * Skema reward berupa poin belanja sembako.
 * Tidak mengimplementasi BisaCairkanTunai karena poin bukan uang tunai —
 * ini mendemonstrasikan kepatuhan terhadap LSP dan ISP.
 */
public class RewardPoinSembako implements SkemaReward {

    // Setiap Rp 1.000 setoran dikonversi menjadi 1 poin
    public static final int KONVERSI_RUPIAH_PER_POIN = 1000;

    @Override
    public void terapkanReward(NasabahWarga nasabah, double totalNominalRupiah) {
        if (nasabah == null) {
            throw new IllegalArgumentException("Nasabah tidak boleh null.");
        }
        int poin = (int) (totalNominalRupiah / KONVERSI_RUPIAH_PER_POIN);
        if (poin > 0) {
            nasabah.tambahPoin(poin);
        }
    }

    @Override
    public String getNamaSkema() {
        return "Poin Belanja Sembako RT";
    }

    @Override
    public String getKeterangan() {
        return "Setiap Rp " + KONVERSI_RUPIAH_PER_POIN + " nilai setoran dikonversi menjadi 1 poin sembako.";
    }
}
