package id.ac.kampus.ecotrash.reward;

import id.ac.kampus.ecotrash.model.NasabahWarga;

/**
 * Implementasi reward berupa poin insentif yang dapat ditukarkan dengan beras, minyak, gula di RT.
 * Setiap Rp 1.000 bernilai 1 Poin Sembako.
 */
public class RewardPoinSembako implements SkemaReward {
    public static final int KONVERSI_RUPIAH_PER_POIN = 1000;

    @Override
    public void terapkanReward(NasabahWarga nasabah, double totalNominalRupiah) {
        if (nasabah != null && totalNominalRupiah > 0) {
            int poinDiperoleh = (int) (totalNominalRupiah / KONVERSI_RUPIAH_PER_POIN);
            if (poinDiperoleh > 0) {
                nasabah.tambahPoin(poinDiperoleh);
            }
        }
    }

    @Override
    public String getNamaSkema() {
        return "Poin Belanja Sembako RT";
    }

    @Override
    public String getKeterangan() {
        return "Dikonversi: Tiap Rp " + KONVERSI_RUPIAH_PER_POIN + " setoran bernilai 1 Poin Belanja Sembako.";
    }
}

